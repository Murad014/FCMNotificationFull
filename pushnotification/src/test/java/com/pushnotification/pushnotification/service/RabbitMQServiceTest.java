package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.impl.RabbitMQServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@SpringBootTest
public class RabbitMQServiceTest {

    @Mock
    private NotificationService notificationServiceImpl;
    @Mock
    private FCMService fcmServiceImpl;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RabbitMQServiceImpl rabbitMQService;


    @Test
    void testReceiveNotificationMessage_WithTopics() {
        // Arrange
        PushNotificationDto pushNotificationDto = new PushNotificationDto();
        var notificationDto = new NotificationDto();
        notificationDto.setTitle("TEST");
        notificationDto.setBody("TEST BODY");

        pushNotificationDto.getLangAndNotification().put(PlatformLanguages.AZ, notificationDto);
        pushNotificationDto.setTopics(Set.of("topic1", "topic2")); // Simulate topics in the DTO

        // When
        when(fcmServiceImpl.sendNotificationToTopic(
                eq(pushNotificationDto.getTopics()),
                eq(pushNotificationDto.getLangAndNotification().get(PlatformLanguages.AZ))
        )).thenReturn("success");


        // Act
        rabbitMQService.receiveNotificationMessage(pushNotificationDto);

        // Assert
        verify(fcmServiceImpl, times(1)).sendNotificationToTopic(
                anySet(),
                eq(pushNotificationDto.getLangAndNotification().get(PlatformLanguages.AZ)));
    }

    @Test
    void testReceiveNotificationMessage_WithUsers() {
        // Arrange
        PushNotificationDto pushNotificationDto = new PushNotificationDto();
        var notificationDto = new NotificationDto();
        notificationDto.setTitle("TEST");
        notificationDto.setBody("TEST BODY");

        pushNotificationDto.getLangAndNotification().put(PlatformLanguages.AZ, notificationDto);
        pushNotificationDto.setUsers(Set.of("cif01", "cif02")); // Simulate topics in the DTO
        UserEntity user01 = new UserEntity();
        user01.setCif("cif01");
        user01.setToken("Token01");
        UserEntity user02 = new UserEntity();
        user02.setCif("cif02");

        // When
        when(fcmServiceImpl.sendNotificationToUserByToken(
                eq(user01.getToken()),
                eq(pushNotificationDto.getLangAndNotification().get(PlatformLanguages.AZ))
        )).thenReturn("success");


        when(userRepository.findAllByCifIn(List.of("cif01", "cif02"))).thenReturn(List.of(user01, user02));


        // Act
        rabbitMQService.receiveNotificationMessage(pushNotificationDto);


    }

}
