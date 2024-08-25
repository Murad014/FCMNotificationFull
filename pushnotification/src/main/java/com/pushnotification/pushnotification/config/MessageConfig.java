package com.pushnotification.pushnotification.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:languages/messages");
        messageSource.setDefaultLocale(Locale.forLanguageTag("ru"));
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }

}