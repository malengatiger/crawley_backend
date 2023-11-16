package com.boha.crawley.services;

import com.boha.crawley.CrawleyApplication;
import com.boha.crawley.data.Article;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.tasks.v2.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CloudTasksService {
    static final Logger logger = Logger.getLogger(CloudTasksService.class.getSimpleName());
    static final String mm = " \uD83C\uDF3F\uD83C\uDF3F\uD83C\uDF3F" +
            " CloudTasksService:  \uD83C\uDF3F\uD83C\uDF3F";
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private final CloudTasksClient cloudTasksClient;
//    private final String project;
//    private final String location;
//    private final String queue;

    @Value("${projectId}")
    private String projectId;
    @Value("${location}")
    private String location;
    @Value("${taskQue}")
    private String taskQue;


    public CloudTasksService(CloudTasksClient cloudTasksClient) {
        this.cloudTasksClient = cloudTasksClient;
    }

    public void processFile(File uploadedFile, String email) throws Exception {
        //get all articles and create payload
        List<Article> articles = getArticlesFromFile(uploadedFile);
        createScrapeTask(G.toJson(articles), email);
        logger.info(mm+" uploaded file processed and articles passed to Task");
    }
    private List<Article> getArticlesFromFile(File csv) throws Exception {
        List<Article> list;

        HashMap<String, Article> map;
        try (Reader in = new FileReader(csv.getAbsolutePath())) {
            CSVParser parser = CSVFormat.RFC4180.parse(in);
            map = new HashMap<>();
            for (CSVRecord csvRecord : parser) {
                String link = csvRecord.get(0);
                String title = csvRecord.get(1);
                Article article = new Article(link, title);
                if (isArticleUrlValid(article)) {
                    map.put(article.getLink(), article);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Unable to parse csv file: " + e.getMessage());
        }
        list = new ArrayList<>(map.values());
        logger.info(mm + "getArticlesFromFile: articles found: \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E "
                + list.size());
        return list;
    }
    private void createScrapeTask(String payload, String email) {
        logger.info(mm+" ... createScrapeTask ... email: " + email);


        Task.Builder taskBuilder = Task.newBuilder();
        taskBuilder.setAppEngineHttpRequest(AppEngineHttpRequest.newBuilder()
                .setRelativeUri("/crawley-service/scrape")
                .setHttpMethod(HttpMethod.POST)

                .setBody(ByteString.copyFrom(payload.getBytes()))
                .putHeaders("email", email)
                .build());

        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
        taskBuilder.setScheduleTime(timestamp);

        CreateTaskRequest createTaskRequest = CreateTaskRequest.newBuilder()
                .setParent(QueueName.of(projectId, location, taskQue).toString())
                .setTask(taskBuilder.build())
                .build();

        Task task = cloudTasksClient.createTask(createTaskRequest);
        logger.info(mm+" task created, queue: " + taskQue + " " + task.getName());

    }
    boolean isArticleUrlValid(Article article) {
        if (article.getLink() == null) {
            return false;
        }
        return article.getLink().startsWith("http://") || article.getLink().startsWith("https://");
    }
}