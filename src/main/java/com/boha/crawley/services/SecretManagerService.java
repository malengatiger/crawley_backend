package com.boha.crawley.services;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SecretManagerService {
    static final Logger logger = Logger.getLogger(SecretManagerService.class.getSimpleName());
    static final String mm = "\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E SecretManagerService: ";

    private final CredentialsProvider credentialsProvider;
    @Value("${projectId}")
    private String projectId;

    @Value("${serpApiKey}")
    private String serpApiKey;

    @Value("${chatGPTKey}")
    private String chatGPTKey;
//    @Value("${chatGPTKeyVersion}")
//    private String chatGPTKeyVersion;

    public String getChatGPTSecret() {
        try (SecretManagerServiceClient client = createSecretManagerServiceClient()) {
            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                    .setName("projects/" + projectId + "/secrets/" + chatGPTKey + "/versions/latest")
                    .build();
            logger.info(mm + " request name: " + request.getName());

            AccessSecretVersionResponse response = client.accessSecretVersion(request);
            String key = response.getPayload().getData().toStringUtf8();
            logger.info(mm + " key: " + key);
            return key;
        } catch (IOException e) {
            logger.severe(mm + "Error retrieving secret: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error retrieving secret", e);
        }
    }

    private SecretManagerServiceClient createSecretManagerServiceClient() throws IOException {
        credentialsProvider.getCredentials().getAuthenticationType();
//        for (List<String> cred : credentialsProvider.getCredentials()
//                .getRequestMetadata().values().stream().toList()) {
//            logger.info(mm+" credentialsProvider: cred: " + cred);
//        }
        SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .build();
        return SecretManagerServiceClient.create(settings);
    }
}