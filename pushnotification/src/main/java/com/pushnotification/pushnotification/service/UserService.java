package com.pushnotification.pushnotification.service;


import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;

import java.util.Set;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserUpdateDto updateUser(String cif, UserUpdateDto userDto);
    void deleteByCIF(String cif);
    void setTopicsByUserCif(String cif, Set<String> topics);

}
