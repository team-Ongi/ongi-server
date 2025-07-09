package com.solution.Ongi.infra.firebase;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

    private String serviceAccountFile;

    public String getServiceAccountFile() {
        return serviceAccountFile;
    }
    public void setServiceAccountFile(String serviceAccountFile) {
        this.serviceAccountFile = serviceAccountFile;
    }
}
