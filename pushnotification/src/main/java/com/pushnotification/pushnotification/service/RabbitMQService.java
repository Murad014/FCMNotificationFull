package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;

public interface RabbitMQService {
    void sendNotificationMessage(PushNotificationDto message);
    void receiveNotificationMessage(PushNotificationDto message);
}
