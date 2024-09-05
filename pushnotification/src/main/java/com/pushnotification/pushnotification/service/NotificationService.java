package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;

import java.util.Map;
import java.util.Set;

public interface NotificationService {
    void sendNotificationByTopics(Map<PlatformLanguages, NotificationDto> notificationsWithLanguages, Set<String> topics);
    void sendNotificationByUsers(Map<PlatformLanguages, NotificationDto> notificationsWithLanguages, Set<String> userCifs);
}
