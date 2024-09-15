package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.NotificationDto;
import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.request.PushNotificationDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/notification")
@Tag(name="Notification Endpoints", description = "Send notification by topics and users' cifs.")
public class NotificationController {
    private final NotificationService notificationService;
    private final GenerateResponseHelper generateResponseHelper;

    private static final String NOTIFICATION_FETCH_SUCCESSFULLY_MESSAGE = "notification.fetch.successfully";
    private static final String NOTIFICATION_SEND_SUCCESSFULLY_MESSAGE = "notification.send.successfully";

    @Autowired
    public NotificationController(NotificationService notificationService,
                                  GenerateResponseHelper generateResponseHelper) {
        this.notificationService = notificationService;
        this.generateResponseHelper = generateResponseHelper;
    }

    @Operation(summary = "Send notification by topics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification has been sent successfully.",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Topics resource not found. ResourceNotFound Exception",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "406", description = "Request body is incorrect. WrongRequestBody Exception",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/send/topics")
    public ResponseEntity<ResponseDto<Void>> sendNotificationByTopics(@RequestBody PushNotificationDto pushNotificationDto) {
        notificationService.saveAndSendNotificationByTopics(pushNotificationDto,
                pushNotificationDto.getTopics());

        return buildResponse(NOTIFICATION_SEND_SUCCESSFULLY_MESSAGE, null);
    }

    @Operation(summary = "Send notification by Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification has been sent successfully.",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Users resource not found. ResourceNotFound Exception",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "406", description = "Wrong Request Body Exception",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/send/users")
    public ResponseEntity<ResponseDto<Void>> sendNotificationByUsers(@RequestBody PushNotificationDto pushNotificationDto) {
        notificationService.sendNotificationByUsers(pushNotificationDto,
                pushNotificationDto.getUsers());

        return buildResponse(NOTIFICATION_SEND_SUCCESSFULLY_MESSAGE, null);
    }

    @Operation(summary = "Get All Notifications By User Cif")
    @GetMapping("/user/{cif}")
    public ResponseEntity<ResponseDto<Set<NotificationDto>>> getAllNotificationsByUserCif(@PathVariable("cif") String cif) {
        var response = notificationService.fetchNotificationsByUserCif(cif);

        return buildResponse(NOTIFICATION_FETCH_SUCCESSFULLY_MESSAGE, response);
    }


    private <D> ResponseEntity<ResponseDto<D>> buildResponse(String messageKey, D data) {
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri();

        var response = generateResponseHelper.generateResponse(HttpStatus.OK.value(), messageKey, data,
                location.getPath());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}