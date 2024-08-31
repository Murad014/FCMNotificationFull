package com.pushnotification.pushnotification.service;


import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.TopicDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.exceptions.ResourceNotFoundException;
import com.pushnotification.pushnotification.helper.TopicEntityCreatorHelper;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.service.impl.TopicServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TopicServiceTest {
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TopicServiceImpl topicService;

    private TopicEntity topicEntity;
    private TopicDto topicDto;
    private List<TopicEntity> topicEntities;
    private List<TopicDto> dtoList;

    @BeforeEach
    public void beforeEach(){
        topicEntity = TopicEntityCreatorHelper.entity();
        topicDto = TopicEntityCreatorHelper.dto();
        topicEntities = TopicEntityCreatorHelper.entityList(5);
        dtoList = TopicEntityCreatorHelper.dtoList(5);
    }

    @Test
    @DisplayName("Add Topic")
    @Order(1)
    public void givenDto_whenSave_returnEntity(){
        topicDto.setName(topicEntity.getName().concat("_az"));
        // When
        when(modelMapper.map(topicDto, TopicEntity.class)).thenReturn(topicEntity);
        when(topicRepository.save(topicEntity)).thenReturn(topicEntity);
        when(modelMapper.map(topicEntity, TopicDto.class)).thenReturn(topicDto);

        // Act
        var saved = topicService.createTopic(topicDto);

        // Verify
        verify(topicRepository, times(3)).save(topicEntity);
        verify(modelMapper, times(3)).map(topicDto, TopicEntity.class);

        // Assert
        assertNotNull(saved);
        assertTrue(
                saved.getName().endsWith("_az") ||
                saved.getName().endsWith("_en") ||
                saved.getName().endsWith("_ru")
        );
    }

    @Test
    @DisplayName("Delete Topic (make is_actives false)")
    @Order(2)
    public void givenDto_delete(){
        final var testName = "test";

        // When
        for(var lang: PlatformLanguages.values()) {
            final var topicNameWithLang = testName.concat("_").concat(lang.toString());
            topicEntity.setName(topicNameWithLang);
            when(topicRepository.findByName(topicNameWithLang)).thenReturn(Optional.of(topicEntity));
            when(topicRepository.save(topicEntity)).thenReturn(topicEntity);

        }

        // Act
        topicService.deleteTopic(testName);

        // Verify
        verify(topicRepository, times(3)).findByName(anyString());
        verify(topicRepository, times(3)).save(topicEntity);
    }

    @Test
    @DisplayName("Fetch All Topics")
    @Order(3)
    public void givenTopics_whenFetchAllTopics_thenReturnTopicDtos() {
        // Mocking
        when(topicRepository.findAll()).thenReturn(List.of(topicEntities.get(0), topicEntities.get(1)));
        when(modelMapper.map(topicEntities.get(0), TopicDto.class)).thenReturn(dtoList.get(1));
        when(modelMapper.map(topicEntities.get(1), TopicDto.class)).thenReturn(dtoList.get(1));

        // Act
        Set<TopicDto> result = topicService.fetchAllTopics();

        // Verify
        verify(topicRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(), any());
    }

    @Test
    @DisplayName("Find All Topics in given topics list")
    @Order(4)
    void testFindAllTopicsInGivenTopicList() {
        // Arrange
        var topic01 = new TopicEntity();
        topic01.setName("Topic1");
        var topic02 = new TopicEntity();
        topic02.setName("Topic2");

        Set<String> givenTopics = new HashSet<>(Arrays.asList("Topic1", "Topic2", "Topic3"));
        Set<TopicEntity> mockTopics = new HashSet<>(Arrays.asList(topic01, topic02));
        Mockito.when(topicRepository.findAllByNameIn(anyList())).thenReturn(mockTopics);

        // Act
        Set<TopicEntity> result = topicService.findAllTopicsInGivenTopicList(givenTopics);

        // Assert
        assertEquals(2, result.size());
        assertEquals(new HashSet<>(mockTopics), result);
        verify(topicRepository, Mockito.times(1)).findAllByNameIn(new ArrayList<>(givenTopics));
    }

    @DisplayName("Check Given All Topics in DB")
    @Test
    void testCheckAllGivenTopicsInDB_AllTopicsPresent() {
        // Arrange
        var topic01 = new TopicEntity();
        topic01.setName("Topic1");
        var topic02 = new TopicEntity();
        topic02.setName("Topic2");
        var topic03 = new TopicEntity();
        topic03.setName("Topic3");

        Set<String> givenTopics = new HashSet<>(Arrays.asList("Topic1", "Topic2", "Topic3"));
        Set<TopicEntity> fromDB = new HashSet<>(Arrays.asList(topic01, topic02, topic03));

        // Act & Assert (No exception should be thrown)
        topicService.checkAllGivenTopicsInDB(givenTopics, fromDB);
    }

    @DisplayName("Check Given All Topics not in DB")
    @Test
    void testCheckAllGivenTopicsInDB_SomeTopicsMissing() {
        // Arrange
        var topic01 = new TopicEntity();
        topic01.setName("Topic1");
        var topic02 = new TopicEntity();
        topic02.setName("Topic2");
        var topic03 = new TopicEntity();
        topic03.setName("Topic3");

        Set<String> givenTopics = new HashSet<>(Arrays.asList("Topic1", "Topic2", "Topic4"));
        Set<TopicEntity> fromDB = new HashSet<>(Arrays.asList(topic01, topic02, topic03));

        // Act & Assert (Exception should be thrown)
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> topicService.checkAllGivenTopicsInDB(givenTopics, fromDB)
        );

        assertEquals("Topics", exception.getResourceName());
        assertEquals("topics", exception.getFieldName());
        assertEquals("[Topic4]", exception.getFieldValue());
    }



}
