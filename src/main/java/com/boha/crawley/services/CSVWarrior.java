package com.boha.crawley.services;

import com.boha.crawley.data.ExtractionBag;
import com.boha.crawley.data.chatgpt.Address;
import com.boha.crawley.data.chatgpt.Email;
import com.boha.crawley.data.chatgpt.Phone;
import com.boha.crawley.data.chatgpt.ProcessedChatGPTResponse;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CSVWarrior {
    static final Logger logger = Logger.getLogger(CSVWarrior.class.getSimpleName());
    static final String mm = "CSVWarrior: \uD83E\uDD8A\uD83E\uDD8A\uD83E\uDD8A";

    private final FirebaseService firebaseService;
    DateFormat df = new SimpleDateFormat("MMMM dd yyyy HH:mm");

    public File createSpreadsheet(String requestId) throws Exception {
        logger.info(mm + "createSpreadsheet: ... create csv file, requestId: " + requestId );
        List<String[]> lines = new ArrayList<>();
        List<ProcessedChatGPTResponse> data = firebaseService.getData(requestId);
        logger.info(mm + "createSpreadsheet: ... records found: " + data.size() );
        String[] strings = new String[7];
        strings[0] = "Companies";
        strings[1] = "Phone";
        strings[2] = "Email Address";
        strings[3] = "Address";
        strings[4] = "Article Title";
        strings[5] = "Article Link";
        strings[6] = "Date Created";
        lines.add(strings);

        String date = df.format(new Date());
        for (ProcessedChatGPTResponse x : data) {
            String[] stuff = new String[7];
            StringBuilder cb = new StringBuilder();
            for (String company : x.getCompanies()) {
                cb.append(company).append("\n");
            }
            stuff[0] = cb.toString();

            StringBuilder pb = new StringBuilder();
            for (Phone p : x.getPhoneList()) {
                pb.append(p.getPhone()).append(" - ");
                pb.append(p.getCompany()).append("\n");
            }
            stuff[1] = pb.toString();

            StringBuilder mb = new StringBuilder();
            for (Email m : x.getEmailList()) {
                mb.append(m.getEmail()).append(" - ");
                mb.append(m.getCompany()).append("\n");
            }
            stuff[2] = mb.toString();
            stuff[3] = flattenAddresses(x.getAddressList());
            stuff[4] = x.getArticle().getTitle();
            stuff[5] = x.getArticle().getLink();
            stuff[6] = date;
            lines.add(stuff);
        }

        // Check if the directory already exists
        File directory = new File("spreadsheets");
        if (directory.exists()) {
            logger.info(mm+"Directory already exists: " + directory.getAbsolutePath());
        } else {
            // Create the directory
            boolean isDirectoryCreated = directory.mkdir();

            if (isDirectoryCreated) {
                logger.info(mm+"Directory created: " + directory.getAbsolutePath());
            } else {
                logger.info(mm+"Failed to create the directory.");
                throw new Exception("Could not create directory"); // Exit the program if directory creation fails
            }
        }

        Path path = Paths.get(directory.getPath(),"StealthCannabis_" +
                System.currentTimeMillis() + ".csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toString()))) {
            writer.writeAll(lines);
        }

        File f = path.toFile();
        logger.info(mm + " Spreadsheet csv file created, length: " + f.length());
        print(f);
        return f;

    }

    private String flattenAddresses(List<Address> list) {
        StringBuilder xb = new StringBuilder();
        for (Address address : list) {
            StringBuilder sb = new StringBuilder();
            sb.append(address.getCompany()).append("\n");
            if (address.getStreet() != null) {
                sb.append(address.getStreet()).append(", ");
            }
            if (address.getCity() != null) {
                sb.append(address.getCity()).append(", ");
            }
            if (address.getState() != null) {
                sb.append(address.getState()).append(", ");
            }
            if (address.getCountry() != null) {
                sb.append(address.getCountry()).append(", ");
            }
            if (address.getZip() != null) {
                sb.append(address.getZip());
            }
            xb.append(sb).append("\n\n");
        }

        return xb.toString();
    }

    private void print(File file) throws IOException {
        logger.info(mm + "Contents of Spreadsheet csv file");
        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            logger.info(line);
        }
        logger.info(mm + " ... end of Contents of spreadsheet csv file\n\n");
    }
}