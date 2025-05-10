package com.solution.Ongi.infra;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        FileInputStream serviceAccount=new FileInputStream(
                new ClassPathResource("firebaseAccountKey.json").getFile());
        FirebaseOptions options=FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        if(FirebaseApp.getApps().isEmpty()){
            FirebaseApp.initializeApp(options);
        }

    }
}
