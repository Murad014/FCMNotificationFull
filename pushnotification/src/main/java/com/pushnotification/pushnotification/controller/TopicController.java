package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.TopicDto;
import com.pushnotification.pushnotification.service.TopicService;
import com.pushnotification.pushnotification.service.impl.TopicServiceImpl;
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
    private final TopicServiceImpl topicServiceImpl;

    @Autowired
    public TopicController(TopicService topicService, TopicServiceImpl topicServiceImpl) {
        this.topicService = topicService;
        this.topicServiceImpl = topicServiceImpl;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<TopicDto>> createTopic(@Valid @RequestBody TopicDto topicDto) {
        var saved = topicService.createTopic(topicDto);
        var responseDto = new ResponseDto<TopicDto>();
        responseDto.setCode(201);
        responseDto.setMessage("Topic created successfully");
        responseDto.setData(saved);
        responseDto.setPath("/api/v1/topics");

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<Set<TopicDto>>> getTopics() {
        Set<TopicDto> fetchAll = topicService.fetchAllTopics();
        var response = new ResponseDto<Set<TopicDto>>();
        response.setCode(200);
        response.setMessage("Topics fetched successfully");
        response.setData(fetchAll);
        response.setPath("/api/v1/topics");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{topicName}")
    public ResponseEntity<ResponseDto<?>> deleteTopic(@Valid @PathVariable("topicName") String name) {
        topicService.deleteTopic(name);
        var responseDto = new ResponseDto<>();
        responseDto.setCode(204);
        responseDto.setMessage("Topic deleted successfully");
        responseDto.setPath("/api/v1/topics/" + name);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
