package com.boha.crawley.services;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class SecretManagerUtil {
    private final Logger logger = Logger.getLogger(SecretManagerUtil.class.getSimpleName());
    private static final String LOG_PREFIX = "\uD83E\uDD6C\uD83E\uDD6C SecretManagerUtil: ";

//    private final SecretManagerServiceClient secretManagerServiceClient;

    @Value("${projectId}")
    private String projectId;

    @Value("${serpApiKey}")
    private String serpApiKey;

    @Value("${chatGPTKey}")
    private String chatGPTKey;

    private final SecretManagerTemplate secretManagerTemplate;
    private final GcpProjectIdProvider projectIdProvider;

    public String getChatAPIKey() {
        //String projectId = projectIdProvider.getProjectId();
        //String secretName = "mongoString";

        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            String secretId = String.format("projects/%s/secrets/%s/versions/latest", projectId, chatGPTKey);
            AccessSecretVersionResponse response = client.accessSecretVersion(secretId);
            SecretPayload payload = response.getPayload();
            String key =  payload.getData().toStringUtf8();
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve secret from Secret Manager", e);
        }
    }
//    public String getChatGPTKey() {
//        logger.info(LOG_PREFIX + "getChatGPTKey: projectId: " + projectId +
//                ", chatGPTKey: " + chatGPTKey);
//
//        String secretId = String.format("projects/%s/secrets/%s/versions/latest", projectId, chatGPTKey);
//        logger.info(LOG_PREFIX + "secretId: " + secretId);
//
//        return getSecretValue();
//    }
//
//    public String getSerpApiKey() {
//        logger.info(LOG_PREFIX + "getSerpApiKey: projectId: " + projectId +
//                ", serpApiKey: " + serpApiKey);
//
//        String secretId = String.format("projects/%s/secrets/%s/versions/latest", projectId, serpApiKey);
//        logger.info(LOG_PREFIX + "secretId: " + secretId);
//
//        return getSecretValue();
//    }
//
//    private String getSecretValue() {
//        try {
//            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
//                    .setName(chatGPTKey)
//                    .build();
//
//            AccessSecretVersionResponse response = secretManagerServiceClient.accessSecretVersion(request);
//            SecretPayload payload = response.getPayload();
//            logger.info(LOG_PREFIX + "Secret value: " + payload);
//
//            return payload.getData().toStringUtf8();
//        } catch (Exception e) {
//            logger.severe(LOG_PREFIX + "Error retrieving secret value: " + e.getMessage());
//            e.printStackTrace();;
//            throw new RuntimeException("Error retrieving secret value", e);
//        }
//    }
}