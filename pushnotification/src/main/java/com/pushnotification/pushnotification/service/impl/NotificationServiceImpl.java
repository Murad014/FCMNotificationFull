package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.entity.NotificationEntity;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.exceptions.WrongRequestBodyException;
import com.pushnotification.pushnotification.helpers.MessageSourceReaderHelper;
import com.pushnotification.pushnotification.repository.NotificationRepository;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.NotificationService;
import com.pushnotification.pushnotification.service.RabbitMQService;
import com.pushnotification.pushnotification.service.TopicService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final TopicService topicService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RabbitMQService rabbitMQService;
    private final MessageSourceReaderHelper messageSourceReaderHelper;

    private static final String ENTER_TOPICS_MESSAGE = "topics.cannot.be.null.or.empty";
    private static final String ENTER_USERS_CIFS_MESSAGE = "users.cifs.cannot.be.null.or.empty";
    private static final String MISSING_LANGUAGE_MESSAGE = "missing.language.key.error";

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   TopicService topicService,
                                   ModelMapper modelMapper,
                                   UserRepository userRepository,
                                   RabbitMQService rabbitMQService,
                                   MessageSourceReaderHelper messageSourceReaderHelper) {

        this.notificationRepository = notificationRepository;
        this.topicService = topicService;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.rabbitMQService = rabbitMQService;
        this.messageSourceReaderHelper = messageSourceReaderHelper;
    }


    @Override
    @Transactional
    public void saveAndSendNotificationByTopics(PushNotificationDto pushNotificationDto,
                                                Set<String> givenTopics) {
        if(givenTopics == null || givenTopics.isEmpty()) {
            var message = messageSourceReaderHelper.getMessage(ENTER_TOPICS_MESSAGE);
            throw new WrongRequestBodyException(message);
        }

        var notificationsWithLanguages = pushNotificationDto.getLangAndNotification();
        checkLanguagesKeys(notificationsWithLanguages);

        Set<TopicEntity> allGivenTopicsTopicsFromDB = checkAllGivenTopicsExistInDBThenReturnTopicsInDB(givenTopics);

        // Save
        List<NotificationEntity> bulkSave = new ArrayList<>();
        for(var language: PlatformLanguages.values()) {
            Set<String> getTopicsForSpecLanguage = allGivenTopicsTopicsFromDB.stream()
                    .filter(topic -> topic.getName().endsWith("_" + language))
                    .collect(Collectors.toSet())
                    .stream()
                    .map(TopicEntity:: getName)
                    .collect(Collectors.toSet());

            // Get Users that belongs given Topics
            List<UserEntity> usersForGivenTopics = userRepository
                    .findAllByTopics_NameIn(new ArrayList<>(getTopicsForSpecLanguage));
            var notificationEntity = modelMapper
                    .map(notificationsWithLanguages.get(language), NotificationEntity.class);

            notificationEntity.setUsers(new HashSet<>(usersForGivenTopics));
            notificationEntity.setTopics(new HashSet<>(allGivenTopicsTopicsFromDB));
            notificationEntity.setSentToMq(true);

            bulkSave.add(notificationEntity);
        }
        notificationRepository.saveAll(bulkSave);

        // Send
        rabbitMQService.sendNotificationMessage(pushNotificationDto);
    }

    @Override
    @Transactional
    public void  sendNotificationByUsers(PushNotificationDto pushNotificationDto, Set<String> userCifs) {
        if(userCifs == null || userCifs.isEmpty()) {
            var message = messageSourceReaderHelper.getMessage(ENTER_USERS_CIFS_MESSAGE);
            throw new WrongRequestBodyException(message);
        }

        var notificationsWithLanguages = pushNotificationDto.getLangAndNotification();
        checkLanguagesKeys(notificationsWithLanguages);

        var getAllUsersByCifFromDB = userRepository.findAllByCifIn(new ArrayList<>(userCifs));
        checkThatAllUsersExistInDB(userCifs, getAllUsersByCifFromDB);

        // Set notifications as sent mq true and save
        var bulkSave = new ArrayList<NotificationEntity>();
        for(var language: PlatformLanguages.values()) {
            var notification = pushNotificationDto.getLangAndNotification().get(language);
            var toEntity = modelMapper.map(notification, NotificationEntity.class);
            toEntity.setSentToMq(true);
            bulkSave.add(toEntity);
        }
        notificationRepository.saveAll(bulkSave);

        // Send
        rabbitMQService.sendNotificationMessage(pushNotificationDto);
    }


    @Override
    public Set<NotificationDto> fetchNotificationsByUserCif(String cif) {
        userRepository.findByCif(cif).orElseThrow(
                () -> new ResourceNotFoundException("User", "cif", cif)
        );

        return notificationRepository.findAllByUsers_Cif(cif).stream().map(notificationEntity ->
                modelMapper.map(notificationEntity, NotificationDto.class))
                .collect(Collectors.toSet());
    }

    private void checkThatAllUsersExistInDB(Set<String> userCifs, List<UserEntity> getAllUsersByCifFromDB) {
        Set<String> copy = new HashSet<>(userCifs);
        Set<String> getNames = getAllUsersByCifFromDB
                .stream()
                .map(UserEntity:: getCif)
                .collect(Collectors.toSet());

        copy.removeAll(getNames); // Eliminate

        if( ! copy.isEmpty())
            throw new ResourceNotFoundException("Users", "cifs", copy.toString());

    }

    private Set<TopicEntity> checkAllGivenTopicsExistInDBThenReturnTopicsInDB(Set<String> givenTopics) {
        Set<String> givenTopicsForAllLanguages = new HashSet<>();

        for(var language: PlatformLanguages.values()) {
            givenTopics
                    .forEach(topic -> {
                        var topicNameWithLang = topic.toUpperCase().concat("_" + language);

                        givenTopicsForAllLanguages.add(topicNameWithLang);
                    });
        }

        var topicListFromDB = topicService.findAllTopicsInGivenTopicList(givenTopicsForAllLanguages);
        topicService.checkAllGivenTopicsInDB(givenTopicsForAllLanguages, topicListFromDB);

        return topicListFromDB;
    }

    private void checkLanguagesKeys(Map<PlatformLanguages, NotificationDto> notificationsWithLanguages) {

        for (var lang : PlatformLanguages.values()) {
            if(!notificationsWithLanguages.containsKey(lang)) {
                var message = messageSourceReaderHelper.getMessage(MISSING_LANGUAGE_MESSAGE);
                throw new WrongRequestBodyException(message.concat(" " + lang.toString()));
            }
        }
    }

}
