package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.entity.NotificationEntity;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.exceptions.WrongRequestBodyException;
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

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   TopicService topicService,
                                   ModelMapper modelMapper,
                                   UserRepository userRepository,
                                   RabbitMQService rabbitMQService) {

        this.notificationRepository = notificationRepository;
        this.topicService = topicService;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.rabbitMQService = rabbitMQService;
    }


    @Override
    @Transactional
    public void saveAndSendNotificationByTopics(PushNotificationDto pushNotificationDto,
                                                Set<String> givenTopics) {
        var notificationsWithLanguages = pushNotificationDto.getLangAndNotification();
        checkLanguagesKeys(notificationsWithLanguages);
        Set<TopicEntity> allTopicsFromDB = checkAllGivenTopicsExistInDBThenReturnTopicsInDB(givenTopics);

        // Save
        List<NotificationEntity> bulkSave = new ArrayList<>();
        for(var language: PlatformLanguages.values()) {
            Set<String> getTopicsForSpecLanguage = allTopicsFromDB.stream()
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

            bulkSave.add(notificationEntity);
        }
        notificationRepository.saveAll(bulkSave);

        // Send
        rabbitMQService.sendNotificationMessage(pushNotificationDto);

    }

    @Override
    public void sendNotificationByUsers(PushNotificationDto pushNotificationDto,
                                        Set<String> userCifs) {
        checkLanguagesKeys(pushNotificationDto.getLangAndNotification());

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
            if(!notificationsWithLanguages.containsKey(lang))
                throw new WrongRequestBodyException("Language " + lang + " is missing!");
        }
    }
}
