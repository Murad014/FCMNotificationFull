package com.pushnotification.pushnotification.controller;


import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@DisplayName("User Controller")
class UserControllerTest {
    private static final int UPDATE_STATUS_CODE = HttpStatus.OK.value();
    final int CREATE_STATUS_CODE = 201;
    final String SELF_MAIN_LINK = "/api/v1/users";
    final String createdMessage = "User created Successfully!";
    final String updateMessage = "User updated Successfully!";


    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;

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
        when(userService.createUser(userDto)).thenReturn(createdUser);
        when(messageSource.getMessage(any(), any(), any())).thenReturn(createdMessage);

        // Act
        ResponseEntity<ResponseDto<UserDto>> responseEntity = userController.createUser(userDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ResponseDto<UserDto> responseDto = responseEntity.getBody();

        assertNotNull(responseDto);
        assertEquals(CREATE_STATUS_CODE, responseDto.getCode());
        assertEquals(SELF_MAIN_LINK, responseDto.getPath());
        assertEquals(createdUser, responseDto.getData());
        assertEquals(responseDto.getMessage(), createdMessage);

        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    @Order(1)
    @DisplayName("Update User")
    void UpdateUser_ShouldReturn200UpdateWithResponseDto() {
        final String cif = "1234567";

        // Arrange
        UserUpdateDto userDto = new UserUpdateDto();
        var createdUser = new UserUpdateDto();

        when(userService.updateUser(cif, userDto)).thenReturn(createdUser);
        when(messageSource.getMessage(any(), any(), any())).thenReturn(updateMessage);

        // Act
        ResponseEntity<ResponseDto<UserUpdateDto>> responseEntity = userController.updateUser(cif, userDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ResponseDto<UserUpdateDto> responseDto = responseEntity.getBody();

        assertNotNull(responseDto);
        assertEquals(UPDATE_STATUS_CODE, responseDto.getCode());
        assertEquals(SELF_MAIN_LINK.concat("/").concat(cif), responseDto.getPath());
        assertEquals(createdUser, responseDto.getData());
        assertEquals(responseDto.getMessage(), updateMessage);

        verify(userService, times(1)).updateUser(cif, userDto);
    }



}
