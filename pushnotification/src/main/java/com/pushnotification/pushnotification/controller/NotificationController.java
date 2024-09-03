package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.service.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final RabbitMQService rabbitMQService;

    @Autowired
    public NotificationController(RabbitMQService rabbitMQService) {
        this.rabbitMQService = rabbitMQService;
    }

    @GetMapping
    public String sendMessage(){
        var notificationDto = new NotificationDto();
        notificationDto.setTitle("Titlooo");
        notificationDto.setBody("Bodyoooo");
        rabbitMQService.sendNotificationMessage(notificationDto);

        return "SUCCESS";
    }

}