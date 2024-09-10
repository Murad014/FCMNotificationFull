package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.dto.NotificationDto;
import java.util.Set;

public interface FCMService {
    String sendNotificationToTopic(Set<String> topics, NotificationDto notification);
    String sendNotificationToUserByToken(String token, NotificationDto notificationDto);
}