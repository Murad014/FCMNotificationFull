package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.TopicDto;
import com.pushnotification.pushnotification.entity.TopicEntity;

import java.util.List;
import java.util.Set;

public interface TopicService {
    TopicDto createTopic(TopicDto topicDto);
    void deleteTopic(String name);
    Set<TopicDto> fetchAllTopics();

    Set<TopicEntity> findAllTopicsInGivenTopicList(Set<String> givenTopics);
    void checkAllGivenTopicsInDB(Set<String> givenTopics,
                                        Set<TopicEntity> fromDB);

}
