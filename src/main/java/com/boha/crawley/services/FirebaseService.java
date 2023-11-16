package com.boha.crawley.services;

import com.boha.crawley.data.DomainData;
import com.boha.crawley.data.ExtractionBag;
import com.boha.crawley.data.chatgpt.ChatGPTResponse;
import com.boha.crawley.data.chatgpt.ProcessedChatGPTResponse;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.*;
import com.google.cloud.storage.Blob;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
//import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

//https://stealthapp-2yr65f7kaa-uw.a.run.app
@Service
@RequiredArgsConstructor
public class FirebaseService {
    static final Logger logger = Logger.getLogger(FirebaseService.class.getSimpleName());
    static final String mm = "\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35" +
            " FirebaseService: \uD83D\uDD35\uD83D\uDD35";
    private final Firestore firestore;

    @Value("${bucketName}")
    private String bucketName;
    @Value("${fileName}")
    private String fileName;

    @Value("${projectId}")
    private String projectId;
    private Storage storage;
    @PostConstruct
    public void initializeFirebase() throws IOException {
        logger.info(mm + " @PostConstruct: initializeFirebase ...");
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        logger.info(mm + " Firebase creds, QuotaProjectId: " + credentials.getQuotaProjectId());
        storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build().getService();
        logger.info(mm + " Firebase Cloud Storage configured: " + storage.toString());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        FirebaseApp.initializeApp(options);
        logger.info(mm + " @PostConstruct: FirebaseApp.initialized! \uD83D\uDD35\uD83D\uDD35");

    }
    static final String extractionData = "extractionData";
    static final String companyData = "companyData";

    public void addExtractionData(ExtractionBag extractionBag) throws ExecutionException, InterruptedException {
//        logger.info(mm + " adding stealth data to Firestore ...");
        CollectionReference collectionRef = firestore.collection(extractionData);
        var res = collectionRef.add(extractionBag);
        logger.info(mm + " extraction bag added: " + res.get().getPath());
    }
    public void addChatGPTResponse(ChatGPTResponse chatGPTResponse) throws ExecutionException, InterruptedException {
//        logger.info(mm + " adding stealth data to Firestore ...");
        CollectionReference collectionRef = firestore.collection("chatGPTResponses");
        var res = collectionRef.add(chatGPTResponse);
        logger.info(mm + " chatGPTResponse added: " + res.get().getPath());
    }

    public List<ProcessedChatGPTResponse> getData(String requestId) {
        logger.severe(mm+"... querying ProcessedChatGPTResponse " +
                "documents, requestId: " + requestId);

        CollectionReference collectionRef = firestore.collection(companyData);
        // Create a query to filter documents by 'requestId'
        Query query = collectionRef.whereEqualTo("requestId", requestId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<ProcessedChatGPTResponse> resultList = new ArrayList<>();

        try {
            QuerySnapshot snapshot = querySnapshot.get();
            // Process the documents that match the 'requestId'
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                ProcessedChatGPTResponse response = document.toObject(
                        ProcessedChatGPTResponse.class);
                resultList.add(response);
            }
        } catch (Exception e) {
            logger.severe(mm+"Error querying documents: " + e.getMessage());
        }
        logger.info(mm+" list of ProcessedChatGPTResponse " +
                "records found: " + resultList.size());
        return resultList;
    }
    public int addProcessedChatGPTResponse(ProcessedChatGPTResponse response) throws ExecutionException, InterruptedException {
//        logger.info(mm + " add processed ChatGPTResponse with addresses, emails and phone numbers to Firestore ...");
        CollectionReference collectionRef = firestore.collection(companyData);
        var res = collectionRef.add(response);
        logger.info(mm + " processed ChatGPTResponse data added to Firestore: " + res.get().getPath());
        return 0;
    }

    public File downloadFile() throws IOException {
        logger.info(mm + " Download Articles file from cloud storage: " + storage.toString());
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);

        // Download the file to the specified destination path
        File dir = new File("articles");
        if (!dir.exists()) {
            boolean ok = dir.mkdir();
            if (ok) {
                logger.info(mm+"Directory created: " + dir.getAbsolutePath());
            }
        }
        File destinationFile = new File(dir,"articles_" + System.currentTimeMillis() + ".csv");
        try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            blob.downloadTo(outputStream);
        }
        logger.info(mm + " Articles file downloaded:  " + destinationFile.length() + " bytes");
        return destinationFile;
    }


    static final String directory = "stealthCannabis";
    public String uploadFile(File file) throws IOException {

        String contentType = Files.probeContentType(file.toPath());

        BlobId blobId = BlobId.of(bucketName, directory
                + "/" + file.getName());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        logger.info(mm +
                " uploadFile to cloud storage, contentType: " + contentType);

        storage.createFrom(blobInfo, Paths.get(file.getPath()));
        String signedUrl = getSignedUrl(file.getName(),contentType);

        logger.info(mm + "File uploaded successfully. Blob name: " + file.getName());

       return signedUrl;
    }
    private String getSignedUrl(String objectName, String contentType  ) {
        logger.info(mm + " getSignedUrl for cloud storage ...");

        BlobId blobId = BlobId.of(bucketName, directory
                + "/" + objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();
        URL vv = storage
                .signUrl(blobInfo, (365*2), TimeUnit.DAYS, Storage.SignUrlOption.withPathStyle());
        logger.info(mm +
                "  signed url acquired. Cool!  \uD83C\uDF4E " + vv.toString());
        return vv.toString();
    }
}
