package com.pushnotification.pushnotification.config;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@TestMethodOrder(MethodOrderer.class)
public class ApplicationPropertiesTest {
    @Autowired
    public ApplicationProperties applicationProperties;



    @Test
    @Order(1)
    @DisplayName("Check Property Fields")
    public void loadedApplicationProperties_whenLoad_thenSetValuesToFields(){
        assertNotNull(applicationProperties.getFirebaseConfigPath());
        assertNotNull(applicationProperties.getRabbitMQQueueName());
        assertNotNull(applicationProperties.getRabbitMQDirectExchange());
        assertNotNull(applicationProperties.getRabbitMQRoutingKey());
        assertNotNull(applicationProperties.getBroadcastStartHour());
        assertNotNull(applicationProperties.getBroadcastStartMinute());
        assertNotNull(applicationProperties.getBroadcastEndHour());
        assertNotNull(applicationProperties.getBroadcastEndMinute());
    }


}