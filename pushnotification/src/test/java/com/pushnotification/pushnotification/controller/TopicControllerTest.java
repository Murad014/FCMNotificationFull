package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.TopicDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.TopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TopicControllerTest {
    private final static String MAIN_PATH = "/api/v1/topics";

    @Mock
    private TopicService topicService;

    @Mock
    private GenerateResponseHelper generateResponseHelper;

    @InjectMocks
    private TopicController topicController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTopic() {
        // Arrange
        TopicDto topicDto = new TopicDto();
        TopicDto savedTopicDto = new TopicDto();
        ResponseDto<TopicDto> responseDto = new ResponseDto<>();
        responseDto.setCode(HttpStatus.CREATED.value());
        responseDto.setMessage("topic.created.success.message");
        responseDto.setData(savedTopicDto);
        responseDto.setPath(MAIN_PATH);

        when(topicService.createTopic(topicDto)).thenReturn(savedTopicDto);
        when(generateResponseHelper.generateResponse(
                HttpStatus.CREATED.value(), "topic.created.success.message", savedTopicDto, MAIN_PATH))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<ResponseDto<TopicDto>> response = topicController.createTopic(topicDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(topicService, times(1)).createTopic(topicDto);
        verify(generateResponseHelper, times(1))
                .generateResponse(HttpStatus.CREATED.value(), "topic.created.success.message", savedTopicDto, MAIN_PATH);
    }

    @Test
    void testGetTopics() {
        // Arrange
        Set<TopicDto> topics = new HashSet<>();
        ResponseDto<Set<TopicDto>> responseDto = new ResponseDto<>();
        responseDto.setCode(HttpStatus.OK.value());
        responseDto.setMessage("topic.fetched.success.message");
        responseDto.setData(topics);
        responseDto.setPath(MAIN_PATH);

        when(topicService.fetchAllTopics()).thenReturn(topics);
        when(generateResponseHelper.generateResponse(
                HttpStatus.OK.value(), "topic.fetched.success.message", topics, MAIN_PATH))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<ResponseDto<Set<TopicDto>>> response = topicController.getTopics();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(topicService, times(1)).fetchAllTopics();
        verify(generateResponseHelper, times(1))
                .generateResponse(HttpStatus.OK.value(), "topic.fetched.success.message", topics, MAIN_PATH);
    }

    @Test
    void testDeleteTopic() {
        // Arrange
        String topicName = "testTopic";
        ResponseDto<Object> responseDto = new ResponseDto<>();
        responseDto.setCode(HttpStatus.OK.value());
        responseDto.setMessage("topic.deleted.success.message");
        responseDto.setData(null);
        responseDto.setPath(MAIN_PATH + "/" + topicName);

        when(generateResponseHelper.generateResponse(
                HttpStatus.OK.value(), "topic.deleted.success.message", null, MAIN_PATH + "/" + topicName))
                .thenReturn(responseDto);

        // Act
        topicController.deleteTopic(topicName);

        // Assert

        verify(topicService, times(1)).deleteTopic(topicName);

    }
}

