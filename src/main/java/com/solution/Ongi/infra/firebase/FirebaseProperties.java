package com.solution.Ongi.infra.firebase;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "firebase")
public record FirebaseProperties(
        String type,
        String projectId,
        String privateKeyId,
        String privateKey,
        String clientEmail,
        String clientId,
        String authUri,
        String tokenUri,
        String authProviderX509CertUrl,
        String clientX509CertUrl,
        String universeDomain
) { public String privateKey() {

        return privateKey.replace("\\n", "\n");
    }
}
