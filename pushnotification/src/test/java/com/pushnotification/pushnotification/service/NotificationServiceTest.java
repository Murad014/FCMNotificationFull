package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.entity.NotificationEntity;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.WrongRequestBodyException;
import com.pushnotification.pushnotification.repository.NotificationRepository;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private TopicService topicService;
    @Mock
    private RabbitMQService rabbitMQService;

    @InjectMocks
    private NotificationServiceImpl notificationService;


    @Test
    void sendNotificationByTopics_success() {
        // Setup test data
        PushNotificationDto pushNotificationDto = new PushNotificationDto();

        Map<PlatformLanguages, NotificationDto> notificationsWithLanguages = new HashMap<>();
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTitle("Test Title");
        notificationsWithLanguages.put(PlatformLanguages.EN, notificationDto);
        notificationsWithLanguages.put(PlatformLanguages.AZ, notificationDto);
        notificationsWithLanguages.put(PlatformLanguages.RU, notificationDto);
        pushNotificationDto.setLangAndNotification(notificationsWithLanguages);
        Set<String> givenTopics = new HashSet<>();
        givenTopics.add("NEWS");

        Set<TopicEntity> topicsFromDB = new HashSet<>();
        TopicEntity topicEntityEn = new TopicEntity();
        topicEntityEn.setName("NEWS_EN");
        TopicEntity topicEntityAz = new TopicEntity();
        topicEntityAz.setName("NEWS_AZ");
        TopicEntity topicEntityRu = new TopicEntity();
        topicEntityRu.setName("NEWS_RU");

        topicsFromDB.add(topicEntityEn);
        topicsFromDB.add(topicEntityAz);
        topicsFromDB.add(topicEntityRu);

        List<UserEntity> users = new ArrayList<>();
        UserEntity userEntity = new UserEntity();
        userEntity.setTopics(topicsFromDB);
        userEntity.setCif("123456");
        users.add(userEntity);


        // When
        NotificationEntity mockNotificationEntity = new NotificationEntity();
        mockNotificationEntity.setTitle("Test Title");
        mockNotificationEntity.setUsers(new HashSet<>(users));

        when(topicService.findAllTopicsInGivenTopicList(anySet())).thenReturn(topicsFromDB);
        when(userRepository.findAllByTopics_NameIn(anyList())).thenReturn(users);
        when(modelMapper.map(any(), eq(NotificationEntity.class)))
                .thenReturn(mockNotificationEntity);
        doNothing().when(rabbitMQService).sendNotificationMessage(any());

        doNothing().when(topicService).checkAllGivenTopicsInDB(anySet(), anySet());

        // Act
        notificationService.saveAndSendNotificationByTopics(pushNotificationDto, givenTopics);

        // Verify
        verify(notificationRepository, times(1)).saveAll(anyList());
        verify(userRepository, times(3)).findAllByTopics_NameIn(anyList());
    }

    @Test
    void sendNotificationByTopics_missingLanguageKey_throwsException() {
        // Arrange
        PushNotificationDto pushNotificationDto = new PushNotificationDto();
        Map<PlatformLanguages, NotificationDto> notificationsWithLanguages = new HashMap<>();
        NotificationDto notificationDto = new NotificationDto();
        notificationsWithLanguages.put(PlatformLanguages.EN, notificationDto);
        pushNotificationDto.setLangAndNotification(notificationsWithLanguages);

        Set<String> givenTopics = new HashSet<>();
        givenTopics.add("NEWS");

        // Assert
        assertThrows(WrongRequestBodyException.class, () ->
                notificationService.saveAndSendNotificationByTopics(pushNotificationDto, givenTopics));
    }

}
