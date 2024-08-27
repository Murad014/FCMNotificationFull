package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.TopicDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final ModelMapper modelMapper;

    public TopicServiceImpl(TopicRepository topicRepository, ModelMapper modelMapper) {
        this.topicRepository = topicRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public TopicDto createTopic(TopicDto topicDto) {
        // Create given topic for all Languages
        for(var lang: PlatformLanguages.values()) {
            final var topicNameWithLang = topicDto
                    .getName()
                    .concat("_")
                    .concat(lang.toString().toLowerCase());
            var convertToEntity = modelMapper.map(topicDto, TopicEntity.class);

            convertToEntity.setName(topicNameWithLang);
            topicRepository.save(convertToEntity);
        }

        return topicDto;
    }

    @Override
    public void deleteTopic(String name) {
        for(var lang: PlatformLanguages.values()) {
            final var topicNameWithLang = name.concat("_").concat(lang.toString().toLowerCase());
            var findByTopicName = topicRepository.findByName(topicNameWithLang).orElse(null);
            if(findByTopicName != null) {
                findByTopicName.setIsActive(false);
                topicRepository.save(findByTopicName);
            }

            log.info("Topic From DB: {}", findByTopicName);
        }
    }

    @Override
    public Set<TopicDto> fetchAllTopics() {
        return topicRepository
                .findAll()
                .stream()
                .map(topicEntity -> modelMapper.map(topicEntity, TopicDto.class))
                .collect(Collectors.toSet());
    }


}
