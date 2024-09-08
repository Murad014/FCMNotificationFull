package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NotificationService {
    void saveAndSendNotificationByTopics(PushNotificationDto pushNotificationDto,
                                         Set<String> topics);

    void sendNotificationByUsers(PushNotificationDto pushNotificationDto,
                                 Set<String> userCifs);

    Set<NotificationDto> fetchNotificationsByUserCif(String cif);

}
