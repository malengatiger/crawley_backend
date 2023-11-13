package com.boha.crawley.services;


import com.boha.crawley.data.Article;
import com.boha.crawley.data.ExtractionBag;
import com.boha.crawley.data.chatgpt.ProcessedChatGPTResponse;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArticleService {
//    public void init() {
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
//        pipeline = new StanfordCoreNLP(props);
//        logger.info(mm + " Stanford NLP pipeline set up!");
//        // Set up pipeline properties
//        synchronized (lock) {
//            if (pipeline == null) {
//                pipeline = new StanfordCoreNLP(props);
//            }
//        }
//    }

    @Autowired
    DomainOwnerInfoService domainOwnerInfoService;
    @Autowired
    FirebaseService firebaseService;

//    @Autowired
//    WhoIsService whoIsService;

    @Autowired
    CSVWarrior csvWarrior;

    @Autowired
    FreaksWhoIsService freaksWhoIsService;

    @Autowired
    EmailService emailService;

    @Autowired
    ChatGPTService chatGPTService;

    @Autowired
    BossService bossService;


    static final Logger logger = Logger.getLogger(ArticleService.class.getSimpleName());
    static final String mm = "ArticleService: \uD83E\uDD66\uD83E\uDD66\uD83E\uDD66";
    static final String mm2 = "ArticleService:  \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD356";
    @Value("${bucketName}")
    private String bucketName;
    @Value("${fileName}")
    private String fileName;


    /**
     * Downloads the articles csv file and creates the extracted data
     *
     * @return List<DomainData>
     */


    @Async
    public CompletableFuture<Void> parseArticlesAsync(File articleFile, String email) {
        return CompletableFuture.runAsync(() -> {
            try {
                parseArticles(articleFile, email);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("parseArticles failed: " + e.getMessage());
            }
        });
    }

    @Async
    public void parseArticles(File articleFile, String email) throws Exception {
        logger.info(mm + " parse Articles starting ..........");
        long startTime = System.currentTimeMillis();
        List<ExtractionBag> extractionBags;

        if (articleFile == null) {
            articleFile = firebaseService.downloadFile();
        }
        logger.info(mm + mm + " parseArticles: we have a file!! ");
        if (!articleFile.exists()) {
            throw new RuntimeException();
        }

        logger.info(mm + " articles file: " + articleFile.getAbsolutePath()
                + " length: " + articleFile.length() + " bytes");

        int totAddr = 0;
        int totEmail = 0;
        int totPhone = 0;
        int numberOfSpreadsheetLines = 0;
        String requestId = UUID.randomUUID().toString();
        String userUrl = stealthUrl + "getSpreadsheet?requestId=" + requestId;
        try {
            var articles = getArticlesFromFile(articleFile);
            extractionBags = getExtractedData(articles, email);

            for (ExtractionBag extractionBag : extractionBags) {
                List<String> pNames = chatGPTService.getCompanyNames(extractionBag.getText());
                ProcessedChatGPTResponse resp = bossService.digForData(pNames);
                String id = UUID.randomUUID().toString();
                try {
                    if (resp != null) {
                        resp.setRequesterEmail(email);
                        resp.setArticle(extractionBag.getArticle());
                        resp.setResponseId(id);
                        resp.setRequestId(requestId);
                        int ok = firebaseService.addProcessedChatResponse(resp);
                        if (ok == 0) {
                            totAddr += resp.getAddressList().size();
                            totEmail += resp.getEmailList().size();
                            totPhone += resp.getPhoneList().size();
                            numberOfSpreadsheetLines++;
                            logger.info("\n\n" + mm + " Article processed: " + extractionBag.getArticle().getTitle());
                            logger.info(mm + " work complete for article \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F " +
                                    "...... ProcessedChatGPTResponse created for: " + resp.getArticle().getTitle() + "  \n\n");
                            //todo -- create self api url that creates csv, send email ...
                        }
                    }
                } catch (ExecutionException | InterruptedException e) {
                    logger.severe(mm + " We fucked, Boss! \uD83D\uDD34\uD83D\uDD34\uD83D\uDD34\uD83D\uDD34"
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
            logger.info(mm+" Number of Spreadsheet lines: " + numberOfSpreadsheetLines);
            //send email
            var addr = NumberFormat.getNumberInstance().format(totAddr);
            var email1 = NumberFormat.getNumberInstance().format(totEmail);
            var phone = NumberFormat.getNumberInstance().format(totPhone);

            if (!extractionBags.isEmpty()) {
                sendEmail(addr, email1, phone, 200, email, userUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendEmail("","","", 400,email, userUrl);
            throw e;
        }


        logger.info("\n" + mm + " WORK COMPLETED! ... extracted data added: " + extractionBags.size()
                + " articles processed \uD83D\uDD35\uD83D\uDD35 ");

        printElapsed(startTime, extractionBags);

    }

    @Value("${stealthUrl}")
    private String stealthUrl;

    private void sendEmail(String totAddr, String totEmail, String totPhone, int status,
                           String recipient, String documentUrl) throws MessagingException {
        String htmlContent;
        String subject;
        if (status == 200) {
            subject = "Results of your request";
            // Create the HTML content
            htmlContent = "<html><body><h2> StealthCannabisApp </h2>" +
                    "<h4> " + subject + "</h4>" +
                    "<p> StealthCannabisApp has processed your request and " + totAddr + " address records, " +
                    totEmail + " email records and " + totPhone + " telephone number records were found. " +
                    "A file of the response has been created for you. Click to start the download.</p>"
                    + "<p><a href=\"" + documentUrl + "\"><b>Click to download Spreadsheet</b></a></p></body></html>";

        } else {
            subject = "Error in your request";
            htmlContent = "<html><body><h4> StealthCannabisApp Error Response</h4>" +
                    "<p> StealthCannabisApp tried to process your request and ran into an error. Please retry your request. Sorry!</p>";
        }

        emailService.sendEmail(recipient, subject, htmlContent);
        logger.info(mm + " Email sent containing link: " + documentUrl);
    }

    private static void printElapsed(long startTime, List<ExtractionBag> dataList) {
        //
        long endTime = System.currentTimeMillis();
        long elapsedTimeMillis = endTime - startTime;
        double elapsedTimeMinutes = elapsedTimeMillis / 1000.0 / 60;
        double elapsedTimeSeconds = elapsedTimeMillis / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String minutes = decimalFormat.format(elapsedTimeMinutes);
        String seconds = decimalFormat.format(elapsedTimeSeconds);

        logger.info("\n\n\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A Extraction complete: "
                + minutes + " elapsed minutes;  " + seconds + " totals seconds " +
                "\uD83E\uDD4F number of records created: "
                + dataList.size() + " \uD83D\uDD35\uD83D\uDD35");
    }

    @NotNull
    private List<ExtractionBag> getExtractedData(List<Article> articles, String email) {

        List<ExtractionBag> extractionBags = new ArrayList<>();

        for (Article article : articles) {
            if (filterLink(article.getLink())) {
                var bag = extractDataFromPage(article);
                if (bag != null) {
                    bag.setArticle(article);
                    extractionBags.add(bag);
                }
            }
        }
        //freaksWhoIsService.getDomainDetails(extractionBags);
        return extractionBags;
    }


    HashMap<String, String> exclusionsMap = new HashMap<>();
    List<String> exclusionList = new ArrayList<>();

    private List<Article> getArticlesFromFile(File csv) throws Exception {
        List<Article> list;

        HashMap<String, Article> map;
        try {
            Reader in = new FileReader(csv.getAbsolutePath());
            Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

            map = new HashMap<>();
            for (CSVRecord csvRecord : records) {
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
        list = map.values().stream().toList();
        logger.info(mm + mm + " articles found: \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E "
                + list.size());
        return list;
    }

    boolean isArticleUrlValid(Article article) {
        if (article.getLink() == null) {
            return false;
        }
        return article.getLink().startsWith("http://") || article.getLink().startsWith("https://");
    }

    public String getDomain(String url) {
        if (url.contains("javascript") || url.contains("sms") || url.contains("whatsapp")) {
            return null;
        }
        String dom = null;
        try {
            // Create a URL object from the given URL string
            URL urlObj = new URL(url);
            // Get the host (domain) from the URL object
            dom = urlObj.getHost().trim();
            // Remove the "www." prefix if present
            dom = dom.startsWith("www.") ? dom.substring(4) : dom;
            dom = dom.trim();
        } catch (MalformedURLException e) {
            logger.severe(e.getMessage());
        }
        return dom;
    }

    private ExtractionBag extractDataFromPage(Article article) {
        long startTime = System.currentTimeMillis();
        ExtractionBag extractionBag = null;
        try {
            Document document = Jsoup.connect(article.getLink()).get();
            var links = extractLinksFromDocument(document);
            String possibleCompanyNames = extractTextFromDocument(document);
            extractionBag = new ExtractionBag(article, links, possibleCompanyNames);
            printElapsed(startTime, article);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }

        return extractionBag;
    }

    private static void printElapsed(long startTime, Article article) {
        long endTime = System.currentTimeMillis();
        // Calculate the elapsed time in seconds
        long elapsedTimeMillis = endTime - startTime;
        double elapsedTimeSeconds = elapsedTimeMillis / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Format the double value to two decimal places
        String seconds = decimalFormat.format(elapsedTimeSeconds);

        logger.info(mm + " Data extracted for page: \uD83D\uDD35\uD83D\uDD35" +
                " elapsed time in seconds: "
                + seconds + " \uD83D\uDD35\uD83D\uDD35 article: " + article.getTitle());
    }

    private boolean filterLink(String link) {
        if (link.contains(".org")
                || link.contains(".gov")
                || link.contains(".edu")
                || link.contains("twitter")
                || link.contains("instagram")
                || link.contains("technocrunch")
                || link.contains("nasdaq")) {
            logger.info(mm + " LINK IGNORED: " + getDomain(link));
            return false;
        } else {
            return true;
        }
    }

    private List<String> extractLinksFromDocument(Document document) {
        List<String> links = new ArrayList<>();
        // Extract all anchor elements from the document
        Elements anchorElements = document.select("a[href]");
        // Iterate over the anchor elements and extract the href attribute
        for (Element anchorElement : anchorElements) {
            String link = anchorElement.attr("abs:href");
            if (isValidUrl(link) && (filterLink(link))) {
                links.add(link);
            }
        }
        HashMap<String,String> map = new HashMap<>();
        for (String link : links) {
            map.put(link,link);
        }

        return map.values().stream().toList();
    }

    public String extractTextFromDocument(Document document) {

        // Extract all paragraphs from the document
        Elements paragraphs = document.select("p");
        StringBuilder stringBuilder = new StringBuilder();
        // Iterate over the paragraphs and append the text to the StringBuilder
        for (Element paragraph : paragraphs) {
            stringBuilder.append(paragraph.text()).append("\n\n");
        }

        String extractedText = stringBuilder.toString();
        logger.info(mm + " Text Extracted from page: \uD83D\uDD34 "
                + extractedText.length() + " bytes \uD83C\uDF88");

        return extractedText;
    }

    private static boolean isValidUrl(String url) {
        boolean ok = url.startsWith("http://") || url.startsWith("https://");
        if (ok) {
            return !url.contains(".edu")
                    && !url.contains(".gov")
                    && !url.contains(".org")
                    && !url.contains("twitter")
                    && !url.contains("facebook")
                    && !url.contains("instagram")
                    && !url.contains("technocrunch")
                    && !url.contains("nasdaq");
        }
        return false;
    }

    private void setExclusionsMap() {
        exclusionsMap.put("Congress", "Congress");
        exclusionsMap.put("Senate", "Senate");
        exclusionsMap.put("University", "University");
        exclusionsMap.put("School", "School");
        exclusionsMap.put("Department", "Department");
        exclusionsMap.put("House", "House");
        exclusionsMap.put("Supreme Court", "Supreme Court");
        exclusionsMap.put("HUD", "HUD");
        exclusionsMap.put("Board of Supervisors", "Board of Supervisors");
        exclusionsMap.put("City Council", "City Council");
        exclusionsMap.put("County", "County");
        exclusionsMap.put("DOJ", "DOJ");
        exclusionsMap.put("Committee", "Committee");
        exclusionsMap.put("DEA", "DEA");
        exclusionsMap.put("USDA", "USDA");
        exclusionsMap.put("Food and Drug", "Food and Drug");
        exclusionsMap.put("NORML", "NORML");
        exclusionsMap.put("Drug Enforcement Agency", "Drug Enforcement Agency");
        exclusionsMap.put("SEC", "SEC");
        exclusionsMap.put("Administration", "Administration");
        exclusionsMap.put("Attorney", "Attorney");
        exclusionsMap.put("District", "District");
        exclusionsMap.put("FBI", "FBI");
        exclusionsMap.put("NSA", "NSA");
        exclusionsMap.put("GOP", "GOP");
        exclusionsMap.put("Democratic", "Democratic");
        exclusionsMap.put("Government", "Government");
        exclusionsMap.put("NYU", "NYU");
        exclusionsMap.put("Justice Department", "Justice Department");
        exclusionsMap.put("Drug Enforcement Administration", "Drug Enforcement Administration");
        exclusionsMap.put("National Institute", "National Institute");
        exclusionsMap.put("Privacy Policy", "Privacy Policy");
        exclusionsMap.put("Securities and Exchange Commission", "Securities and Exchange Commission");
        exclusionsMap.put("Office", "Office");
        exclusionsMap.put("Study", "Study");
        exclusionsMap.put("YMCA", "YMCA");
        exclusionsMap.put("NATO", "NATO");
        exclusionsMap.put("NYSE", "NYSE");
        exclusionsMap.put("EU", "EU");
        exclusionsMap.put("Company", "Company");
        exclusionsMap.put("Public", "Public");
        exclusionsMap.put("Homeland Security", "Homeland Security");
        exclusionsMap.put("Highway", "Highway");
        exclusionsMap.put("NCAA", "NCAA");
        exclusionsMap.put("DCC", "DCC");
        exclusionsMap.put("Agency", "Agency");
        exclusionsMap.put("Assembly", "Assembly");
        exclusionsMap.put("Organization", "Organization");
        exclusionsMap.put("Legislature", "Legislature");
        exclusionsMap.put("Republican", "Republican");
        exclusionsMap.put("Council", "Council");
        exclusionsMap.put("Home Office", "Home Office");
        exclusionsMap.put("PFAS", "PFAS");
        exclusionsMap.put("PFOA", "PFOA");
        exclusionsMap.put("CERCLA", "CERCLA");
        exclusionsMap.put("NHS", "NHS");
        exclusionsMap.put("Commission", "Commission");
        exclusionsMap.put("IRS", "IRS");
        exclusionsMap.put("NIAAA", "NIAAA");
        exclusionsMap.put("NIH", "NIH");
        exclusionsMap.put("UCLA", "UCLA");
        exclusionsMap.put("Authority", "Authority");
        exclusionsMap.put("Army", "Army");
        exclusionsMap.put("Air Force", "Air Force");
        exclusionsMap.put("SPAC", "SPAC");
        exclusionsMap.put("Fed", "Fed");
        exclusionsMap.put("OSHA", "OSHA");
        exclusionsMap.put("FDA", "FDA");

        exclusionsMap.put("Medicare", "Medicare");
        exclusionsMap.put("ZIDA", "ZIDA");
        exclusionsMap.put("MDMA", "MDMA");
        exclusionsMap.put("EQIP", "EQIP");
        exclusionsMap.put("RFI", "RFI");
        exclusionsMap.put("Institute", "Institute");
        exclusionsMap.put("Industry", "Industry");
        exclusionsMap.put("Tax Foundation", "Tax Foundation");
        exclusionsMap.put("Ministry", "Ministry");
        exclusionsMap.put("DOT", "DOT");
        exclusionsMap.put("Program", "Program");
        exclusionsMap.put("CEQA", "CEQA");
        exclusionsMap.put("Alcohol", "Alcohol");
        exclusionsMap.put(".org", ".org");
        exclusionsMap.put("NIDA", "NIDA");
        exclusionsMap.put("IPO", "IPO");
        exclusionsMap.put("U.S.", "U.S.");

        exclusionsMap.put("CRC", "CRC");
        exclusionsMap.put("USSC", "USSC");
        exclusionsMap.put("ATA", "ATA");

        exclusionsMap.put("Circuit", "Circuit");
        exclusionsMap.put("US Navy", "US Navy");
        exclusionsMap.put("GPD", "GPD");
        exclusionsMap.put("FinCEN", "FinCEN");
        exclusionsMap.put("Treasury", "Treasury");
        exclusionsMap.put("OCM", "OCM");
        exclusionsMap.put("Foundation", "Foundation");
        exclusionsMap.put("FTC", "FTC");
        exclusionsMap.put("Board", "Board");
        exclusionsMap.put("FCC", "FCC");
        exclusionsMap.put("LAPD", "LAPD");
        exclusionsMap.put("BCC", "BCC");
        exclusionsMap.put("NIST", "NIST");
        exclusionsMap.put("CDC", "CDC");
        exclusionsMap.put("HHC", "HHC");

        exclusionsMap.put("EMCDDA", "EMCDDA");
        exclusionsMap.put("ACLU", "ACLU");
        exclusionsMap.put("UC ", "UC ");
        exclusionsMap.put("MMJ", "MMJ");
        exclusionsMap.put("CCAA", "CCAA");
        exclusionsMap.put("College", "College");
        exclusionsMap.put("AMA", "AMA");
        exclusionsMap.put("Association", "Association");
        exclusionsMap.put("NDPIH", "NDPIH");
        exclusionsMap.put("APAC", "APAC");
        exclusionsMap.put("HHS", "HHS");

        exclusionsMap.put("NHN", "NHN");
        exclusionsMap.put("CHC", "CHC");
        exclusionsMap.put("MRTA", "MRTA");
        exclusionsMap.put("FDIC", "FDIC");

        exclusionsMap.put("Law Enforcement", "Law Enforcement");
        exclusionsMap.put("ONDCP", "ONDCP");
        exclusionsMap.put("UN ", "UN ");
        exclusionsMap.put("PAC", "PAC");
        exclusionsMap.put("RCMP", "RCMP");
        exclusionsMap.put("AP ", "AP ");
        exclusionsMap.put("LDB", "LDB");

        exclusionsMap.put("OLCC", "OLCC");
        exclusionsMap.put("CBG", "CBG");
        exclusionsMap.put("BDSA", "BDSA");

        exclusionsMap.put("WADA", "WADA");
        exclusionsMap.put("OHA", "OHA");
        exclusionsMap.put("VHA", "VHA");
        exclusionsMap.put("FSA", "FSA");

        exclusionsMap.put("MEC", "MEC");
        exclusionsMap.put("DNI", "DNI");
        exclusionsMap.put("CIA", "CIA");
        exclusionsMap.put("CBP", "CBP");
        exclusionsMap.put("NDIN", "NDIN");
        exclusionsMap.put("NBC", "NBC");
        exclusionsMap.put("WTOP", "WTOP");
        exclusionsMap.put("PLSI", "PLSI");
        exclusionsMap.put("NDAA", "NDAA");
        exclusionsMap.put("CBDD", "CBDD");
        exclusionsMap.put("SICPA", "SICPA");

        exclusionsMap.put("WACA", "WACA");
        exclusionsMap.put("THCA", "THCA");
        exclusionsMap.put("CBDA", "CBDA");
        exclusionsMap.put("USVI", "USVI");
        exclusionsMap.put("XYZ", "XYZ");

        exclusionsMap.put("Taliban", "Taliban");
        exclusionsMap.put("Al Qaeda", "Al Qaeda");
        exclusionsMap.put("ISIS", "ISIS");
        exclusionsMap.put("FTO", "FTO");
        exclusionsMap.put("TIHTA", "TIHTA");
        exclusionsMap.put("ISO", "ISO");

        exclusionsMap.put("Census Bureau", "Census Bureau");
        exclusionsMap.put("Teamsters", "Teamsters");
        exclusionsMap.put("NWG", "NWG");
        exclusionsMap.put("CIHC", "CIHC");

        exclusionsMap.put("Court", "Court");
        exclusionsMap.put("Community", "Community");
        exclusionsMap.put("IMF", "IMF");
        exclusionsMap.put("IFRS", "IFRS");
        exclusionsMap.put("NCCIH", "NCCIH");
        exclusionsMap.put("NHLBI", "NHLBI");
        exclusionsMap.put("NIMH", "NIMH");
        exclusionsMap.put("OCS", "OCS");
        exclusionsMap.put("World Bank", "World Bank");

        exclusionsMap.put("PFOS", "PFOS");
        exclusionsMap.put("OMB", "OMB");
        exclusionsMap.put("FDCA", "FDCA");

        exclusionsMap.put("TSX", "TSX");
        exclusionsMap.put("CCSAC", "CCSAC");

        exclusionsMap.put("Board of Pharmacy", "Board of Pharmacy");
        exclusionsMap.put("CA AB", "CA AB");
        exclusionsMap.put("CBD", "CBD");
        exclusionsMap.put("FDP", "FDP");
        exclusionsMap.put("CDU", "CDU");
        exclusionsMap.put("SPD", "SPD");
        exclusionsMap.put("USP", "USP");
        exclusionsMap.put("NLRB", "NLRB");
        exclusionsMap.put("UFCW", "UFCW");
        exclusionsMap.put("SAMHSA", "SAMHSA");
        exclusionsMap.put("WFLA", "WFLA");
        exclusionsMap.put("KGET", "KGET");
        exclusionsMap.put("EIHA", "EIHA");

        exclusionsMap.put("Task Force", "Task Force");
        exclusionsMap.put("Pentagon", "Pentagon");
        exclusionsMap.put("DOD", "DOD");

        exclusionsMap.put("CW", "CW");
        exclusionsMap.put("AJNA", "AJNA");
        exclusionsMap.put("AOAC", "AOAC");
        exclusionsMap.put("ASTM", "ASTM");
        exclusionsMap.put("GTI", "GTI");
        exclusionsMap.put("THCO", "THCO");
        exclusionsMap.put("KERN", "KERN");
        exclusionsMap.put("VA", "VA");
        exclusionsMap.put("NFT", "NFT");
        exclusionsMap.put("AP", "AP");
        exclusionsMap.put("CBC", "CBC");
        exclusionsMap.put("State Police", "State Police");

        exclusionsMap.put("Host", "Host");
        exclusionsMap.put("ICBA", "ICBA");
        exclusionsMap.put("WVU", "WVU");
        exclusionsMap.put("DPS", "DPS");

        exclusionsMap.put("CDPHE", "CDPHE");
        exclusionsMap.put("HACCP", "HACCP");
        exclusionsMap.put("ERP", "ERP");
        exclusionsMap.put("FOIA", "FOIA");

        exclusionsMap.put("Harvard", "Harvard");
        exclusionsMap.put("CIBC", "CIBC");
        exclusionsMap.put("NOSI", "NOSI");
        exclusionsMap.put("CBN", "DOT");

        exclusionsMap.put("NEI", "NEI");
        exclusionsMap.put("NCI", "NCI");
        exclusionsMap.put("PEAC", "PEAC");
        exclusionsMap.put("CDFA", "CDFA");

        exclusionsMap.put("EPA", "EPA");
        exclusionsMap.put("EVALI", "EVALI");
        exclusionsMap.put("APD", "APD");
        exclusionsMap.put("MCSB", "MCSB");

        exclusionsMap.put("PBR", "PBR");
        exclusionsMap.put("ASX", "ASX");
        exclusionsMap.put("EPN", "EPN");
        exclusionsMap.put("GMP", "GMP");

        exclusionsMap.put("MMA", "MMA");
        exclusionsMap.put("UFC", "UFC");
        exclusionsMap.put("USADA", "USADA");
        exclusionsMap.put("CCG", "CCG");
        exclusionsMap.put("NAICS", "NAICS");
        exclusionsMap.put("ECPC", "ECPC");
        exclusionsMap.put("THC", "THC");
        exclusionsMap.put("Parliament", "Parliament");
        exclusionsMap.put("AUMA", "AUMA");
        exclusionsMap.put("SXSW", "SXSW");
        exclusionsMap.put("ACOG", "ACOG");
        exclusionsMap.put("DSHS", "DSHS");
        exclusionsMap.put("GSC", "GSC");
        exclusionsMap.put("NPR", "NPR");
        exclusionsMap.put("EC", "EC");
        exclusionsMap.put("CEC", "CEC");

        exclusionsMap.put("NDI", "NDI");
        exclusionsMap.put("ECS", "ECS");
        exclusionsMap.put("IBS", "IBS");
        exclusionsMap.put("DOE", "DOE");

        exclusionsMap.put("USCC", "USCC");
        exclusionsMap.put("HIA", "HIA");
        exclusionsMap.put("CUNY", "CUNY");
        exclusionsMap.put("ACB", "ACB");

        exclusionsMap.put("EPL", "EPL");
        exclusionsMap.put("CMO", "CMO");
        exclusionsMap.put("MLCA", "MLCA");
        exclusionsMap.put("BSA", "BSA");

        exclusionsMap.put("ALTA", "ALTA");
        exclusionsMap.put("FCPA", "FCPA");
        exclusionsMap.put("TASE", "TASE");
        exclusionsMap.put("SMGH", "SMGH");
        exclusionsMap.put("PBS", "PBS");
        exclusionsMap.put("ABC", "ABC");
        exclusionsMap.put("USC", "USC");
        exclusionsMap.put("ICC", "ICC");
        exclusionsMap.put("GMR", "GMR");

        exclusionsMap.put("The", "The");
        exclusionsMap.put("We", "We");
        exclusionsMap.put("When", "When");
        exclusionsMap.put("Many", "Many");

        exclusionsMap.put("One", "One");
        exclusionsMap.put("Even", "Even");
        exclusionsMap.put("This", "This");
        exclusionsMap.put("From", "From");
        exclusionsMap.put("Egypt", "Egypt");
        exclusionsMap.put("There", "There");
        exclusionsMap.put("It", "It");
        exclusionsMap.put("Journal", "Journal");
        exclusionsMap.put("So", "So");
        exclusionsMap.put("By", "By");
        exclusionsMap.put("Consequently", "Consequently");
        exclusionsMap.put("You", "You");
        exclusionsMap.put("Its", "Its");
        exclusionsMap.put("But", "But");
        exclusionsMap.put("In", "In");
        exclusionsMap.put("All", "All");

        exclusionsMap.put("Include", "Include");
        exclusionsMap.put("Nobody", "Nobody");
        exclusionsMap.put("Everyone", "Everyone");
        exclusionsMap.put("They", "They");
        exclusionsMap.put("If", "If");
        exclusionsMap.put("Therefore", "Therefore");
        exclusionsMap.put("Simply", "Simply");
        exclusionsMap.put("Or", "Or");
        exclusionsMap.put("Just", "Just");
        exclusionsMap.put("Save", "Save");
        exclusionsMap.put("Privacy", "Privacy");
        exclusionsMap.put("Abuse", "Abuse");
        exclusionsMap.put("SUD", "SUD");
        exclusionsMap.put("To", "To");
        exclusionsMap.put("Deputy", "Deputy");
        exclusionsMap.put("Director", "Director");

        exclusionsMap.put("Very", "Very");
        exclusionsMap.put("Look", "Look");
        exclusionsMap.put("Required", "Required");
        exclusionsMap.put("Comment", "Comment");

        exclusionsMap.put("Name", "Name");
        exclusionsMap.put("Email", "Email");
        exclusionsMap.put("Our", "Our");
        exclusionsMap.put("Some", "Some");
        exclusionsMap.put("Another", "Another");
        exclusionsMap.put("Despite", "Despite");
        exclusionsMap.put("Furthermore", "Furthermore");
        exclusionsMap.put("However", "However");

        exclusionsMap.put("Language", "Language");
        exclusionsMap.put("Although", "Although");
        exclusionsMap.put("Okay", "Okay");
        exclusionsMap.put("Again", "Again");
        exclusionsMap.put("Yikes", "Yikes");
        exclusionsMap.put("Visit", "Visit");
        exclusionsMap.put("FREE", "FREE");
        exclusionsMap.put("Free", "Free");
        exclusionsMap.put("Unauthorized", "Unauthorized");
        exclusionsMap.put("Policy", "Policy");
        exclusionsMap.put("More", "More");
        exclusionsMap.put("Most", "Most");

        exclusionsMap.put("Red", "Red");
        exclusionsMap.put("Symptom", "Symptom");
        exclusionsMap.put("Animal", "Animal");
        exclusionsMap.put("Hot", "Hot");
        exclusionsMap.put("MILLION", "MILLION");
        exclusionsMap.put("Meet", "Meet");
        exclusionsMap.put("Now", "Now");
        exclusionsMap.put("His", "His");
        exclusionsMap.put("Who", "Who");
        exclusionsMap.put("Up", "Up");
        exclusionsMap.put("Yes", "Yes");
//        exclusionsMap.put("Save", "Save");
//        exclusionsMap.put("GMR", "GMR");
//        exclusionsMap.put("GMR", "GMR");
//        exclusionsMap.put("GMR", "GMR");
//        exclusionsMap.put("GMR", "GMR");
//        exclusionsMap.put("GMR", "GMR");
//        exclusionsMap.put("GMR", "GMR");
//        exclusionsMap.put("GMR", "GMR");
//        exclusionsMap.put("GMR", "GMR");


        exclusionList = exclusionsMap.values().stream().toList();
        logger.info(mm + " Exclusion list contains: " + exclusionList.size());
    }

    Gson G = new GsonBuilder().setPrettyPrinting().create();
}
