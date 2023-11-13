package com.boha.crawley.services;

import com.boha.crawley.data.serp.OrganicResult;
import com.boha.crawley.data.serp.SERPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor

public class SERPService {
    static final Logger logger = Logger.getLogger(SERPService.class.getSimpleName());
    static final String mm = "\uD83E\uDD6C\uD83E\uDD6C SERPService: " +
            "\uD83E\uDD6C\uD83E\uDD6C\uD83E\uDD6C\uD83E\uDD6C";
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    static final String url = "https://api.scaleserp.com/search?";
    @Value("${serpApiKey}")
    private String serpApiKey;

    private final OkHttpClient client;
    static final MediaType mediaType = MediaType.parse("application/json");

    public SERPService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set the maximum time to establish a connection
                .readTimeout(600, TimeUnit.SECONDS) // Set the maximum time to read data from the server
                .writeTimeout(600, TimeUnit.SECONDS) // Set the maximum time to write data to the server
                .retryOnConnectionFailure(true)
                .build();
    }

//    public static void main(String[] args) {
//        SERPService service = new SERPService();
//        var list = service.getPossibleAddresses("IBM address");
//
//    }

    private static final String SERP_URL = "https://api.scaleserp.com/search?api_key=";
    public String getPossibleAddresses(String query) {
        logger.info(mm+" ............ Getting possible addresses from SERP ....");
        List<String> foundAddresses = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // Build the request URL
            String requestUrl = SERP_URL
                    + serpApiKey
                    + "&q=" + encodedQuery;

            URL mUrl = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            logger.info(mm + " getPossibleAddresses : response code: " + responseCode);
            // Read the response from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            try {
                SERPResponse sr = G.fromJson(response.toString(), SERPResponse.class);
                logger.info(mm + " SERP has responded! " + sr.getOrganicResults().size() +
                        " organic results obtained \uD83D\uDD35 ");
                for (OrganicResult result : sr.getOrganicResults()) {
                    foundAddresses.add(result.getSnippet());
                }
            } catch (JsonSyntaxException e) {
                logger.severe(mm+"JsonSyntaxException from SERP call: " +
                        "\uD83D\uDD34\uD83D\uDD34\uD83D\uDD34 " + e.getMessage());
                logger.severe(mm+" bad response? \uD83D\uDD34\uD83D\uDD34\uD83D\uDD34" +
                        " : \n" + response.substring(0,1024));
                return null;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (String address : foundAddresses) {
            sb.append(address).append(" ");
        }
        logger.info(mm + " SERP has responded! " + foundAddresses.size() +
                " addresses found \uD83D\uDD35 ");
        return sb.toString();
    }


}
