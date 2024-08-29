package com.pushnotification.pushnotification.dto;

import com.pushnotification.pushnotification.config.MessageConfig;
import com.pushnotification.pushnotification.repository.TopicRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("TopicDto Validation Test")
public class TopicDtoTest {
    @Autowired
    private final Validator validator;

    @Autowired
    private MessageConfig messageConfig;

    @Autowired
    private TopicRepository userRepository;

    public TopicDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test Valid TopicDto")
    public void testValidTopicDto() {
        // Arrange
        TopicDto topicDto = new TopicDto();
        topicDto.setName("test");
        topicDto.setDescription("test description");

        // Act
        var violations = validator.validate(topicDto);

        // Assert
        assertTrue(violations.isEmpty(), "UserDto should be valid");
    }



}
