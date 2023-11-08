package com.boha.crawley.services;

import com.boha.crawley.data.DomainData;
import com.boha.crawley.data.DomainDataBag;
import com.boha.crawley.data.SearchText;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
    static final String domainData = "domainData";
    static final String searchData = "searchData";

    public void addDomainData(DomainDataBag domainDataBag) throws ExecutionException, InterruptedException {
        logger.info(mm + " adding stealth data to Firestore ...");
        CollectionReference collectionRef = firestore.collection(domainData);

        int cnt = 0;
        for (DomainData data : domainDataBag.getDomainDataList()) {
            var res = collectionRef.add(data);
            cnt++;
            logger.info(mm + " domain data added #" + cnt
                    + " record to Firestore: \uD83C\uDF0D " + data.getDomain() + " - " + res.get().getPath());

        }
    }
    public SearchText addSearchText(List<String> texts) throws ExecutionException, InterruptedException {
        logger.info(mm + " adding search data to Firestore ...");
        CollectionReference collectionRef = firestore.collection(searchData);

        SearchText st = new SearchText();
        st.setDate(new DateTime().toDateTimeISO().toString());
        st.setSearchId(UUID.randomUUID().toString());
        st.setSearchTexts(texts);

        var res = collectionRef.add(st);
        logger.info(mm + " search data added to Firestore: " + res.get().getPath());
        return st;
    }

    public List<DomainData> getDomainDataList() throws ExecutionException, InterruptedException {

        CollectionReference collectionRef = firestore.collection(domainData);
        Query query = collectionRef.orderBy("domain");

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot snapshot = querySnapshot.get();

        List<DomainData> domainDataList = snapshot.toObjects(DomainData.class);
        logger.info(mm + " domain data from Firestore: " + domainDataList.size());
        return domainDataList;
    }
    public File downloadFile() throws IOException {
        logger.info(mm + " Download Articles file from cloud storage: " + storage.toString());
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);

        // Download the file to the specified destination path
        File destinationFile = new File("articles_" + System.currentTimeMillis() + ".csv");
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
    public String getSignedUrl(String objectName, String contentType  ) {
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