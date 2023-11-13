package com.boha.crawley.services;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.logging.Logger;

public class CompanyWebsiteSearch {
    static final Logger logger = Logger.getLogger(CompanyWebsiteSearch.class.getSimpleName());
    static final String mm = " \uD83C\uDF00 CompanyWebsiteSearch:  \uD83C\uDF00 \uD83C\uDF00 \uD83C\uDF00 \uD83C\uDF00";

//    public static void main(String[] args) {
//        findWebsites("IBM");
//    }
    public static void findWebsites(String companyName) {
        try {
            // Build the search query
            String query = companyName + " official website";

            // Connect to the search engine results page
            Document document = Jsoup.connect("https://www.google.com/search?q=" + query).get();

            // Extract website links from the search results

            Elements searchResults = document.select("a[href^=/url]");
            for (Element result : searchResults) {
                String websiteUrl = result.attr("href").replace("/url?q=", "");
                logger.info(mm + "Website: " + websiteUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}