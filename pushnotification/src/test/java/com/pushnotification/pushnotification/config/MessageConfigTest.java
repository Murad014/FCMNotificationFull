package com.pushnotification.pushnotification.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MessageConfigTest {
    @Test
    @DisplayName("Message Source Creation")
    public void test_message_source_creation() {
        MessageConfig config = new MessageConfig();
        MessageSource messageSource = config.messageSource();
        assertNotNull(messageSource);
        assertInstanceOf(ReloadableResourceBundleMessageSource.class, messageSource);
    }
}
