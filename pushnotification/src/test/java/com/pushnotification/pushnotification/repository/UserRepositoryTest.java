package com.pushnotification.pushnotification.repository;


import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.helper.UserEntityCreatorHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {
    private UserEntity entity;
    private List<UserEntity> entityList;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    private static final int SIZE_LIST = 10;


    @BeforeEach
    public void beforeEach(){
        entity = UserEntityCreatorHelper.entity();
        entityList = UserEntityCreatorHelper.entityList(SIZE_LIST);
        topicRepository.saveAll(entity.getTopics());

        entityList.forEach(entity -> topicRepository.saveAll(entity.getTopics()));
    }



    @Test
    @Order(1)
    @DisplayName("Save Entity")
    public void givenEntity_whenSave_thenReturnEntity(){
        // Arrange - before each

        // Act
        UserEntity savedEntity = userRepository.save(entity);

        // Assert
        assertionsOfEntity(entity, savedEntity);
    }

    @Test
    @Order(2)
    @DisplayName("Update Entity")
    @Transactional
    public void givenEntityIdAndEntity_whenUpdate_thenReturnEntity(){
        // Arrange
        final String uuid = UUID.randomUUID().toString();
        final String cif = RandomStringUtils.randomNumeric(6);
       // entity.setIsActive(true);
        var savedEntity = userRepository.save(entity);
        savedEntity.setUuid(uuid);
        savedEntity.setPlatform(Platform.ANDROID);
        savedEntity.setCif(cif);

        // Act
        var updatedEntity = userRepository.save(savedEntity);

        // Assert
        assertNotNull(updatedEntity);
        assertEquals(updatedEntity.getId(), savedEntity.getId());
        assertEquals(uuid, updatedEntity.getUuid());
        assertEquals(cif, updatedEntity.getCif());
        assertEquals(Platform.ANDROID, updatedEntity.getPlatform());
    }


    @Test
    @DisplayName("Save All")
    @Order(3)
    public void givenEntityList_whenSave_thenReturnEntityList(){
        // Arrange - beforeEach

        // Act
        var saveAll = userRepository.saveAll(entityList);

        // Assert
        assertNotNull(saveAll);
        assertFalse(saveAll.isEmpty());
        for(int i = 0; i < saveAll.size(); i++)
            assertionsOfEntity(entityList.get(i), saveAll.get(i));

    }

    @RepeatedTest(5)
    @DisplayName("Find All By Cif Which in given Cif List")
    @Order(4)
    public void givenCifList_returnListEntities_whereInTable(){
        final String doesNotExistsCif = "12414132";

        // Arrange
        userRepository.saveAll(entityList);
        Set<String> givenCifList = new HashSet<>();
        givenCifList.add(entityList.get(0).getCif());
        givenCifList.add(entityList.get(2).getCif());
        givenCifList.add(doesNotExistsCif); // does not exist one
        givenCifList.add(entityList.get(4).getCif());
        givenCifList.add(entityList.get(6).getCif());
        int expectedSize = (int) entityList
                .stream()
                .filter(entity -> givenCifList.contains(entity.getCif()) && entity.getIsActive()).count();


        // Act
        var getFromDB = userRepository.findAllByCifIn(new ArrayList<>(givenCifList));

        // Assert
        assertFalse(getFromDB.stream().anyMatch(s -> s.getCif().equals(doesNotExistsCif)));
        assertEquals(expectedSize, getFromDB.size());

    }

    @Test
    @DisplayName("Find All Users")
    @Order(5)
    public void findAllUserEntities(){
        // Arrange
        userRepository.saveAll(entityList);

        // Act
        List<UserEntity> usersFromDB = userRepository.findAll();

        // Arrange
        assertFalse(usersFromDB.isEmpty());
        assertFalse(usersFromDB.stream().anyMatch(entity -> !entity.getIsActive()));
    }

    @Test
    @DisplayName("DeActive the User by Token")
    @Order(6)
    public void givenToken_updateIsActiveFalse(){
        // Arrange
        entity.setIsActive(true);
        var saved = userRepository.save(entity);
        assertTrue(saved.getIsActive());
        var fromDB = userRepository.findByToken(saved.getToken()).orElse(null);
        assert fromDB != null;

        // Act
        fromDB.setIsActive(false);
        var update = userRepository.save(fromDB);

        // Assert
        assertNotNull(update);
        assertFalse(update.getIsActive());
        // check that is active is False entity does not find
        assertFalse(userRepository.findByToken(update.getToken()).isPresent());
    }


    @Test
    @DisplayName("Active the User by Token")
    @Transactional
    @Order(7)
    public void givenToken_updateIsActiveTrue(){
        // Arrange
        entity.setIsActive(false);
        var saved = userRepository.save(entity);
        assertFalse(saved.getIsActive());

        // Act
        saved.setIsActive(true);
        var update = userRepository.save(saved);

        // Assert
        assertNotNull(update);
        assertTrue(update.getIsActive());
        // check that is active is False entity does not find
        assertTrue(userRepository.findByToken(update.getToken()).isPresent());
    }

    @Test
    @DisplayName("Exists User By Cif")
    @Order(8)
    public void givenCif_whenFind_thenReturnTrue_otherwiseReturnFalse(){
        // Arrange
        entity.setIsActive(true);
        userRepository.save(entity);

        // Act
        boolean exists = userRepository.existsByCif(entity.getCif());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Exists User By Token")
    @Order(8)
    public void givenToken_whenFind_thenReturnTrue_otherwiseReturnFalse(){
        // Arrange
        entity.setIsActive(true);
        userRepository.save(entity);

        // Act
        boolean exists = userRepository.existsByToken(entity.getToken());

        // Assert
        assertTrue(exists);
    }




    private void assertionsOfEntity(UserEntity expected, UserEntity actual){
        assertNotNull(actual);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCif(), actual.getCif());
        assertEquals(expected.getUuid(), actual.getUuid());
        assertEquals(expected.getToken(), actual.getToken());
        assertEquals(expected.getPlatform(), actual.getPlatform());

        assertEquals(expected.getCreatedDate(), actual.getCreatedDate());
        assertNotEquals(expected.getCreatedDate(), "");

        assertEquals(expected.getTopics().size(), actual.getTopics().size());

    }

}
