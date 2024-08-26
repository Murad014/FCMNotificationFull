package com.pushnotification.pushnotification.service;


import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserUpdateDto updateUser(String cif, UserUpdateDto userDto);
    void deleteByCIF(String cif);
}
