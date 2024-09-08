package com.pushnotification.pushnotification.repository;

import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.entity.NotificationEntity;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.helper.NotificationCreator;
import com.pushnotification.pushnotification.helper.TopicEntityCreatorHelper;
import com.pushnotification.pushnotification.helper.UserEntityCreatorHelper;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TopicRepository topicRepository;

    private NotificationEntity entity;
    private Set<NotificationEntity> entitySet;



    @BeforeEach
    public void beforeEach() {
        entity = NotificationCreator.entity();
        entitySet = NotificationCreator.entityList(10);
    }

    @Test
    @DisplayName("Add Notification")
    @Order(1)
    public void givenEntity_thenSave_thenReturnEntity(){
        // Arrange - beforeEach

        // Act
        var save = notificationRepository.save(entity);

        // Assert
        customAssertions(entity, save);
    }

    @Test
    @DisplayName("Add Notification With Topic and Users")
    @Order(2)
    @Transactional
    public void givenEntityWithTopicAndUsers_thenSave_thenReturnEntity() {
        // Arrange - beforeEach
        var topics = TopicEntityCreatorHelper.entityList(5);
        var users = UserEntityCreatorHelper.entityList(5);
        userRepository.saveAll(users);
        topicRepository.saveAll(topics);


        entity.setUsers(new HashSet<>(users));


        // Act
        var save = notificationRepository.save(entity);

        // Assertions
        assertNotNull(save);
        assertFalse(save.getUsers().isEmpty());
        assertEquals(save.getUsers().size(), 5);
    }


    private void customAssertions(NotificationEntity expected, NotificationEntity actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getBody(), actual.getBody());
        assertEquals(expected.getIconUrl(), actual.getIconUrl());
        assertEquals(expected.getImageUrl(), actual.getImageUrl());
    }



}
