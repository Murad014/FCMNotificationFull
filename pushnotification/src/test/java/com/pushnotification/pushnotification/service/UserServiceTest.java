package com.pushnotification.pushnotification.service;


import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
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

import java.util.*;
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

        var expandLangList = expandLanguages(topicList, userDto.getPlatformLanguage());
        Set<TopicEntity> tt = new HashSet<>();
        expandLangList.forEach(expandLang -> {
            var t = new TopicEntity();
            t.setName(expandLang);
            tt.add(t);

        });
        userEntity.setTopics(tt);


        // When
        when(converter.mapToEntity(userDto, UserEntity.class))
                .thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(topicRepository.findAllByNameIn(expandLangList))
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
    public void givenDtoAndUserCif_whenUpdate_thenReturnUserDto() {
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
        when(topicRepository.findAllByNameIn(anyList()))
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
        }, "If there is not user in DB then should be throw exception.");
    }

    @Test
    @Order(4)
    @DisplayName("Make is Active True User by CIF that does not exist in DB")
    public void givenCif_whenDoesNotFind_thenReturnException(){
        // Arrange
        final String cif = "1234567";
        userEntity.setCif(cif);
        userEntity.setIsActive(true);

        // When
        when(userRepository.findByCif(cif)).thenReturn(Optional.ofNullable(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);


        // When Throw
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteByCIF("1312322");
        }, "If there is not user in DB then should be throw exception.");

        // Verify
        verify(userRepository, times(1)).findByCif("1312322");
    }

    @Test
    @Order(5)
    @DisplayName("Make Is Active False User by CIF")
    public void givenCif_whenSave_thenReturnNothing(){
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


    private void customerAssertionsForCreate(UserEntity expected, UserDto actual){
        assertNotNull(actual);

        assertEquals(expected.getCif(), actual.getCif());
        assertEquals(expected.getToken(), actual.getToken());
        assertEquals(expected.getPlatform(), actual.getPlatform());
        assertEquals(expected.getPlatformLanguage(), actual.getPlatformLanguage());

        if(expected.getTopics() != null)
            assertEquals(expected.getTopics().size(), actual.getTopics().size());
    }

    private void customAssertionForUpdate(UserEntity expected, UserUpdateDto actual){
        assertNotNull(actual);

        assertEquals(expected.getToken(), actual.getToken());
        assertEquals(expected.getPlatform(), actual.getPlatform());
        assertEquals(expected.getPlatformLanguage(), actual.getPlatformLanguage());
    }

    // topics: news => news_az (az device app lang)
    private List<String> expandLanguages(Set<String> languages, PlatformLanguages platformLanguage){
        var list = new ArrayList<String>();
        languages.forEach(language -> {
            list.add(language.concat("_"+platformLanguage.toString().toLowerCase()));
        });

        return list;
    }

}
