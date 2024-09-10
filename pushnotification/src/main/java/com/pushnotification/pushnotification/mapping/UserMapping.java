package com.pushnotification.pushnotification.mapping;

import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class UserMapping {

    public UserDto toUserDto(UserEntity userEntity) {
        var userDto = new UserDto();
        userDto.setCif(userDto.getCif());
        userDto.setToken(userEntity.getToken());
        userDto.setPlatform(userEntity.getPlatform());
        //userDto.setTopics(new HashSet<>());
        userDto.setPlatformLanguage(userEntity.getPlatformLanguage());


        return userDto;
    }


    public UserUpdateDto toUserUpdateDto(UserEntity userEntity) {
        var userUpdateDto = new UserUpdateDto();
        userUpdateDto.setToken(userEntity.getToken());
        userUpdateDto.setPlatform(userEntity.getPlatform());
        userUpdateDto.setTopics(new HashSet<>());
        userUpdateDto.setPlatformLanguage(userEntity.getPlatformLanguage());

        userEntity.getTopics().forEach(topicEntity -> userUpdateDto.getTopics().add(topicEntity.getName()));

        return userUpdateDto;
    }


}
