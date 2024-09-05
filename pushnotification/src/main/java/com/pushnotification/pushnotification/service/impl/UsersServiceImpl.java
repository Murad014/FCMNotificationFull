package com.pushnotification.pushnotification.service.impl;

import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.TopicService;
import com.pushnotification.pushnotification.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UsersServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TopicService topicService;

    @Autowired
    public UsersServiceImpl(UserRepository userRepository,
                            ModelMapper modelMapper,
                            TopicService topicService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.topicService = topicService;
    }

    @Override
    public UserDto createUser(UserDto userDto){
        UserEntity newUser = modelMapper.map(userDto, UserEntity.class);
        return modelMapper.map(userRepository.save(newUser), UserDto.class);
    }

    @Override
    public UserUpdateDto updateUser(String cif, UserUpdateDto userDto) {
        UserEntity findByCif = userRepository.findByCif(cif).orElseThrow(
                () -> new ResourceNotFoundException("User", "cif", cif)
        );
        findByCif.setToken(userDto.getToken());
        findByCif.setPlatform(userDto.getPlatform());
        findByCif.setPlatformLanguage(userDto.getPlatformLanguage());

        var updated = userRepository.save(findByCif);
        return modelMapper.map(updated, UserUpdateDto.class);
    }

    @Override
    public void deleteByCIF(String cif) {
        var findByCif = userRepository.findByCif(cif).orElseThrow(
                () -> new ResourceNotFoundException("User", "cif", cif)
        );
        findByCif.setIsActive(false);
        userRepository.save(findByCif);
    }

    @Override
    public void setTopicsByUserCif(String cif, Set<String> topics) {
        // Check User that exists
        var getUserByCif = userRepository.findByCif(cif).orElseThrow(
                () -> new ResourceNotFoundException("User", "cif", cif)
        );

        // Concat topicNames to user's device lang
        topics = topics
                .stream()
                .map(name -> name.toUpperCase()
                        .concat("_")
                        .concat(getUserByCif.getPlatformLanguage().toString()))
                .collect(Collectors.toSet());

        var getAllTopicsInGivenList = topicService.findAllTopicsInGivenTopicList(topics);

        // Check Topics exist
        topicService.checkAllGivenTopicsInDB(topics, getAllTopicsInGivenList);

        // Set Topics to User
        getUserByCif.setTopics(getAllTopicsInGivenList);

        // Save the User
        userRepository.save(getUserByCif);
    }


}