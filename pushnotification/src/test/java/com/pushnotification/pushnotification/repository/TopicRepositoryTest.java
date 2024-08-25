package com.pushnotification.pushnotification.repository;

import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.helper.TopicEntityCreatorHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @PersistenceContext
    private EntityManager entityManager;
    private TopicEntity entity;
    private List<TopicEntity> entityList;



    @BeforeEach
    public void beforeEach(){
        entity = TopicEntityCreatorHelper.entity();
        entityList = TopicEntityCreatorHelper.entityList(10);
    }


    @Test
    @DisplayName("Add topic")
    @Order(1)
    public void givenEntity_whenSave_thenReturnEntity(){
        // Arrange - beforeEach

        // Act
        var saved = topicRepository.save(entity);
        System.out.println("FUCK: " + saved.getCreatedDate());
        // Assert
        assertions(entity, saved);
    }

    @Test
    @DisplayName("Make DeActive Topic by name")
    @Order(2)
    public void givenTopicName_makeIsActiveFalse(){
        // Assert
        entity.setIsActive(true);
        var saved = topicRepository.save(entity);

        // Act
        saved.setIsActive(false);
        var fromDB = topicRepository.save(saved);

        // Assert
        assertFalse(fromDB.getIsActive());

    }

    @Test
    @DisplayName("Make IsActive Topic by name")
    @Transactional
    @Order(3)
    public void givenTopicName_makeDeActiveTrue(){
        // Assert
        entity.setIsActive(false);
        var saved = topicRepository.save(entity);

        // Act
        saved.setIsActive(true);
        var fromDB = entityManager.merge(saved);


        // Assert
        assertTrue(fromDB.getIsActive());
    }

    @Test
    @DisplayName("Check exists by Topic name")
    @Transactional
    @Order(3)
    public void givenTopicName_checkExists(){
        // Arrange
        entity.setIsActive(true);
        topicRepository.save(entity);

        // Act
        var exists = topicRepository.existsByName(entity.getName());


        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Find All in given list by Topic name")
    @Order(4)
    public void givenTopicNameList_whenFindInList_thenReturnList(){


        // Arrange
        entityList.forEach(e -> e.setIsActive(true));
        topicRepository.saveAll(entityList);
        List<String> givenList = new ArrayList<>(List.of(entityList.get(0).getName(),
                entityList.get(1).getName()));

        // Act
        Set<TopicEntity> result = topicRepository.findAllByNameIn(givenList);

        // Assert
        assertEquals(givenList.size(), result.size());
    }





    private void assertions(TopicEntity expected, TopicEntity actual){
        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertNotNull(expected.getCreatedDate());

        assertNotEquals(expected.getCreatedDate(), "");

        if(!expected.getUsers().isEmpty())
            assertEquals(expected.getUsers().size(), actual.getUsers().size());
    }




}
