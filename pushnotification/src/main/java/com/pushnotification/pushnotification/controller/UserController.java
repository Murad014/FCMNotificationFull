package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Users Endpoints", description = "Add user, Update user by cif, Set Topics by User cif")
public class UserController {
    private final UserService userService;
    private final GenerateResponseHelper generateResponseHelper;


    @Autowired
    public UserController(UserService userService, GenerateResponseHelper generateResponseHelper) {
        this.userService = userService;
        this.generateResponseHelper = generateResponseHelper;
    }

    @Operation(summary = "Create a new user", description = "Creates a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDto<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);

        return buildResponse(HttpStatus.CREATED, "user.created.message",
                createdUser);
    }

    @Operation(summary = "Update user by cif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User resource not found",
            content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PutMapping("/{cif}")
    public ResponseEntity<ResponseDto<UserUpdateDto>> updateUser(@PathVariable("cif") String cif,
                                                                 @Valid @RequestBody UserUpdateDto userDto) {
        UserUpdateDto updatedUser = userService.updateUser(cif, userDto);

        return buildResponse(HttpStatus.OK, "user.updated.message",
                updatedUser);
    }

    @Operation(summary = "Set topics by user cif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topics set successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User resource not found or Topics resource not found",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
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
