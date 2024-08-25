package com.pushnotification.pushnotification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Configuration
public class FCMInitializer {

    private final ApplicationProperties applicationProperties;

    public FCMInitializer(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource(applicationProperties.getFirebaseConfigPath()).getInputStream())
                    )
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("FIRE_BASE_APPLICATION_HAS_BEEN_INITIALIZED");
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
