package com.boha.crawley.services;

import com.boha.crawley.data.ChatRequest;
import com.boha.crawley.data.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class ChatGPTService {
    static final Logger logger = Logger.getLogger(ArticleService.class.getSimpleName());
    static final String mm = "\uD83C\uDF6F ChatGPTService: \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F";

    private static final String API_URL =
            "https://api.openai.com/v1/chat/completions" ;
    static final String apiKey = "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";
    private final OkHttpClient client;
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public ChatGPTService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set the maximum time to establish a connection
                .readTimeout(300, TimeUnit.SECONDS) // Set the maximum time to read data from the server
                .writeTimeout(300, TimeUnit.SECONDS) // Set the maximum time to write data to the server
                .retryOnConnectionFailure(true)
                .build();
    }

    public void saySomething(String prompt) {
        logger.info(mm + " saySomething to ChatGPT ... \uD83D\uDD34 "
                + prompt + " \uD83D\uDD34");

        ChatRequest cr = new ChatRequest();
        var msg = new Message();
        msg.setContent(prompt);
        msg.setRole("user");
        List<Message> messageList = new ArrayList<>();
        messageList.add(msg);
        cr.setMessages(messageList);
        cr.setModel("gpt-3.5-turbo");

        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,
                    G.toJson(cr));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            String responseBody;

            try (Response response = client.newCall(request).execute()) {
                assert response.body() != null;
                responseBody = response.body().string();
            }
            logger.info(mm + mm + mm + " ChatGPT has responded!");
            logger.info(responseBody);
            logger.info(mm + " end of ChatGPT response!");
        } catch (IOException e) {
            logger.severe("\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F " + e.getMessage());
            e.printStackTrace();
        }

    }
}
