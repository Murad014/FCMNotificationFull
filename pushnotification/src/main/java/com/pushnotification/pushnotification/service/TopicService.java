package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.dto.request.TopicRequestDto;
import com.pushnotification.pushnotification.dto.TopicFetchDto;
import com.pushnotification.pushnotification.entity.TopicEntity;

import java.util.Set;

public interface TopicService {
    TopicRequestDto createTopic(TopicRequestDto topicRequestDto);
    void deleteTopic(String name);
    Set<TopicFetchDto> fetchAllTopics();

    Set<TopicEntity> findAllTopicsInGivenTopicList(Set<String> givenTopics);
    void checkAllGivenTopicsInDB(Set<String> givenTopics,
                                        Set<TopicEntity> fromDB);

}
