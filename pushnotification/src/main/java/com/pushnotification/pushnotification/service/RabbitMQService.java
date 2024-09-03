package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.dto.NotificationDto;

public interface RabbitMQService {
    void sendNotificationMessage(NotificationDto message);
    void receiveNotificationMessage(NotificationDto message);
}
