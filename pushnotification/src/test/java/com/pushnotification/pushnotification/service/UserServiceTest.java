package com.pushnotification.pushnotification.service;


import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exception.ResourceNotFoundException;
import com.pushnotification.pushnotification.helper.TopicEntityCreatorHelper;
import com.pushnotification.pushnotification.helper.UserDtoCreatorHelper;
import com.pushnotification.pushnotification.helpers.ConverterHelper;
import com.pushnotification.pushnotification.mapping.UserMapping;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConverterHelper converter;
    @Mock
    private UserMapping userMapping;
    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private UsersServiceImpl userService;


    private UserDto userDto;
    private UserUpdateDto userUpdateDto;
    private UserEntity userEntity;


    @BeforeEach
    public void beforeEach() {
        userDto = UserDtoCreatorHelper.dto();
        userUpdateDto = UserDtoCreatorHelper.updateDto();
        userEntity = new UserEntity();

        userEntity.setUuid(userDto.getUuid());
        userEntity.setCif(userDto.getCif());
        userEntity.setToken(userDto.getToken());
        userEntity.setPlatform(userDto.getPlatform());
        userEntity.setPlatformLanguage(userDto.getPlatformLanguage());
    }

    @Test
    @Order(1)
    @DisplayName("Add User")
    public void givenDto_whenSave_thenReturnUserDto() {
        // Arrange
        userEntity.setTopics(new HashSet<>(TopicEntityCreatorHelper.entityList(10)));
        Set<String> topicList = userEntity.getTopics()
                .stream()
                .map(TopicEntity::getName)
                .collect(Collectors.toSet());
        userDto.setTopics(topicList);

        // When
        when(converter.mapToEntity(userDto, UserEntity.class))
                .thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(topicRepository.findAllByNameIn(new ArrayList<>(topicList)))
                .thenReturn(userEntity.getTopics());
        when(userMapping.toUserDto(userEntity)).thenReturn(userDto);


        // Act
        var savedDto = userService.createUser(userDto);

        // Verify
        verify(userRepository, times(1)).save(userEntity);
        verify(converter, times(1)).mapToEntity(userDto, UserEntity.class);
        verify(topicRepository, times(1)).findAllByNameIn(anyList());

        // Assert
        customerAssertionsForCreate(userEntity, savedDto);
    }

    @Test
    @Order(3)
    @DisplayName("Update User By Cif")
    public void givenDtoAndUserCif_whenUpdate_thenReturnUserDto(){
        // Arrange
        // Arrange
        userEntity.setTopics(new HashSet<>(TopicEntityCreatorHelper.entityList(10)));
        Set<String> topicList = userEntity.getTopics()
                .stream()
                .map(TopicEntity::getName)
                .collect(Collectors.toSet());
        userUpdateDto.setTopics(topicList);

        String cif = "1234567";
        userEntity.setCif(cif);

        // When
        when(userRepository.findByCif(cif))
                .thenReturn(Optional.ofNullable(userEntity));
        when(userMapping.toUserUpdateDto(userEntity))
                .thenReturn(userUpdateDto);
        when(userRepository.save(userEntity))
                .thenReturn(userEntity);
        when(topicRepository.findAllByNameIn(new ArrayList<>(topicList)))
                .thenReturn(userEntity.getTopics());

        // Act
        var savedDto = userService.updateUser(cif, userUpdateDto);

        // Verify
        verify(userRepository, times(1)).findByCif(cif);
        verify(topicRepository, times(1)).findAllByNameIn(anyList());
        verify(userRepository, times(1)).save(userEntity);

        // Assert
        customAssertionForUpdate(userEntity, savedDto);

        // Test does not exists CIF ID.
        String doesNotExists = "1234569";
        userEntity.setCif(doesNotExists);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(doesNotExists, userUpdateDto);
        }, "If there is not company in DB then should be throw exception.");
    }



    private void customerAssertionsForCreate(UserEntity expected, UserDto actual){
        assertNotNull(actual);

        assertEquals(expected.getCif(), actual.getCif());
        assertEquals(expected.getUuid(), actual.getUuid());
        assertEquals(expected.getToken(), actual.getToken());
        assertEquals(expected.getPlatform(), actual.getPlatform());
        assertEquals(expected.getPlatformLanguage(), actual.getPlatformLanguage());

        if(expected.getTopics() != null)
            assertEquals(expected.getTopics().size(), actual.getTopics().size());
    }

    private void customAssertionForUpdate(UserEntity expected, UserUpdateDto actual){
        assertNotNull(actual);

        assertEquals(expected.getUuid(), actual.getUuid());
        assertEquals(expected.getToken(), actual.getToken());
        assertEquals(expected.getPlatform(), actual.getPlatform());
        assertEquals(expected.getPlatformLanguage(), actual.getPlatformLanguage());
    }

}
