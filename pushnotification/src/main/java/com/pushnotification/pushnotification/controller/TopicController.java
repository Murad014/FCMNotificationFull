package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.request.TopicRequestDto;
import com.pushnotification.pushnotification.dto.TopicFetchDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {

    private final TopicService topicService;
    private final GenerateResponseHelper generateResponseHelper;

    @Autowired
    public TopicController(TopicService topicService, GenerateResponseHelper generateResponseHelper) {
        this.topicService = topicService;
        this.generateResponseHelper = generateResponseHelper;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<TopicRequestDto>> createTopic(@Valid @RequestBody TopicRequestDto topicRequestDto) {
        var saved = topicService.createTopic(topicRequestDto);
        return buildResponse(HttpStatus.CREATED, "topic.created.success.message", saved);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<Set<TopicFetchDto>>> getTopics() {
        Set<TopicFetchDto> fetchAll = topicService.fetchAllTopics();
        return buildResponse(HttpStatus.OK, "topic.fetched.success.message", fetchAll);
    }

    @DeleteMapping("/{topicName}")
    public ResponseEntity<ResponseDto<Void>> deleteTopic(@Valid @PathVariable("topicName") String name) {
        topicService.deleteTopic(name);
        return buildResponse(HttpStatus.NO_CONTENT, "topic.deleted.success.message", null);
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
