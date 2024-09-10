package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.entity.NotificationEntity;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.exceptions.WrongRequestBodyException;
import com.pushnotification.pushnotification.helpers.MessageSourceReaderHelper;
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

import static org.junit.jupiter.api.Assertions.*;
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
    @Mock
    private MessageSourceReaderHelper messageSourceReaderHelper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private static final String MISSING_LANGUAGE_MESSAGE = "missing.language.key.error";

    @Test
    @DisplayName("Send Notification by topics Success")
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
    @DisplayName("Send Notification by Users' Cifs Success")
    void sendNotificationByUsers_success() {
        // Arrange
        PushNotificationDto pushNotificationDto = new PushNotificationDto();

        Map<PlatformLanguages, NotificationDto> langAndNotification = new HashMap<>();
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTitle("Test Title");
        langAndNotification.put(PlatformLanguages.EN, notificationDto);
        langAndNotification.put(PlatformLanguages.RU, notificationDto);
        langAndNotification.put(PlatformLanguages.AZ, notificationDto);
        pushNotificationDto.setLangAndNotification(langAndNotification);

        Set<String> userCifs = new HashSet<>(Arrays.asList("CIF123", "CIF456"));
        var userEntity01 = new UserEntity();
        userEntity01.setCif("CIF123");
        var userEntity02 = new UserEntity();
        userEntity02.setCif("CIF456");

        List<UserEntity> usersFromDB = Arrays.asList(userEntity01, userEntity02);

        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setSentToMq(false);

        when(userRepository.findAllByCifIn(new ArrayList<>(userCifs))).thenReturn(usersFromDB);
        when(modelMapper.map(any(NotificationDto.class), eq(NotificationEntity.class)))
                .thenReturn(notificationEntity);

        // Act
        notificationService.sendNotificationByUsers(pushNotificationDto, userCifs);

        // Assert
        verify(userRepository, times(1)).findAllByCifIn(anyList());
        verify(notificationRepository, times(1)).saveAll(anyList());
        verify(rabbitMQService, times(1)).sendNotificationMessage(pushNotificationDto);

        // Check if notification entities are set as sent to MQ
        assertTrue(notificationEntity.isSentToMq());
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

        // When
        when(messageSourceReaderHelper.getMessage(anyString())).thenReturn(MISSING_LANGUAGE_MESSAGE);

        // Assert
        assertThrows(WrongRequestBodyException.class, () ->
                notificationService.saveAndSendNotificationByTopics(pushNotificationDto, givenTopics));
    }


    @Test
    void testFetchNotificationsByUserCif_UserExists() {
        // Arrange
        String cif = "CIF123";
        UserEntity userEntity = new UserEntity(); // Mock user entity
        NotificationEntity notificationEntity = new NotificationEntity(); // Mock notification entity
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTitle("Test Notification");

        when(userRepository.findByCif(cif)).thenReturn(Optional.of(userEntity));
        when(notificationRepository.findAllByUsers_Cif(cif)).thenReturn(new HashSet<>(List.of(notificationEntity)));
        when(modelMapper.map(notificationEntity, NotificationDto.class)).thenReturn(notificationDto);

        // Act
        Set<NotificationDto> result = notificationService.fetchNotificationsByUserCif(cif);

        // Assert
        verify(userRepository, times(1)).findByCif(cif);
        verify(notificationRepository, times(1)).findAllByUsers_Cif(cif);
        verify(modelMapper, times(1)).map(notificationEntity, NotificationDto.class);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Notification", result.iterator().next().getTitle());
    }

    @Test
    void testFetchNotificationsByUserCif_UserNotFound() {
        // Arrange
        String cif = "CIF123";

        when(userRepository.findByCif(cif)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            notificationService.fetchNotificationsByUserCif(cif);
        });

        assertEquals("User", exception.getResourceName());
        assertEquals("cif", exception.getFieldName());
        assertEquals(cif, exception.getFieldValue());

        verify(userRepository, times(1)).findByCif(cif);
        verify(notificationRepository, never()).findAllByUsers_Cif(anyString());
        verify(modelMapper, never()).map(any(), any());
    }

}
