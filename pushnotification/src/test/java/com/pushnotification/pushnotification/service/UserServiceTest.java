package com.pushnotification.pushnotification.service;

import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.helper.TopicEntityCreatorHelper;
import com.pushnotification.pushnotification.helper.UserDtoCreatorHelper;
import com.pushnotification.pushnotification.helper.UserEntityCreatorHelper;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.impl.TopicServiceImpl;
import com.pushnotification.pushnotification.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private TopicServiceImpl topicServiceImpl;

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

        userEntity.setCif(userDto.getCif());
        userEntity.setToken(userDto.getToken());
        userEntity.setPlatform(userDto.getPlatform());
        userEntity.setPlatformLanguage(userDto.getPlatformLanguage());
    }

    @Test
    @DisplayName("Create User")
    @Order(1)
    public void givenDto_whenSave_thenReturnDto() {
        when(modelMapper.map(userDto, UserEntity.class)).thenReturn(userEntity);
        when(modelMapper.map(userEntity, UserDto.class)).thenReturn(userDto);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Act
        UserDto saved = userService.createUser(userDto);

        // Verify
        verify(modelMapper, times(1)).map(userDto, UserEntity.class);
        verify(modelMapper, times(1)).map(userEntity, UserDto.class);
        verify(userRepository, times(1)).save(userEntity);

        // Assert
        assertNotNull(saved);

    }

    @Test
    @DisplayName("Update User by CIF")
    @Order(2)
    public void givenCifAndDto_whenFindByCifAndUpdate_thenReturnDto() {
        // Arrange
        var cif = "131543";

        // When
        when(userRepository.findByCif(cif)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(modelMapper.map(userEntity, UserUpdateDto.class)).thenReturn(userUpdateDto);

        // Act
        var updated = userService.updateUser(cif, userUpdateDto);

        // Verify
        verify(userRepository).findByCif(cif);
        verify(userRepository).save(userEntity);
        verify(modelMapper).map(userEntity, UserUpdateDto.class);
        assertNotNull(updated);

    }

    @Test
    @Order(3)
    @DisplayName("Update User by Cif that does not Exists")
    public void givenUserCifThatDoesNotExists_whenCannotFind_thenReturnException() {
        // Arrange
        var cif = "131543";

        // When
        when(userRepository.findByCif(cif)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(cif, userUpdateDto), "If there is not user by cif in DB then should be throw exception.");

        // Verify
        verify(userRepository).findByCif(cif);
        verify(userRepository, times(0)).save(userEntity);
        verify(modelMapper, times(0)).map(userEntity, UserUpdateDto.class);

    }

    @Test
    @Order(4)
    @DisplayName("Make Is Active False User by CIF")
    public void givenCif_whenFind_thenMakeIsActiveFalse() {
        // Arrange
        final String cif = "1234567";
        userEntity.setCif(cif);
        userEntity.setIsActive(true);

        // When
        when(userRepository.findByCif(cif)).thenReturn(Optional.ofNullable(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Act
        userService.deleteByCIF(cif);

        // Verify
        verify(userRepository, times(1)).findByCif(cif);
    }

    @Test
    @Order(5)
    @DisplayName("Make Is Active False User by CIF that cif does not exist")
    public void givenDoesNotExistCif_whenCannotFind_thenReturnException(){
        // Arrange
        final String cif = "1234567";
        userEntity.setCif(cif);
        userEntity.setIsActive(true);

        // When
        when(userRepository.findByCif(cif)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteByCIF(cif), "If there is not user by cif in DB then should be throw exception.");


        // Verify
        verify(userRepository, times(1)).findByCif(cif);
        verify(userRepository, times(0)).save(userEntity);
    }

    @Test
    @DisplayName("Set User's topics by CIF")
    void testSetTopicsByUserCif_UserExists_TopicsValid() {
        // Arrange
        var topicEntity = UserEntityCreatorHelper.entity();
        var topics = new HashSet<String>();
        topics.add("Topic1");
        topics.add("Topic2");

        // When
        when(userRepository.findByCif(topicEntity.getCif())).thenReturn(Optional.of(topicEntity));
        when(topicServiceImpl.findAllTopicsInGivenTopicList(topics)).thenReturn(
                new HashSet<>(TopicEntityCreatorHelper.entityList(2))
        );
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        doNothing().when(topicServiceImpl).checkAllGivenTopicsInDB(anySet(), anySet());

        // Act
        userService.setTopicsByUserCif(topicEntity.getCif(), topics);

        // Verify
        verify(userRepository, times(1)).findByCif(topicEntity.getCif());
        verify(topicServiceImpl, times(1)).findAllTopicsInGivenTopicList(anySet());
        verify(topicServiceImpl, times(1)).checkAllGivenTopicsInDB(anySet(), anySet());
        verify(userRepository, times(1)).save(topicEntity);
    }

    @Test
    void testSetTopicsByUserCif_UserNotFound_ThrowsException() {
        // Arrange
        String cif = "123456";
        Set<String> topics = new HashSet<>();
        topics.add("Topic1");
        topics.add("Topic2");

        when(userRepository.findByCif(cif)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.setTopicsByUserCif(cif, topics);
        });
    }



}
