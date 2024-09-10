package com.pushnotification.pushnotification.controller;

import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.request.TopicRequestDto;
import com.pushnotification.pushnotification.dto.TopicFetchDto;
import com.pushnotification.pushnotification.helpers.GenerateResponseHelper;
import com.pushnotification.pushnotification.service.TopicService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class TopicControllerTest {
    private final static String MAIN_PATH = "/api/v1/topics";

    @Mock
    private TopicService topicService;

    @Mock
    private GenerateResponseHelper generateResponseHelper;

    @InjectMocks
    private TopicController topicController;


    @Test
    void testCreateTopic() {
        // Arrange
        TopicRequestDto topicRequestDto = new TopicRequestDto();
        TopicRequestDto savedTopicRequestDto = new TopicRequestDto();
        ResponseDto<TopicRequestDto> responseDto = new ResponseDto<>();
        responseDto.setCode(HttpStatus.CREATED.value());
        responseDto.setMessage("topic.created.success.message");
        responseDto.setData(savedTopicRequestDto);
        responseDto.setPath(MAIN_PATH);

        when(topicService.createTopic(topicRequestDto)).thenReturn(savedTopicRequestDto);
        when(generateResponseHelper.generateResponse(
                HttpStatus.CREATED.value(), "topic.created.success.message", savedTopicRequestDto, MAIN_PATH))
                .thenReturn(responseDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri();
        // Act
        ResponseEntity<ResponseDto<TopicRequestDto>> response = topicController.createTopic(topicRequestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(topicService, times(1)).createTopic(topicRequestDto);
        verify(generateResponseHelper, times(1))
                .generateResponse(HttpStatus.CREATED.value(), "topic.created.success.message", savedTopicRequestDto, location.getPath());
    }

    @Test
    void testGetTopics() {
        // Arrange
        Set<TopicFetchDto> topics = new HashSet<>();
        ResponseDto<Set<TopicFetchDto>> responseDto = new ResponseDto<>();
        responseDto.setCode(HttpStatus.OK.value());
        responseDto.setMessage("topic.fetched.success.message");
        responseDto.setData(topics);
        responseDto.setPath(MAIN_PATH);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri();

        when(topicService.fetchAllTopics()).thenReturn(topics);
        when(generateResponseHelper.generateResponse(
                HttpStatus.OK.value(), "topic.fetched.success.message", topics, location.getPath()))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<ResponseDto<Set<TopicFetchDto>>> response = topicController.getTopics();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(topicService, times(1)).fetchAllTopics();
        verify(generateResponseHelper, times(1))
                .generateResponse(HttpStatus.OK.value(), "topic.fetched.success.message",
                        topics,
                        location.getPath());
    }

    @Test
    void testDeleteTopic() {
        // Arrange
        String topicName = "testTopic";
        ResponseDto<Object> responseDto = new ResponseDto<>();
        responseDto.setCode(HttpStatus.OK.value());
        responseDto.setMessage("topic.deleted.success.message");
        responseDto.setData(null);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri();
        // When
        when(generateResponseHelper.generateResponse(
                HttpStatus.OK.value(),
                "topic.deleted.success.message",
                null,
                location.getPath()))
                .thenReturn(responseDto);

        doNothing().when(topicService).deleteTopic(topicName);

        // Act
        topicController.deleteTopic(topicName);

        // Assert

        verify(topicService, times(1)).deleteTopic(topicName);

    }
}

