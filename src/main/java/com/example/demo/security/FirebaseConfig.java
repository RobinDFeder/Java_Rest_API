package com.example.demo.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public String initializeFirebase(){
        FileInputStream serviceAccount = null;
        try{
            serviceAccount = new FileInputStream("src/main/resources/Credentials/serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            return "Firebase initialized successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
