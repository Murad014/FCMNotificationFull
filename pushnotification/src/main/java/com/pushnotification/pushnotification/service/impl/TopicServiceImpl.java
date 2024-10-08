package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.request.TopicRequestDto;
import com.pushnotification.pushnotification.dto.TopicFetchDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.exceptions.WrongRequestBodyException;
import com.pushnotification.pushnotification.helpers.MessageSourceReaderHelper;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final ModelMapper modelMapper;
    private final MessageSourceReaderHelper messageSourceReaderHelper;

    private static final String MISSING_LANGUAGE_MESSAGE = "missing.language.key.error";

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository,
                            ModelMapper modelMapper,
                            MessageSourceReaderHelper messageSourceReaderHelper) {
        this.topicRepository = topicRepository;
        this.modelMapper = modelMapper;
        this.messageSourceReaderHelper = messageSourceReaderHelper;
    }


    @Override
    public TopicRequestDto createTopic(TopicRequestDto topicRequestDto) {
        // Check Topic Description with All Languages
        checkAllLanguagesKeys(topicRequestDto);

        // Create given topic for all Languages
        var bulkSaveTopics = new ArrayList<TopicEntity>();
        for(var lang: PlatformLanguages.values()) {
            final var topicNameWithLang = topicRequestDto
                    .getName()
                    .concat("_")
                    .concat(lang.toString());
            var convertToEntity = modelMapper.map(topicRequestDto, TopicEntity.class);
            convertToEntity.setName(topicNameWithLang);
            convertToEntity.setDescription(topicRequestDto.getDescription().get(lang));

            bulkSaveTopics.add(convertToEntity);
        }
        topicRepository.saveAll(bulkSaveTopics); // Saved for all Languages

        return topicRequestDto;
    }

    @Override
    public void deleteTopic(String name) {
        for(var lang: PlatformLanguages.values()) {
            final var topicNameWithLang = name
                    .toUpperCase()
                    .concat("_")
                    .concat(lang.toString());

            var findByTopicName = topicRepository.findByName(topicNameWithLang).orElse(null);
            if(findByTopicName != null) {
                findByTopicName.setIsActive(false);
                topicRepository.save(findByTopicName);
            }

            log.info("Topic From DB: {}", findByTopicName);
        }
    }

    @Override
    public Set<TopicFetchDto> fetchAllTopics() {

        return  topicRepository
                .findAll()
                .stream()
                .map(topicEntity ->modelMapper.map(topicEntity, TopicFetchDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<TopicEntity> findAllTopicsInGivenTopicList(Set<String> givenTopics) {
        return topicRepository.findAllByNameIn(new ArrayList<>(givenTopics));
    }

    @Override
    public void checkAllGivenTopicsInDB(Set<String> givenTopics,
                                        Set<TopicEntity> fromDB) {

        Set<String> copyOfGivenTopics = new HashSet<>(givenTopics);

        Set<String> getTopicNames = fromDB
                .stream()
                .map(TopicEntity::getName)
                .collect(Collectors.toSet());

        copyOfGivenTopics.removeAll(getTopicNames); // Eliminate
        if( ! copyOfGivenTopics.isEmpty()) {
            copyOfGivenTopics =
                    copyOfGivenTopics.stream().map(topic ->
                            topic.substring(0, topic.lastIndexOf('_'))).collect(Collectors.toSet());
            throw new ResourceNotFoundException("Topics", "topics", copyOfGivenTopics.toString());
        }

    }

    private void checkAllLanguagesKeys(TopicRequestDto topicRequestDto){
        for (var lang : PlatformLanguages.values()) {
            if(!topicRequestDto.getDescription().containsKey(lang))
                throw new WrongRequestBodyException(messageSourceReaderHelper
                        .getMessage(MISSING_LANGUAGE_MESSAGE).concat(" " + lang.toString()));
        }
    }


}
