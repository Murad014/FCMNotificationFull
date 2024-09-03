package com.pushnotification.pushnotification.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class NotificationDto {
    Long id;
    String title;
    String body;
    String imageUrl;
    String iconUrl;
    String subtitle;
    boolean sentToMq;
    boolean sentToFcm;
}
