package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.FCMService;
import com.pushnotification.pushnotification.service.RabbitMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RabbitMQServiceImpl implements RabbitMQService {
    private final RabbitTemplate rabbitTemplate;
    private final FCMService fcmService;
    private final UserRepository userRepository;

    @Value("${push-notification.rabbitmq.queueName}")
    private String queueName;

    @Autowired
    public RabbitMQServiceImpl(RabbitTemplate rabbitTemplate, FCMService fcmService, UserRepository userRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.fcmService = fcmService;
        this.userRepository = userRepository;
    }

    @Override
    public void sendNotificationMessage(PushNotificationDto notificationDto) {
        rabbitTemplate.convertAndSend(queueName, notificationDto);

        log.info("RabbitMQ has sent message: {}", notificationDto);
    }

    @Override
    @RabbitListener(queues = "${push-notification.rabbitmq.queueName}")
    public void receiveNotificationMessage(PushNotificationDto notificationDto) {
        log.info("RabbitMq Subscriber has get message: {}", notificationDto);
        log.info("Message is sending to FCM: {}", notificationDto);

        if(notificationDto.getTopics() != null)
            sendNotificationByTopics(notificationDto);
        else if(notificationDto.getUsers() != null)
            sendNotificationByUsersTokens(notificationDto);

        log.info("receiveNotificationMessage ended.");

    }


    private void sendNotificationByTopics(PushNotificationDto notificationDto){
        for(var language: PlatformLanguages.values()) {
            var sendTopics = new HashSet<String>();
            notificationDto.getTopics().forEach(topic -> {
                var topicWithLanguage = topic.toUpperCase().concat("_" + language);
                sendTopics.add(topicWithLanguage);
            });
            var response = fcmService
                    .sendNotificationToTopic(sendTopics, notificationDto.getLangAndNotification().get(language));

            log.info("Response: {}", response);
        }
    }

    private void sendNotificationByUsersTokens(PushNotificationDto notificationDto) {
        var allUsersFromDB = userRepository.findAllByCifIn(new ArrayList<>(notificationDto.getUsers()));

        var unsuccessfulSentCifs = new ArrayList<String>();
        for(var userEntity: allUsersFromDB){
            PlatformLanguages lang = userEntity.getPlatformLanguage();
            var response = fcmService
                    .sendNotificationToUserByToken(userEntity.getToken(),
                            notificationDto.getLangAndNotification().get(lang));

            if(response == null) // Unsuccessful
                unsuccessfulSentCifs.add(userEntity.getCif());
        }

        log.error("Unsuccessful sending notification to FCM: {}", unsuccessfulSentCifs);

    }
}
