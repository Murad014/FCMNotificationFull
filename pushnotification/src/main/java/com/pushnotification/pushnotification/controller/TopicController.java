package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.TopicDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {

    private final TopicService topicService;
    private final GenerateResponseHelper generateResponseHelper;
    private final static String MAIN_PATH = "/api/v1/topics";
    
    @Autowired
    public TopicController(TopicService topicService, GenerateResponseHelper generateResponseHelper) {
        this.topicService = topicService;
        this.generateResponseHelper = generateResponseHelper;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<TopicDto>> createTopic(@Valid @RequestBody TopicDto topicDto) {
        var saved = topicService.createTopic(topicDto);
        return buildResponse(HttpStatus.CREATED, "topic.created.success.message", saved);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<Set<TopicDto>>> getTopics() {
        Set<TopicDto> fetchAll = topicService.fetchAllTopics();
        return buildResponse(HttpStatus.OK, "topic.fetched.success.message", fetchAll);
    }

    @DeleteMapping("/{topicName}")
    public ResponseEntity<ResponseDto<Object>> deleteTopic(@Valid @PathVariable("topicName") String name) {
        topicService.deleteTopic(name);
        var responseDto = new ResponseDto<>();
        responseDto.setCode(204);
        responseDto.setMessage("Topic deleted successfully");
        responseDto.setPath("/api/v1/topics/" + name);

        return buildResponse(HttpStatus.OK, "topic.deleted.success.message", null);
    }

    private <D> ResponseEntity<ResponseDto<D>> buildResponse(HttpStatus status, String messageKey,
                                                             D data) {
        var response = generateResponseHelper.generateResponse(status.value(), messageKey, data,
                TopicController.MAIN_PATH);
        return new ResponseEntity<>(response, status);
    }

}
