package com.pushnotification.pushnotification.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceReaderHelper {
    private final MessageSource messageSource;

    @Autowired
    public MessageSourceReaderHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String messageCode) {

        return messageSource.getMessage(
                messageCode,
                null,
                LocaleContextHolder.getLocale());
    }
}
