package com.pushnotification.pushnotification.helper;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomGeneratorHelper {

    public static boolean randomBoolean(){
        return Integer.parseInt(RandomStringUtils.randomNumeric(3)) % 2 == 0;
    }




}
