package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.service.RabbitMQService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQServiceImpl implements RabbitMQService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${push-notification.rabbitmq.queueName}")
    private String queueName;

    public RabbitMQServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendNotificationMessage(PushNotificationDto notificationDto) {
        rabbitTemplate.convertAndSend(queueName, notificationDto);

        System.out.println("Message sent to RabbitMQ: " + notificationDto);
    }

    @Override
    @RabbitListener(queues = "${push-notification.rabbitmq.queueName}")
    public void receiveNotificationMessage(PushNotificationDto notificationDto) {
        System.out.println("RECEIVED MESSAGE: " + notificationDto);
    }
}
