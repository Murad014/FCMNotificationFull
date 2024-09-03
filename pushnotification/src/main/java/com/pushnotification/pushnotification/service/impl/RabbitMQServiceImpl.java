package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.dto.NotificationDto;
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
    public void sendNotificationMessage(NotificationDto notificationDto) {
        rabbitTemplate.convertAndSend(queueName, notificationDto);

        System.out.println("Message sent to RabbitMQ: " + notificationDto);
    }

    @Override
    @RabbitListener(queues = "${push-notification.rabbitmq.queueName}")
    public void receiveNotificationMessage(NotificationDto notificationDto) {
        System.out.println("RECIEVED MESSAGE: " + notificationDto);
    }
}
