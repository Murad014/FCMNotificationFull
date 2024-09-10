package com.pushnotification.pushnotification.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.service.FCMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FCMServiceImpl implements FCMService {

    public FCMServiceImpl() {
    }

    @Override
    public String sendNotificationToTopic(Set<String> topics, NotificationDto notification) {
        try {
            String condition = topics.stream()
                    .map(topic -> "'" + topic + "' in topics")
                    .collect(Collectors.joining(" || "));

            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(notification.getTitle())
                            .setBody(notification.getBody())
                            .setImage(notification.getImageUrl())
                            .build())
                    .setCondition(condition)  // Use condition to target multiple topics
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);

            log.info("Notification send response: {}", response);

            return response;
        } catch (Exception e) {
            log.error("Error occurred when sending notification to FCM: {}", e.getMessage());
            return null;
        }
    }


    @Override
    public String sendNotificationToUserByToken(String token, NotificationDto notification) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(notification.getTitle())
                            .setBody(notification.getBody())
                            .setImage(notification.getImageUrl())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);

            log.info("Notification send response by tokens: {}", response);

            return response;
        } catch (Exception e) {
            return null;
        }
    }

}
