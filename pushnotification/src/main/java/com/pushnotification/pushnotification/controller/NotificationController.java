package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.FCMService;
import com.pushnotification.pushnotification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final GenerateResponseHelper generateResponseHelper;

    @Autowired
    public NotificationController(NotificationService notificationService,
                                  GenerateResponseHelper generateResponseHelper) {
        this.notificationService = notificationService;
        this.generateResponseHelper = generateResponseHelper;
    }

    @PostMapping("/send/topics")
    public ResponseEntity<ResponseDto<Void>> sendMessage(@RequestBody PushNotificationDto pushNotificationDto) {
        notificationService.saveAndSendNotificationByTopics(pushNotificationDto,
                pushNotificationDto.getTopics());

        return buildResponse(HttpStatus.OK, "notification.send.successfully", null);
    }

    @GetMapping("/user/{cif}")
    public ResponseEntity<ResponseDto<Set<NotificationDto>>> getAllNotifications(@PathVariable("cif") String cif) {
        var response = notificationService.fetchNotificationsByUserCif(cif);
        return buildResponse(HttpStatus.OK, "notification.fetch.successfully", response);
    }

    @PostMapping("/send/users")
    public String sendNotification(@RequestBody PushNotificationDto pushNotificationDto) {
        notificationService.sendNotificationByUsers(pushNotificationDto,
                pushNotificationDto.getUsers());

        return "SUCCESS";
    }

    private <D> ResponseEntity<ResponseDto<D>> buildResponse(HttpStatus status, String messageKey, D data) {
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri();

        var response = generateResponseHelper.generateResponse(status.value(), messageKey, data,
                location.getPath());

        return new ResponseEntity<>(response, status);
    }

}