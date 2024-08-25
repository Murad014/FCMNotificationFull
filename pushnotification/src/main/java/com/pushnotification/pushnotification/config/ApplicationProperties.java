package com.pushnotification.pushnotification.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class ApplicationProperties {
    @Value("${api.firebase-configuration-file}")
    String firebaseConfigPath;
    @Value("${push-notification.rabbitmq.queueName}")
    String rabbitMQQueueName;
    @Value("${push-notification.rabbitmq.exchange}")
    String rabbitMQDirectExchange;
    @Value("${push-notification.rabbitmq.routingKey}")
    String rabbitMQRoutingKey;
    @Value("${broadcast-time.start-hour}")
    Integer broadcastStartHour;
    @Value("${broadcast-time.start-minute}")
    Integer broadcastStartMinute;
    @Value("${broadcast-time.end-hour}")
    Integer broadcastEndHour;
    @Value("${broadcast-time.end-minute}")
    Integer broadcastEndMinute;

}