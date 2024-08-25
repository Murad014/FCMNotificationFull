package com.pushnotification.pushnotification.config;


import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FCMInitializerTest {



    @Test
    @DisplayName("Initialize Firebase App With Valid Configuration")
    public void test_initialize_firebase_app_with_valid_configuration() {
        ApplicationProperties mockProperties = mock(ApplicationProperties.class);
        when(mockProperties.getFirebaseConfigPath()).thenReturn("valid/path/to/firebase/config.json");
    
        FCMInitializer fcmInitializer = new FCMInitializer(mockProperties);
    
        fcmInitializer.initialize();
    
        assertFalse(FirebaseApp.getApps().isEmpty());
    }

    @Test
    @DisplayName("Initialize IO Exception Firebase App With Valid Configuration")
    public void test_handle_io_exception_when_reading_firebase_config() {
        ApplicationProperties mockProperties = mock(ApplicationProperties.class);
        when(mockProperties.getFirebaseConfigPath()).thenReturn("invalid/path/to/firebase/config.json");
    
        FCMInitializer fcmInitializer = new FCMInitializer(mockProperties);
    
        fcmInitializer.initialize();

    }

}