package com.boha.crawley.services;

import com.boha.crawley.data.whois.WhoIsRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class WhoIsService {
    static final Logger logger = Logger.getLogger(WhoIsService.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    static final String mm = "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E" +
            " WhoIsService: \uD83C\uDF4E";
    @Value("${whoIsApiKey}")
    private String whoIsApiKey;
    OkHttpClient client;

    @PostConstruct
    private void init() {
        logger.info(mm + " initializing OkHttpClient");
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set the maximum time to establish a connection
                .readTimeout(600, TimeUnit.SECONDS) // Set the maximum time to read data from the server
                .writeTimeout(600, TimeUnit.SECONDS) // Set the maximum time to write data to the server
                .retryOnConnectionFailure(true)
                .build();
    }
    static final String url = "https://www.whoisxmlapi.com/whoisserver/WhoisService?apiKey=";
    public WhoIsRecord getDomainDetails(String domain) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append(whoIsApiKey);
        sb.append("&domainName=");
        sb.append(domain);
        sb.append("&outputFormat=JSON");

        WhoIsRecord wir = null;
        // Create the request
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();

        try {
            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                logger.info(mm + " \uD83C\uDF0D WhoIs response code: " + response.code());
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String jsonResponse = response.body().string();
                    JSONObject obj = new JSONObject(jsonResponse);
                    JSONObject realObj = obj.getJSONObject("WhoisRecord");
                    wir = G.fromJson(realObj.toString(), WhoIsRecord.class);

                    logger.info(mm + " \uD83C\uDF0D WhoIs response: " +
                            G.toJson(wir) + " \uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wir;
    }

}
