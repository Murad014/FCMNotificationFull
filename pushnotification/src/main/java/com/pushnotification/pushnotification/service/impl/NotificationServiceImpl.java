package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.entity.NotificationEntity;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.exceptions.WrongRequestBodyException;
import com.pushnotification.pushnotification.repository.NotificationRepository;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.service.NotificationService;
import com.pushnotification.pushnotification.service.TopicService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final TopicService topicService;
    private final TopicRepository topicRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   TopicService topicService,
                                   TopicRepository topicRepository, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.topicService = topicService;
        this.topicRepository = topicRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public void sendNotificationByTopics(Map<PlatformLanguages, NotificationDto> notificationsWithLanguages,
                                         Set<String> topics) {

        checkLanguagesKeys(notificationsWithLanguages);

        var withLanguages = new ArrayList<String>();
        for(var lang: PlatformLanguages.values())
            topics.forEach(topic -> withLanguages.add(topic.concat("_" + lang).toUpperCase()) );

        var getAllTopicsWithLang = topicService.findAllTopicsInGivenTopicList(new HashSet<>(withLanguages));
        checkAllGivenTopicsExistInDB(new HashSet<>(withLanguages), getAllTopicsWithLang);

        // Set Notification topics
        var bulkSaveNotification = new ArrayList<NotificationEntity>();

        for(var notificationMap : notificationsWithLanguages.entrySet()) {
            var notificationEntity = modelMapper.map(notificationMap.getValue(), NotificationEntity.class);

            getAllTopicsWithLang.forEach(topic -> {
                if (topic.getName().endsWith(notificationMap.getKey().toString()))
                    notificationEntity.getTopics().add(topic);
            });
            bulkSaveNotification.add(notificationEntity);
        }

        notificationRepository.saveAll(bulkSaveNotification);
    }

    @Override
    public void sendNotificationByUsers(Map<PlatformLanguages, NotificationDto> notificationsWithLanguages,
                                        Set<String> userCifs) {
        checkLanguagesKeys(notificationsWithLanguages);
    }


    private void checkAllGivenTopicsExistInDB(Set<String> givenTopics, Set<TopicEntity> topicEntitySet) {
        topicService.checkAllGivenTopicsInDB(givenTopics, topicEntitySet);
    }

    private void checkLanguagesKeys(Map<PlatformLanguages, NotificationDto> notificationsWithLanguages) {
        for (var lang : PlatformLanguages.values()) {
            if(!notificationsWithLanguages.containsKey(lang))
                throw new WrongRequestBodyException("Language " + lang + " is not supported");
        }
    }
}
