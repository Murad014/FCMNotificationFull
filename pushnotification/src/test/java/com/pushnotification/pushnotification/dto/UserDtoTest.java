package com.pushnotification.pushnotification.dto;

import com.pushnotification.pushnotification.config.MessageConfig;
import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.repository.UserRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("UserDto Validation Test")
public class UserDtoTest {

    @Autowired
    private final Validator validator;

    @Autowired
    private MessageConfig messageConfig;

    @Autowired
    private UserRepository userRepository;


    public UserDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test Valid User Dto")
    public void testValidUserDto() {
        // Arrange

        UserDto userDto = new UserDto();
        userDto.setCif("1234567");
        userDto.setToken("sample-token");
        userDto.setPlatformLanguage(PlatformLanguages.AZ);
        userDto.setPlatform(Platform.ANDROID); // Example value

        // Act
        var violations = validator.validate(userDto);

        // Assert
        assertTrue(violations.isEmpty(), "UserDto should be valid");
    }

    @Test
    @DisplayName("Test Invalid User Dto")
    public void testInvalidUserDto() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setCif(""); // Invalid: empty string
        userDto.setToken(null); // Optional field
        userDto.setPlatform(null); // Invalid: null

        // Act
        var violations = validator.validate(userDto);

        // Assert
        assertFalse(violations.isEmpty(), "UserDto should have validation errors");


        var cifViolation = violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("cif"));
        var platformViolation = violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("platform"));

        assertTrue(cifViolation, "CIF field should have validation error");
        assertTrue(platformViolation, "Platform field should have validation error");
    }

    @Test
    @DisplayName("Test Cif Validation Range")
    public void testCifValidationRange() {
        //Arrange

        //Act
        UserDto userDto = new UserDto();
        userDto.setCif("123456");
        userDto.setPlatformLanguage(PlatformLanguages.AZ);

        var violations = validator.validate(userDto);

        // Assert
        assertFalse(violations.isEmpty(), "UserDto should have validation errors");
        var cifViolation = violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("cif"));
        assertTrue(cifViolation, "CIF field should have validation error for range");
    }

    @Test
    @DisplayName("Check unique CIF violation")
    public void testUniqueCif() {
        // Arrange
        UserDto userDtoToSave = new UserDto();
        userDtoToSave.setToken("sample-token");
        userDtoToSave.setCif("123456");
        userDtoToSave.setPlatformLanguage(PlatformLanguages.AZ);
        var fromDB = userRepository.save(mapToUserEntity(userDtoToSave));

        UserDto userDto = new UserDto();
        userDto.setCif(fromDB.getCif());
        userDto.setToken("sample-token2");
        userDto.setPlatform(Platform.ANDROID);
        userDto.setPlatformLanguage(PlatformLanguages.AZ);

        // Act
        var violations = validator.validate(userDto);

        // Assert
        assertFalse(violations.isEmpty(), "UserDto should have validation errors");
        var cifViolation = violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("cif"));
        assertTrue(cifViolation, "CIF field should have validation error for unique violation");

    }


    @Test
    @DisplayName("Check unique Token violation")
    public void testUniqueToken() {
        // Arrange
        UserDto userDtoToSave = new UserDto();
        userDtoToSave.setToken("sample-toke213123n");
        userDtoToSave.setCif("1234562");
        userDtoToSave.setPlatformLanguage(PlatformLanguages.AZ);

        var fromDB = userRepository.save(mapToUserEntity(userDtoToSave));

        UserDto userDto = new UserDto();
        userDto.setCif("1234526");
        userDto.setToken(fromDB.getToken());
        userDto.setPlatform(Platform.ANDROID);
        userDto.setPlatformLanguage(PlatformLanguages.AZ);

        // Act
        var violations = validator.validate(userDto);


        // Assert
        assertFalse(violations.isEmpty(), "UserDto should have validation errors");
        var tokenViolation = violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("token"));
        assertTrue(tokenViolation, "Token field should have validation error for unique violation");

    }

    @Test
    @DisplayName("Check unique UUID violation")
    @Disabled
    public void testUniqueUUID() {
        // Arrange
        UserDto userDtoToSave = new UserDto();
        userDtoToSave.setPlatformLanguage(PlatformLanguages.AZ);
        var fromDB = userRepository.save(mapToUserEntity(userDtoToSave));

        UserDto userDto = new UserDto();
        userDto.setCif("1234526");
        userDto.setToken("ashdiyqiuwyduiyiy");
        userDto.setPlatform(Platform.ANDROID);

        // Act
        var violations = validator.validate(userDto);

        // Assert
        assertFalse(violations.isEmpty(), "UserDto should have validation errors");
        var uuidViolation = violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("uuid"));
        assertTrue(uuidViolation, "UUID field should have validation error for unique violation");

    }




    private UserEntity mapToUserEntity(UserDto userDto) {
        var userEntity = new UserEntity();
        userEntity.setCif(userDto.getCif());
        userEntity.setToken(userDto.getToken());
        userEntity.setIsActive(true);
        userEntity.setPlatformLanguage(userDto.getPlatformLanguage());
        return userEntity;

    }
}
