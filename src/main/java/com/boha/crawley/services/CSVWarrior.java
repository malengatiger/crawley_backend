package com.boha.crawley.services;

import com.boha.crawley.data.DomainData;
import com.opencsv.CSVWriter;
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
public class CSVWarrior {
    static final Logger logger = Logger.getLogger(CSVWarrior.class.getSimpleName());
    static final String mm = "CSVWarrior: \uD83E\uDD8A\uD83E\uDD8A\uD83E\uDD8A";

    public File writeDomainCSV(List<DomainData> data) throws Exception {
        logger.info(mm + " create Domain csv file: " + data.size() + " rows");
        List<String[]> lines = new ArrayList<>();
        String[] strings = new String[7];
        strings[0] = "Domain";
        strings[1] = "Company";
        strings[2] = "Article Title";
        strings[3] = "State";
        strings[4] = "Country";
        strings[5] = "Article Link";
        strings[6] = "Date";
        lines.add(strings);

        for (DomainData d : data) {
            String[] stuff = new String[7];
            stuff[0] = d.getDomain();
            stuff[1] = d.getDomainOwner();
            stuff[2] = d.getArticleTitle();
            stuff[3] = d.getState();
            stuff[4] = d.getCountry();
            stuff[5] = d.getUrl();
            stuff[6] = df.format(new Date());
            lines.add(stuff);
        }
        Path path = Paths.get("domain" +
                System.currentTimeMillis() + ".csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toString()))) {
            writer.writeAll(lines);
        }

        File f = path.toFile();
        logger.info(mm + " DomainData csv file, length: " + f.length());
        print(f);
        return f;

    }
    DateFormat df = new SimpleDateFormat("MMMM dd yyyy HH:mm");

    public File writeCompaniesCSV(List<String> data) throws Exception {
        logger.info(mm + " create companies csv file: " + data.size() + " rows");
        List<String[]> lines = new ArrayList<>();
        String[] strings = new String[2];
        strings[0] = "Date";
        strings[1] = "Company";

        lines.add(strings);
        for (String d : data) {
            String[] stuff = new String[2];
            stuff[0] = df.format(new Date());
            stuff[1] = d;
            lines.add(stuff);
        }
        Path path = Paths.get("companies" +
                System.currentTimeMillis() + ".csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toString()))) {
            writer.writeAll(lines);
        }

        File f = path.toFile();
        logger.info(mm + " Companies csv file, length: " + f.length());
        print(f);
        return f;

    }

    private void print(File file) throws IOException {
        logger.info(mm + "Contents of DomainData csv file");
        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            logger.info(line);
        }
        logger.info(mm + " ... end of Contents of DomainData csv file\n\n");
    }
}