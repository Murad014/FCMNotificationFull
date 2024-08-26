package com.pushnotification.pushnotification.helper;


import com.pushnotification.pushnotification.dto.TopicDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopicEntityCreatorHelper {

    public static TopicEntity entity(){
        TopicEntity entity = new TopicEntity();

        entity.setName(RandomStringUtils.randomAlphabetic(5));
        entity.setDescription("Kompaniyalar");
        entity.setIsActive(RandomGeneratorHelper.randomBoolean());

        return entity;
    }

    public static TopicDto dto(){
        var dto = new TopicDto();
        dto.setName(RandomStringUtils.randomAlphabetic(5));
        dto.setDescription(RandomStringUtils.randomAlphabetic(5));

        return dto;
    }

    public static List<TopicDto> dtoList(int size){
        return Stream
                .generate(TopicEntityCreatorHelper::dto)
                .limit(size)
                .collect(Collectors.toList());
    }

    public static List<TopicEntity> entityList(int size){
        return Stream
                .generate(TopicEntityCreatorHelper:: entity)
                .limit(size)
                .collect(Collectors.toList());
    }

}
