package com.pushnotification.pushnotification.service.impl;


import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UsersServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UsersServiceImpl(UserRepository userRepository,
                            ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
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
}