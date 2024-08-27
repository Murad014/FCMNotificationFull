package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.helpers.ConverterHelper;
import com.pushnotification.pushnotification.mapping.UserMapping;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
            var expandLangList = expandLanguages(userDto.getTopics(), userDto.getPlatformLanguage());
            Set<TopicEntity> topicsFromDB = topicRepository.findAllByNameIn(expandLangList);
            checkAllTopicsExistsInDBIfNotThenThrowException(topicsFromDB, new HashSet<>(expandLangList));
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
            var expandLangList = expandLanguages(userDto.getTopics(), userDto.getPlatformLanguage());
            Set<TopicEntity> topicsFromDB = topicRepository.findAllByNameIn(expandLangList);
            checkAllTopicsExistsInDBIfNotThenThrowException(topicsFromDB, new HashSet<>(expandLangList));
            findByCif.setTopics(topicsFromDB);
        }

        findByCif.setToken(userDto.getToken());
        findByCif.setPlatform(userDto.getPlatform());
        findByCif.setPlatformLanguage(userDto.getPlatformLanguage());

        return  userMapping.toUserUpdateDto(userRepository.save(findByCif));

    }

    @Override
    public void deleteByCIF(String cif) {
        var findByCif = userRepository.findByCif(cif).orElseThrow(
                () -> new ResourceNotFoundException("User", "cif", cif)
        );

        findByCif.setIsActive(false);

        userRepository.save(findByCif);
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

    // topics: news => news_az (az is device app lang)
    private List<String> expandLanguages(Set<String> languages, PlatformLanguages platformLanguage){
        var list = new ArrayList<String>();
        languages.forEach(language -> list.add(language.concat("_" + platformLanguage.toString().toLowerCase())));

        return list;
    }



}