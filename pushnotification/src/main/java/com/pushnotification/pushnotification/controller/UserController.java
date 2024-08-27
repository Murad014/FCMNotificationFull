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


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final String mainPath = "/api/v1/users";
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
        var response = generateResponseHelper.generateResponse(201, "user.created.message",
                createdUser, mainPath);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{cif}")
    public ResponseEntity<ResponseDto<UserUpdateDto>> updateUser(@PathVariable("cif") String cif,
                                                                 @Valid @RequestBody UserUpdateDto userDto) {
        UserUpdateDto updatedUser = userService.updateUser(cif, userDto);
        var response = generateResponseHelper.generateResponse(200, "user.update.message",
                updatedUser, mainPath.concat("/").concat(cif));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
