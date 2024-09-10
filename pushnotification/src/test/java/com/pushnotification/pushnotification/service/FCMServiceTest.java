package com.pushnotification.pushnotification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.service.impl.FCMServiceImpl;
import com.pushnotification.pushnotification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FCMServiceTest {
    @Mock
    private FirebaseMessaging firebaseMessaging;



    @InjectMocks
    private FCMServiceImpl fcmService;

    @Test
    void testSendNotificationToTopic_Success() throws Exception {
        // Arrange
        Set<String> topics = Set.of("topic1", "topic2");
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTitle("Test Title");
        notificationDto.setBody("Test Body");
        notificationDto.setImageUrl("http://test-image-url.com");

        String expectedCondition = topics.stream()
                .map(topic -> "'" + topic + "' in topics")
                .collect(Collectors.joining(" || "));

        Message expectedMessage = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(notificationDto.getTitle())
                        .setBody(notificationDto.getBody())
                        .setImage(notificationDto.getImageUrl())
                        .build())
                .setCondition(expectedCondition)
                .build();

        String expectedResponse = "messageId12345";

        // Mock FirebaseMessaging send method
        when(firebaseMessaging.send(any(Message.class))).thenReturn(expectedResponse);

        // Act
        fcmService.sendNotificationToTopic(topics, notificationDto);


    }
}
