package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.request.TopicRequestDto;
import com.pushnotification.pushnotification.dto.TopicFetchDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.TopicService;
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

import java.util.Set;

@RestController
@RequestMapping("/api/v1/topics")
@Tag(name="Topics Endpoints", description = "Create topic, Get Topics")
public class TopicController {

    private final TopicService topicService;
    private final GenerateResponseHelper generateResponseHelper;

    private static final String TOPIC_FETCHED_SUCCESS_MESSAGE = "topic.fetched.success.message";
    private static final String TOPIC_CREATED_SUCCESS_MESSAGE = "topic.created.success.message";

    @Autowired
    public TopicController(TopicService topicService, GenerateResponseHelper generateResponseHelper) {
        this.topicService = topicService;
        this.generateResponseHelper = generateResponseHelper;
    }

    @Operation(summary = "Create a new topic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Topic successfully created",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDto<TopicRequestDto>> createTopic(@Valid @RequestBody TopicRequestDto topicRequestDto) {
        var saved = topicService.createTopic(topicRequestDto);
        return buildResponse(HttpStatus.CREATED, TOPIC_CREATED_SUCCESS_MESSAGE, saved);
    }

    @Operation(summary = "Get all topics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched all topics successfully.",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseDto<Set<TopicFetchDto>>> getTopics() {
        Set<TopicFetchDto> fetchAll = topicService.fetchAllTopics();
        return buildResponse(HttpStatus.OK, TOPIC_FETCHED_SUCCESS_MESSAGE, fetchAll);
    }

    private <D> ResponseEntity<ResponseDto<D>> buildResponse(HttpStatus status, String messageKey,
                                                             D data) {
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri();
        var response = generateResponseHelper.generateResponse(status.value(), messageKey, data,
                location.getPath());
        return new ResponseEntity<>(response, status);
    }

}
