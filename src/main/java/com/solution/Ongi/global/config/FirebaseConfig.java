package com.solution.Ongi.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;

import com.solution.Ongi.infra.firebase.FirebaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
public class FirebaseConfig {

    private final FirebaseProperties props;

    public FirebaseConfig(FirebaseProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() throws Exception{
        if (!FirebaseApp.getApps().isEmpty()){
            var app = FirebaseApp.getInstance();
            log.info("[Firebase] already initialized. projectId={}", app.getOptions().getProjectId());
            return;
        }

        Path path = Paths.get(props.getServiceAccountFile());
        if (!Files.exists(path)) {
            throw new IllegalStateException("[Firebase] service account file not found: " + path);
        }

        try (InputStream in = Files.newInputStream(path)) {
            var creds = GoogleCredentials.fromStream(in);
            var options = FirebaseOptions.builder().setCredentials(creds).build();
            FirebaseApp.initializeApp(options);
            log.info("[Firebase] initialized. projectId={}", options.getProjectId());
        }
    }
}