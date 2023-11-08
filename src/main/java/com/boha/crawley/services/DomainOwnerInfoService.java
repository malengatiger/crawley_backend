package com.boha.crawley.services;

import org.apache.commons.net.whois.WhoisClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Logger;

@Service
public class DomainOwnerInfoService {
    static final java.util.logging.Logger logger = Logger.getLogger(DomainOwnerInfoService.class.getSimpleName());
    static final String mm = "DomainOwnerInfo: \uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35";


    private String extractDomainName(String url) {
        // Remove the protocol (e.g., "https://") from the URL
        String domain = url.replaceFirst("^(https?://)?(www\\.)?", "");

        // Remove any path or query parameters
        int slashIndex = domain.indexOf('/');
        if (slashIndex != -1) {
            domain = domain.substring(0, slashIndex);
        }
//        // Disconnect from the WHOIS server
//        whoisClient.disconnect();
        return domain;
    }

    final WhoisClient whoisClient = new WhoisClient();

    public String getDomainOwnerCompany(String domainName) throws IOException {

        String url = null;
        try {
            // Connect to the WHOIS server
            whoisClient.connect(WhoisClient.DEFAULT_HOST);
            // Query the domain name
            String whoisData = whoisClient.query(domainName);
            var loc = whoisClient.getLocalAddress().getHostAddress();
            var rem = whoisClient.getRemoteAddress().getHostAddress();
            // Extract the organization name from the WHOIS data
            // Modify this logic based on the structure of the WHOIS data for the specific domain extension
            // This example assumes the organization name is preceded by "Registrant Organization:"
            logger.info(mm + " whoisData for domain: " + domainName + "  \uD83C\uDF81 " + whoisData);
            int otherIndex = whoisData.indexOf("Domain Name");
            if (otherIndex != -1) {
                int startIndex = otherIndex + "Domain Name:".length();
                int endIndex = whoisData.indexOf('\n', startIndex);
                if (endIndex != -1) {
                    url = whoisData.substring(startIndex, endIndex);
                    if (url.contains("mailto") || url.contains("javascript")) {
                        url = null;
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return url;
    }
}
