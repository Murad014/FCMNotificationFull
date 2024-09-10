package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final GenerateResponseHelper generateResponseHelper;


    @Autowired
    public UserController(UserService userService, GenerateResponseHelper generateResponseHelper) {
        this.userService = userService;
        this.generateResponseHelper = generateResponseHelper;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);

        return buildResponse(HttpStatus.CREATED, "user.created.message",
                createdUser);
    }

    @PutMapping("/{cif}")
    public ResponseEntity<ResponseDto<UserUpdateDto>> updateUser(@PathVariable("cif") String cif,
                                                                 @Valid @RequestBody UserUpdateDto userDto) {
        UserUpdateDto updatedUser = userService.updateUser(cif, userDto);

        return buildResponse(HttpStatus.OK, "user.updated.message",
                updatedUser);
    }

    @PutMapping("/{cif}/topics")
    public ResponseEntity<ResponseDto<UserUpdateDto>> setTopicsByUserCif(@PathVariable("cif") String cif,
                                                                @RequestBody Map<String, Set<String>> requestBody) {
        userService.setTopicsByUserCif(cif, requestBody.get("topics"));

        return buildResponse(HttpStatus.OK, "user.topics.set.successfully",
                null);
    }

    private <D> ResponseEntity<ResponseDto<D>> buildResponse(HttpStatus status, String messageKey,
                                                             D data) {
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri();
        var response = generateResponseHelper.generateResponse(status.value(), messageKey, data, location.getPath());
        return new ResponseEntity<>(response, status);
    }
}
