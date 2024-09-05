package com.pushnotification.pushnotification.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("NotificationDto Validation Test")
public class NotificationDtoTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenTitleIsNull_thenValidationFails() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTitle(null); // title is null
        notificationDto.setBody("Sample Body");
        notificationDto.setImageUrl("http://example.com/image.png");
        notificationDto.setIconUrl("http://example.com/icon.png");
        notificationDto.setSubtitle("Sample Subtitle");


        Set<ConstraintViolation<NotificationDto>> violations = validator.validate(notificationDto);
        System.out.println(violations);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    @Test
    public void whenTitleIsEmpty_thenValidationFails() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTitle(""); // title is empty
        notificationDto.setBody("Sample Body");
        notificationDto.setImageUrl("http://example.com/image.png");
        notificationDto.setIconUrl("http://example.com/icon.png");
        notificationDto.setSubtitle("Sample Subtitle");

        Set<ConstraintViolation<NotificationDto>> violations = validator.validate(notificationDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    public void whenTitleIsValid_thenValidationPasses() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTitle("Valid Title");
        notificationDto.setBody("Sample Body");
        notificationDto.setImageUrl("http://example.com/image.png");
        notificationDto.setIconUrl("http://example.com/icon.png");
        notificationDto.setSubtitle("Sample Subtitle");

        Set<ConstraintViolation<NotificationDto>> violations = validator.validate(notificationDto);
        assertTrue(violations.isEmpty());
    }

}
