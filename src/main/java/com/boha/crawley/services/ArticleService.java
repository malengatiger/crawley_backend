package com.boha.crawley.services;


import com.boha.crawley.data.*;
import com.boha.crawley.data.whois.WhoIsRecord;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ArticleService {
    @Autowired
    DomainOwnerInfoService domainOwnerInfoService;
    @Autowired
    FirebaseService firebaseService;

    @Autowired
    WhoIsService whoIsService;



    static final Logger logger = Logger.getLogger(ArticleService.class.getSimpleName());
    static final String mm = "ArticleService: \uD83E\uDD66\uD83E\uDD66\uD83E\uDD66";
    static final String mm2 = "ArticleService:  \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD356";
    @Value("${bucketName}")
    private String bucketName;
    @Value("${fileName}")
    private String fileName;

    public ArticleService() {
        logger.info(mm + " ArticleService Construction ");
    }

    @Value("${articles}")
    private String articlesUrl;
    @Value("${articlesPath}")
    private String articlesPath;

    public File writeDomainDataCsvFile(List<DomainData> domainDataList) {

        String filePath = "domain_" + System.currentTimeMillis() + ".csv";
        File csvFile = new File(filePath);
        try (FileWriter writer = new FileWriter(csvFile)) {
            // Write the header column
            writer.append("Domain,Company,State,Country,Article Title,URL,Date");
            writer.append("\n");

            // Write the data rows
            for (DomainData domainData : domainDataList) {
                writer.append(domainData.getDomain());
                writer.append(",");
                writer.append(domainData.getDomainOwner());
                writer.append(",");
                writer.append(domainData.getState());
                writer.append(",");
                writer.append(domainData.getCountry());
                writer.append(",");
                writer.append(domainData.getArticleTitle());
                writer.append(",");
                writer.append(domainData.getUrl());
                writer.append(",");
                writer.append(domainData.getDate());
                writer.append("\n");
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvFile;
    }
    public File writeSearchTextCsvFile(SearchText searchText) {

        String filePath = "domain_" + System.currentTimeMillis() + ".csv";
        File csvFile = new File(filePath);
        try (FileWriter writer = new FileWriter(csvFile)) {
            // Write the header column
            writer.append("Domain,Article Title,URL");
            writer.append("\n");

            // Write the data rows
            for (String text : searchText.getSearchTexts()) {
                writer.append(DateTime.now().toDateTimeISO().toString());
                writer.append(",");
                writer.append(text);
                writer.append("\n");
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvFile;
    }

    /**
     * Downloads the articles csv file and creates the extracted data
     *
     * @return List<DomainData>
     */

    public File parseUploadedFile(File articleFile) throws Exception {
        return parseArticles(articleFile);
    }
    public File parseArticles(File articleFile ) throws Exception {
        logger.info(mm + " parse Articles starting ..........");
        long startTime = System.currentTimeMillis();
        List<DomainData> dataList = new ArrayList<>();
        List<String> names;
        HashMap<String, String> nameMap = new HashMap<>();

        //
        setExclusionsMap();
        if (articleFile == null) {
            articleFile= firebaseService.downloadFile();
        }
        logger.info(mm + mm + " parseArticles: we have a file!! ");
        if (!articleFile.exists()) {
            throw new RuntimeException();
        }

        logger.info(mm + " articles file: " + articleFile.getAbsolutePath()
                + " length: " + articleFile.length() + " bytes");

        try {
            var articles = getArticlesFromFile(articleFile);
            for (Article article : articles) {
                if (filterLink(article.link())) {
                    var bag = extractDataFromPage(article.link());
                    if (bag != null) {
                        String domain = getDomain(article.link());
                        WhoIsRecord wir = whoIsService.getDomainDetails(domain);
                        var dd = new DomainData(domain, article.link(),
                                article.title(), bag.getText(), wir.getRegistrant().getOrganization(),
                                wir.getRegistrant().getCountry(), wir.getRegistrant().getState(),
                                bag.getLinks(), DateTime.now().toDateTimeISO().toString());
                        dataList.add(dd);
                        for (String name : bag.getNames()) {
                            nameMap.put(name, name);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        names = nameMap.values().stream().toList();
        List<String> fNames = new ArrayList<>();
        for (String name : names) {
            fNames.add(name);
        }
        //
        Collections.sort(fNames);
        var st = firebaseService.addSearchText(fNames);
        logger.info(mm + " search text data added: " + st.getSearchTexts().size()
        + " \uD83D\uDD35\uD83D\uDD35date: " + st.getDate());
        //
        Collections.sort(dataList);
        var bag = new DomainDataBag(dataList,
                new DateTime().toDateTimeISO().toString(),
                UUID.randomUUID().toString());
        //
        firebaseService.addDomainData(bag);
        logger.info(mm + " domain data added: " + dataList.size()
                + " \uD83D\uDD35\uD83D\uDD35date: " );

        var file1 = writeDomainDataCsvFile(dataList);
        String url1 = firebaseService.uploadFile(file1);

        var file2 = writeSearchTextCsvFile(st);
        String url2 = firebaseService.uploadFile(file2);

        // Calculate the elapsed time in seconds
        long endTime = System.currentTimeMillis();
        long elapsedTimeMillis = endTime - startTime;
        double elapsedTimeMinutes = elapsedTimeMillis / 1000.0 / 60;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String minutes = decimalFormat.format(elapsedTimeMinutes);
        logger.info("\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A Extraction complete: "
                + minutes + " elapsed minutes; \uD83E\uDD4F domainAndURLS: " + dataList.size());
        return file1;
    }

    StanfordCoreNLP pipeline;

    public List<String> extractCompanyNames(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        if (pipeline == null) {
            pipeline = new StanfordCoreNLP(props);
        }

        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<String> companyNames = new ArrayList<>();

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            getNames(companyNames, sentence);
        }
        logger.info(mm + " names found: " + companyNames.size());
        List<String> filtered = new ArrayList<>();
        for (String n : companyNames) {
            if (!n.isEmpty() && (shouldKeepName(n))) {
                filtered.add(n.trim());
                logger.info(mm + " name extracted: \uD83D\uDD34\uD83D\uDD34 " + n);

            }
        }
        return filtered;
    }

    HashMap<String, String> exclusionsMap = new HashMap<>();
    List<String> exclusionList = new ArrayList<>();

    private boolean shouldKeepName(String name) {
        boolean ok = true;
        for (String s : exclusionList) {
            if (name.contains(s)) {
                ok = false;
                break;
            }
        }
        return ok;
    }

    private void getNames(List<String> companyNames, CoreMap sentence) {
        StringBuilder currentEntity = null;
        for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String word = token.word();
            String nerTag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

            if (nerTag.equals("ORGANIZATION")) {
                if (currentEntity == null) {
                    currentEntity = new StringBuilder(word);
                } else {
                    currentEntity.append(" ").append(word);
                }
            } else {
                if (currentEntity != null) {
                    companyNames.add(currentEntity.toString());
                    currentEntity = null;
                }
            }
        }

        if (currentEntity != null && (shouldKeepName(currentEntity.toString()))) {
                companyNames.add(currentEntity.toString());

        }
    }

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
                var article = new Article(link, title);
                if (isArticleUrlValid(article)) {
                    map.put(article.link(), article);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        list = map.values().stream().toList();
        logger.info(mm + mm + " articles found: \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E "
                + list.size());
        return list;
    }

    boolean isArticleUrlValid(Article article) {

        return article.link().startsWith("http://") || article.link().startsWith("https://");
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

    private ExtractionBag extractDataFromPage(String url) {
        long startTime = System.currentTimeMillis();
        ExtractionBag eb = null;
        try {
            Document document = Jsoup.connect(url).get();
            var links = extractLinksFromDocument(document);
            var text = extractTextFromDocument(document);
            var names = extractCompanyNames(text);

            eb = new ExtractionBag(text, links, names);
            printElapsed(startTime, url);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }

        return eb;
    }

    private static void printElapsed(long startTime, String url) {
        long endTime = System.currentTimeMillis();
        // Calculate the elapsed time in seconds
        long elapsedTimeMillis = endTime - startTime;
        double elapsedTimeSeconds = elapsedTimeMillis / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Format the double value to two decimal places
        String seconds = decimalFormat.format(elapsedTimeSeconds);

        logger.info(mm + " Data extracted for page: \uD83D\uDD35\uD83D\uDD35" +
                " elapsed time in seconds: "
                + seconds + " \uD83D\uDD35\uD83D\uDD35 url: " + url);
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

        return links;
    }

    public String extractTextFromDocument(Document document) {
        String extractedText = "";
        // Extract all paragraphs from the document
        Elements paragraphs = document.select("p");
        StringBuilder stringBuilder = new StringBuilder();
        // Iterate over the paragraphs and append the text to the StringBuilder
        for (Element paragraph : paragraphs) {
            stringBuilder.append(paragraph.text()).append("\n\n");
        }

        extractedText = stringBuilder.toString();

        logger.info(mm + " Text Extracted from page: \uD83D\uDD34 "
                + extractedText.length() + " bytes");
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

    private File downloadFile() throws IOException {
        // Initialize the Firebase Admin SDK with the service account credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream("stealthcannabis-firebase.json"));
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build().getService();
        logger.info(mm + " storage: " + storage.toString());
        // Create a BlobId for the file in the default bucket
        BlobId blobId = BlobId.of(bucketName, fileName);

        // Get the Blob from the default bucket
        Blob blob = storage.get(blobId);

        // Download the file to the specified destination path
        File destinationFile = new File("file_" + System.currentTimeMillis() + ".csv");
        try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            blob.downloadTo(outputStream);
        }
        return destinationFile;
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
        exclusionList = exclusionsMap.values().stream().toList();
        logger.info(mm + " Exclusion list contains: " + exclusionList.size());
    }
    Gson G = new GsonBuilder().setPrettyPrinting().create();
}
