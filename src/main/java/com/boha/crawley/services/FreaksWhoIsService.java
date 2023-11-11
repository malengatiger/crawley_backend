package com.boha.crawley.services;

import com.boha.crawley.data.DomainData;
import com.boha.crawley.data.freaks.FreaksWhoIsRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class FreaksWhoIsService {
    static final Logger logger = Logger.getLogger(FreaksWhoIsService.class.getSimpleName());
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    static final String mm = "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E" +
            " FreaksWhoIsService: \uD83C\uDF4E";
    @Value("${freaksWhoIsApiKey}")
    private String freaksWhoIsApiKey;
    @Value("${rateLimiter.maxRequestsPerInterval}")
    private int maxRequestsPerInterval;

    @Value("${rateLimiter.requestInterval}")
    private long requestInterval;
    OkHttpClient client;
//    private final RequestRateLimiter requestRateLimiter;

//    public FreaksWhoIsService(RequestRateLimiter requestRateLimiter) {
//        this.requestRateLimiter = requestRateLimiter;
//    }


    @PostConstruct
    private void init() {
        logger.info(mm + " initializing OkHttpClient .....");
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set the maximum time to establish a connection
                .readTimeout(600, TimeUnit.SECONDS) // Set the maximum time to read data from the server
                .writeTimeout(600, TimeUnit.SECONDS) // Set the maximum time to write data to the server
                .retryOnConnectionFailure(true)
                .build();
    }

    static final String url = "https://api.whoisfreaks.com/v1.0/whois?apiKey=";
    RequestRateLimiter requestRateLimiter;
    public void getDomainDetails(List<DomainData> domains) throws Exception {
        requestRateLimiter = new RequestRateLimiter(maxRequestsPerInterval, requestInterval);
        List<FreaksWhoIsRecord> list = new ArrayList<>();

        for (DomainData domain : domains) {
            requestRateLimiter.acquire();
            String sb = url +
                    freaksWhoIsApiKey +
                    "&whois=live" +
                    "&domainName=" +
                    domain.getDomain();

            FreaksWhoIsRecord freaksWhoIsRecord;
            // Create the request
            Request request = new Request.Builder()
                    .url(sb)
                    .build();

            try {
                // Execute the request
                try (Response response = client.newCall(request).execute()) {
                    logger.info(mm + " \uD83C\uDF0D FreaksWhoIsRecord response code: " + response.code());
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        freaksWhoIsRecord = G.fromJson(jsonResponse, FreaksWhoIsRecord.class);
//                        domain.setFreaksWhoIsRecord(freaksWhoIsRecord);
                        list.add(freaksWhoIsRecord);
                        logger.info(mm + " \uD83C\uDF0D FreaksWhoIsRecord response returned: " +
                                " \uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D" + G.toJson(freaksWhoIsRecord.getRegistrantContact()));
                    }
                }
                requestRateLimiter.release();
//                try {
//                    Thread.sleep(6000);
//                } catch (InterruptedException e) {
//                    logger.severe(mm + " Thread sleep interrupted: " + e.getMessage());
//                }
            } catch (IOException e) {
                logger.severe(" FreaksWhoIsRecord call fucked up! \uD83D\uDD34\uD83D\uDD34\uD83D\uDD34\uD83D\uDD34 "
                + e.getMessage());
            }
        }

        logger.info(mm + " responses from FreaksWhoIs: " + list.size());
    }

}
