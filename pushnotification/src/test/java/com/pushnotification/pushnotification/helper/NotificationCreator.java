package com.pushnotification.pushnotification.helper;

import com.pushnotification.pushnotification.entity.NotificationEntity;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NotificationCreator {

    public static NotificationEntity entity(){
        NotificationEntity entity = new NotificationEntity();

        entity.setTitle(RandomStringUtils.randomAlphabetic(10));
        entity.setBody(RandomStringUtils.randomAlphabetic(50));
        entity.setImageUrl(RandomStringUtils.randomAlphabetic(20));
        entity.setIconUrl(RandomStringUtils.randomAlphabetic(20));
        entity.setSubtitle(RandomStringUtils.randomAlphabetic(20));
        entity.setSentToMq(Integer.parseInt(RandomStringUtils.randomNumeric(2)) % 2 == 0);
        entity.setSentToFcm(Integer.parseInt(RandomStringUtils.randomNumeric(2)) % 2 == 0);

        return entity;
    }

    public static Set<NotificationEntity> entityList(int size) {
        return Stream
                .generate(NotificationCreator:: entity)
                .limit(size)
                .collect(Collectors.toSet());
    }

}
