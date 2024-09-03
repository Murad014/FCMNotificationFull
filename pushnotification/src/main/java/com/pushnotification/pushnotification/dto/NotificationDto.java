package com.pushnotification.pushnotification.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @Valid
    @NotNull(message = "{notification.title.input.error}")
    @NotEmpty(message = "{notification.title.input.error}")
    String title;

    String body;
    String imageUrl;
    String iconUrl;
    String subtitle;
    boolean sentToMq;
    boolean sentToFcm;
}
