package com.zerobee.pillscheduler.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {
    
    String FIREBASE_SERVICE_ACCOUNT_JSON_PATH = "dashdrop-1d768-firebase-adminsdk-r34m3-4c4e721a0c.json";
    
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        ClassPathResource serviceAccount = new ClassPathResource(FIREBASE_SERVICE_ACCOUNT_JSON_PATH);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .build();
        return FirebaseApp.initializeApp(options);
    }
}
