package com.pushnotification.pushnotification.helper;


import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class UserDtoCreatorHelper {
    public static UserDto dto() {
        UserDto userDto = new UserDto();
        userDto.setCif(RandomStringUtils.randomNumeric(7));
        userDto.setPlatform(RandomGeneratorHelper.randomBoolean() ? Platform.IOS : Platform.ANDROID);
        userDto.setPlatformLanguage(PlatformLanguages.AZ);
        userDto.setToken(RandomStringUtils.randomAlphanumeric(80));

        return userDto;
    }

    public static UserUpdateDto updateDto() {
        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setUuid(UUID.randomUUID().toString());
        userDto.setPlatform(RandomGeneratorHelper.randomBoolean() ? Platform.IOS : Platform.ANDROID);
        userDto.setPlatformLanguage(PlatformLanguages.AZ);
        userDto.setToken(RandomStringUtils.randomAlphanumeric(80));

        return userDto;
    }
}
