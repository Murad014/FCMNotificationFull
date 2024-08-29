package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private static final String MAIN_PATH = "/api/v1/users";
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
                createdUser, MAIN_PATH);
    }

    @PutMapping("/{cif}")
    public ResponseEntity<ResponseDto<UserUpdateDto>> updateUser(@PathVariable("cif") String cif,
                                                                 @Valid @RequestBody UserUpdateDto userDto) {
        UserUpdateDto updatedUser = userService.updateUser(cif, userDto);

        return buildResponse(HttpStatus.OK, "user.update.message",
                updatedUser, MAIN_PATH.concat("/").concat(cif));
    }

    @PutMapping("/{cif}/topics")
    public ResponseEntity<ResponseDto<UserUpdateDto>> setTopics(@PathVariable("cif") String cif,
                                                                @RequestBody Set<String> topics){

        System.out.println("Test: " + topics.toString());
        return null;
    }


    private <D> ResponseEntity<ResponseDto<D>> buildResponse(HttpStatus status, String messageKey,
                                                             D data, String path) {
        var response = generateResponseHelper.generateResponse(status.value(), messageKey, data, path);
        return new ResponseEntity<>(response, status);
    }
}
