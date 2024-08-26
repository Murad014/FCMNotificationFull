package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.dto.TopicDto;

import java.util.Set;

public interface TopicService {
    TopicDto createTopic(TopicDto topicDto);
    void deleteTopic(String name);
    Set<TopicDto> fetchAllTopics();
}
