package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.service.NotificationService;
import com.pushnotification.pushnotification.service.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send/topics")
    public ResponseEntity<?> sendMessage(@RequestBody PushNotificationDto pushNotificationDto) {
        notificationService.saveAndSendNotificationByTopics(pushNotificationDto,
                pushNotificationDto.getTopics());

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("/user/{cif}")
    public ResponseEntity<Set<NotificationDto>> getAllNotifications(@PathVariable("cif") String cif) {
        return new ResponseEntity<>(notificationService.fetchNotificationsByUserCif(cif), HttpStatus.OK);
    }
}