package com.boha.crawley.services;

import com.boha.crawley.data.Article;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Service

public class GoogleSheetsService {
    static final Logger logger = Logger.getLogger(GoogleSheetsService.class.getSimpleName());
    static final String mm = "GoogleSheetsService: \uD83D\uDD22\uD83D\uDD22\uD83D\uDD22\uD83D\uDD22";

    private static final String APPLICATION_NAME = "StealthCannabis";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    @Value("${credsPath}")
    private String credsPath;

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private  Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        Path filePath = Paths.get(credsPath);

        try {
            InputStream in = Files.newInputStream(filePath);

            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String extractSpreadsheetId(String url) {
        String spreadsheetId = "";

        // Find the start and end index of the SPREADSHEET_ID in the URL
        int startIndex = url.indexOf("/d/") + 3;
        int endIndex = url.indexOf("/edit");

        // Extract the SPREADSHEET_ID from the URL
        if (endIndex != -1) {
            spreadsheetId = url.substring(startIndex, endIndex);
        }

        return spreadsheetId;
    }

    public List<Article> downloadSpreadsheet(String url) throws Exception {
        // Build a new authorized API client service.
        List<Article> articles = new ArrayList<>();
        logger.info(mm + " ....... starting .....");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = extractSpreadsheetId(url);
        final String range = "Class Data!A2:E";
        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                        getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        logger.info(mm + " response from Google Sheets ..." + values.size());
        if (values.isEmpty()) {
            logger.info("\uD83D\uDD34\uD83D\uDD34 No data found.");
        } else {
            logger.info(mm + mm + " Data has been found, ");
            for (List<Object> row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                logger.info("data: " + row.get(0) + " - " + row.get(1) + "  \uD83D\uDC4D\uD83C\uDFFD");
            }
        }
        return articles;
    }
}