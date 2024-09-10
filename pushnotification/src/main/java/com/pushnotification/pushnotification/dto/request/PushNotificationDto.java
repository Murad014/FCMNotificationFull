package com.pushnotification.pushnotification.dto.request;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PushNotificationDto {
    Set<String> topics;
    Set<String> users;


    @Valid
    @NotEmpty
    Map<PlatformLanguages, NotificationDto> langAndNotification = new HashMap<>();
}
