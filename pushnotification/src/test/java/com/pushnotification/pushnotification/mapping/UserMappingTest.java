package com.pushnotification.pushnotification.mapping;

import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMappingTest {

    @InjectMocks
    private UserMapping userMapping;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userMapping = new UserMapping();


        userEntity = new UserEntity();
        userEntity.setUuid("1234-5678-9012");
        userEntity.setToken("token123");
        userEntity.setPlatform(Platform.ANDROID);
        userEntity.setPlatformLanguage(PlatformLanguages.EN);

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("Sports");

        TopicEntity topic2 = new TopicEntity();
        topic2.setName("Technology");

        Set<TopicEntity> topics = new HashSet<>();
        topics.add(topic1);
        topics.add(topic2);

        userEntity.setTopics(topics);
    }

    @Test
    void testToUserDto() {
        UserDto userDto = userMapping.toUserDto(userEntity);

        assertNotNull(userDto);
        assertEquals(userEntity.getToken(), userDto.getToken());
        assertEquals(userEntity.getPlatform(), userDto.getPlatform());
        assertEquals(userEntity.getPlatformLanguage(), userDto.getPlatformLanguage());
        assertEquals(2, userDto.getTopics().size());
        assertEquals(Set.of("Sports", "Technology"), userDto.getTopics());
    }

    @Test
    void testToUserUpdateDto() {
        UserUpdateDto userUpdateDto = userMapping.toUserUpdateDto(userEntity);

        assertNotNull(userUpdateDto);
        assertEquals(userEntity.getToken(), userUpdateDto.getToken());
        assertEquals(userEntity.getPlatform(), userUpdateDto.getPlatform());
        assertEquals(userEntity.getPlatformLanguage(), userUpdateDto.getPlatformLanguage());
        assertEquals(2, userUpdateDto.getTopics().size());
        assertEquals(Set.of("Sports", "Technology"), userUpdateDto.getTopics());
    }
}
