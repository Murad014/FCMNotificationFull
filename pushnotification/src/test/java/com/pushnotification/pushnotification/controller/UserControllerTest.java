package com.pushnotification.pushnotification.controller;


import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.helper.UserDtoCreatorHelper;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@DisplayName("User Controller")
class UserControllerTest {
    final String createdMessage = "User created Successfully!";
    final String updateMessage = "User updated Successfully!";


    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private GenerateResponseHelper generateResponseHelper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    @DisplayName("Create User")
    void createUser_ShouldReturn201CreatedWithResponseDto() {

        // Arrange
        UserDto userDto = new UserDto();
        UserDto createdUser = new UserDto();

        // When
        when(userService.createUser(userDto)).thenReturn(createdUser);
        when(messageSource.getMessage(any(), any(), any())).thenReturn(createdMessage);
        when(generateResponseHelper.generateResponse(eq(201), any(), any(), any())).thenReturn(any());

        // Act
        ResponseEntity<ResponseDto<UserDto>> responseEntity = userController.createUser(userDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Verify
        verify(userService).createUser(userDto);
        verify(generateResponseHelper).generateResponse(eq(201), any(), any(), any());
    }

    @Test
    @Order(1)
    @DisplayName("Update User By CIF")
    void UpdateUser_ShouldReturn200UpdateWithResponseDto() {
        final String cif = "1234567";

        // Arrange
        UserUpdateDto userDto = new UserUpdateDto();
        var updatedUser = new UserUpdateDto();

        when(userService.updateUser(cif, userDto)).thenReturn(updatedUser);
        when(messageSource.getMessage(any(), any(), any())).thenReturn(updateMessage);

        // Act
        ResponseEntity<ResponseDto<UserUpdateDto>> responseEntity = userController.updateUser(cif, userDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify
        verify(userService, times(1)).updateUser(cif, userDto);
        verify(generateResponseHelper).generateResponse(eq(200), any(), any(), any());
    }

    @Test
    void testSetTopicsByUserCif() {
        // Arrange
        UserDto userDto = UserDtoCreatorHelper.dto();
        var updatedCIF = "1212314";
        var topics = new HashSet<>(List.of("Topic1"));
        var mockResponseDto = new ResponseDto<>();
        mockResponseDto.setCode(HttpStatus.OK.value());
        mockResponseDto.setMessage("user.topics.set.successfully");
        mockResponseDto.setData(null);
        mockResponseDto.setPath("/api/v1/users/1212314/topics");

        Map<String, Set<String>> controllerInput = new HashMap<>();
        controllerInput.put("topics", topics);

        // When
        doNothing().when(userService).setTopicsByUserCif(eq(updatedCIF), eq(topics));
        when(generateResponseHelper.generateResponse(Mockito.anyInt(), Mockito.anyString(), Mockito.any(), Mockito.anyString()))
                .thenReturn(mockResponseDto);

        // Act
        var response = userController.setTopicsByUserCif(updatedCIF, controllerInput);

        // Verify
        verify(userService, times(1)).setTopicsByUserCif(updatedCIF, topics);
        verify(generateResponseHelper, times(1))
                .generateResponse(Mockito.eq(HttpStatus.OK.value()),
                        Mockito.eq("user.topics.set.successfully"),
                        Mockito.eq(null), Mockito.eq("/api/v1/users/1212314/topics"));

        // Assert
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user.topics.set.successfully", response.getBody().getMessage());
        assertEquals("/api/v1/users/1212314/topics", response.getBody().getPath());
        assertEquals(null, response.getBody().getData());
    }



}
