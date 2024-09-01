package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.dto.NotificationDto;

public interface NotificationService {
    NotificationDto savedNotification(NotificationDto notification);
}
