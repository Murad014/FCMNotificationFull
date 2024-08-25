package com.pushnotification.pushnotification.helper;


import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserEntityCreatorHelper {

    public static UserEntity entity(){
        Set<TopicEntity> topics = new HashSet<>();
        topics.add(TopicEntityCreatorHelper.entity());

        Platform[] platforms = Platform.values();
        UserEntity entity = new UserEntity();

        entity.setCif(RandomStringUtils.randomNumeric(6));
        entity.setUuid(UUID.randomUUID().toString());
        entity.setToken(RandomStringUtils.randomAlphabetic(200));
        entity.setPlatform(platforms[Integer.parseInt(RandomStringUtils.randomNumeric(4)) % platforms.length]);
        entity.setTopics(topics);
        entity.setPlatformLanguage(PlatformLanguages.AZ);
        entity.setIsActive(RandomGeneratorHelper.randomBoolean());

        return entity;
    }

    public static List<UserEntity> entityList(int size){
        return Stream
                .generate(UserEntityCreatorHelper::entity)
                .limit(size)
                .collect(Collectors.toList());
    }

}
