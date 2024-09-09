package com.pushnotification.pushnotification.helper;


import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.request.TopicRequestDto;
import com.pushnotification.pushnotification.entity.TopicEntity;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.List;
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

    public static TopicRequestDto dto(){
        var dto = new TopicRequestDto();
        var hashMap = new HashMap<PlatformLanguages, String>();
        hashMap.put(PlatformLanguages.AZ, RandomStringUtils.randomAlphabetic(5));
        hashMap.put(PlatformLanguages.EN, RandomStringUtils.randomAlphabetic(5));
        hashMap.put(PlatformLanguages.RU, RandomStringUtils.randomAlphabetic(5));

        dto.setName(RandomStringUtils.randomAlphabetic(5));
        dto.setDescription(hashMap);

        return dto;
    }

    public static List<TopicRequestDto> dtoList(int size){
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
