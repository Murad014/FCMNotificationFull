package com.pushnotification.pushnotification.dto;

import com.pushnotification.pushnotification.config.MessageConfig;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.request.TopicRequestDto;
import com.pushnotification.pushnotification.repository.TopicRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TopicDto Validation Test")
public class TopicRequestDtoTest {
    @Autowired
    private final Validator validator;

    @Autowired
    private MessageConfig messageConfig;

    @Autowired
    private TopicRepository userRepository;

    public TopicRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test Valid TopicDto")
    public void testValidTopicDto() {
        // Arrange
        Map<PlatformLanguages, String> hashMap = new HashMap<>();
        hashMap.put(PlatformLanguages.AZ, "Desc");

        TopicRequestDto topicRequestDto = new TopicRequestDto();
        topicRequestDto.setName("test");
        topicRequestDto.setDescription(hashMap);

        // Act
        var violations = validator.validate(topicRequestDto);

        // Assert
        assertTrue(violations.isEmpty(), "UserDto should be valid");
    }



}
