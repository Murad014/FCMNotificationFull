package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exception.ResourceNotFoundException;
import com.pushnotification.pushnotification.helpers.ConverterHelper;
import com.pushnotification.pushnotification.mapping.UserMapping;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UsersServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConverterHelper converter;
    private final TopicRepository topicRepository;
    private final UserMapping userMapping;

    @Autowired
    public UsersServiceImpl(UserRepository userRepository,
                            ConverterHelper converter,
                            TopicRepository topicRepository, UserMapping userMapping) {
        this.userRepository = userRepository;
        this.converter = converter;
        this.topicRepository = topicRepository;
        this.userMapping = userMapping;
    }

    @Override
    public UserDto createUser(UserDto userDto){
        UserEntity newUser = converter.mapToEntity(userDto, UserEntity.class);

        if(userDto.getTopics() != null && !userDto.getTopics().isEmpty()) {
            Set<TopicEntity> topicsFromDB = topicRepository.findAllByNameIn(new ArrayList<>(userDto.getTopics()));
            checkAllTopicsExistsInDBIfNotThenThrowException(topicsFromDB, userDto.getTopics());
            newUser.setTopics(topicsFromDB);
        }

        UserEntity saved = userRepository.save(newUser);
        return userMapping.toUserDto(saved);
    }

    @Override
    public UserUpdateDto updateUser(String cif, UserUpdateDto userDto) {
        UserEntity findByCif = userRepository.findByCif(cif).orElseThrow(
                () -> new ResourceNotFoundException("User", "cif", cif)
        );

        if(userDto.getTopics() != null) {
            Set<TopicEntity> topicsFromDB = topicRepository.findAllByNameIn(new ArrayList<>(userDto.getTopics()));
            checkAllTopicsExistsInDBIfNotThenThrowException(topicsFromDB, userDto.getTopics());
            findByCif.setTopics(topicsFromDB);
        }

        findByCif.setToken(userDto.getToken());
        findByCif.setUuid(userDto.getUuid());
        findByCif.setPlatform(userDto.getPlatform());
        findByCif.setPlatformLanguage(userDto.getPlatformLanguage());

        return  userMapping.toUserUpdateDto(userRepository.save(findByCif));

    }

    private void checkAllTopicsExistsInDBIfNotThenThrowException(Set<TopicEntity> topicsFromDB,
                                                                       Set<String> topicsFromClient) {
        Set<String> newRef = new HashSet<>(topicsFromClient); // For unit test mockito
        if(!newRef.isEmpty()) {
            Set<String> getJustNamesFromDB = topicsFromDB
                    .stream()
                    .map(TopicEntity::getName)
                    .collect(Collectors.toSet());

            newRef.removeAll(getJustNamesFromDB);
            if (!newRef.isEmpty())
                throw new ResourceNotFoundException("Topics", "topics", newRef.toString());

        }
    }



}