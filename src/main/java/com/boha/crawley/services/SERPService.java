package com.boha.crawley.services;

import com.boha.crawley.data.serp.OrganicResult;
import com.boha.crawley.data.serp.SERPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor

public class SERPService {
    static final Logger logger = Logger.getLogger(SERPService.class.getSimpleName());
    static final String mm = "\uD83C\uDF6F SERPService: \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F";
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
//        tryWeb("IBM official website, address");
//    }


    public List<String> getAddresses(String query) {
        List<String> foundAddresses = new ArrayList<>();
        try {
            // Set up the request parameters
            // Encode the query parameter
            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            // Build the request URL
            String requestUrl = "https://api.scaleserp.com/search?api_key="
                    + "2CFC9CDECBE3423D8D94E36450D3ED04"
//                    + "&output=JSON"
//                    + "&location=United+States"
                    + "&q=" + encodedQuery;

            // Create a URL object from the request URL
            URL url = new URL(requestUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();
            logger.info(mm + " response code: " + responseCode);
            // Read the response from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            SERPResponse sr = G.fromJson(response.toString(), SERPResponse.class);
            // Print the JSON response from Scale SERP
           logger.info(mm+G.toJson(sr));
            foundAddresses = askChatGPT(sr.getOrganicResults());
            for (OrganicResult result : sr.getOrganicResults()) {
                logger.info(mm+" snippet: " + result.getSnippet());
            }
            // Close the connection
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundAddresses;
    }
//    public static String getAddress(String randomString) {
//
//        // Regular expression pattern to match a street address
////        String regex = "\\d+\\s+\\w+\\s+\\w+";
//        String regex = "\\b\\d+\\s+.*?(?=,|$)";
//
//        // Create a Pattern object
//        Pattern pattern = Pattern.compile(regex);
//
//        // Create a Matcher object
//        Matcher matcher = pattern.matcher(randomString);
//
//        // Find the first occurrence of a street address
//        if (matcher.find()) {
//            return matcher.group();
//        } else {
//            logger.info(mm+" No street address found.");
//            return "";
//        }
//    }
    public  List<String> askChatGPT(List<OrganicResult> organicResults) {
        List<String> list = new ArrayList<>();
        for (OrganicResult result : organicResults) {
            String address = chatGPTService.findAddressOrPhone(result.getSnippet(), 0);
            logger.info(mm+" address? " + address);
            list.add(address);
        }
        return list;
    }
    @Autowired
    private ChatGPTService chatGPTService;
}
