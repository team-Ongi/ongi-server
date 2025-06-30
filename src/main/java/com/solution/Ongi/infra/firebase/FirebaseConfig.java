package com.solution.Ongi.infra.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class FirebaseConfig {

    private final FirebaseProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    public FirebaseConfig(FirebaseProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() throws Exception {

        Map<String, Object> cred = new HashMap<>();
        cred.put("type",props.type());
        cred.put("project_id",props.projectId());
        cred.put("private_key_id",props.privateKeyId());
        cred.put("private_key",props.privateKey());
        cred.put("client_email",props.clientEmail());
        cred.put("client_id",props.clientId());
        cred.put("auth_uri",props.authUri());
        cred.put("token_uri",props.tokenUri());
        cred.put("auth_provider_x509_cert_url",props.authProviderX509CertUrl());
        cred.put("client_x509_cert_url",props.clientX509CertUrl());
        cred.put("universe_domain",props.universeDomain());

        byte[] jsonBytes = mapper.writeValueAsBytes(cred);
        try (InputStream is = new ByteArrayInputStream(jsonBytes)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(is);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}
