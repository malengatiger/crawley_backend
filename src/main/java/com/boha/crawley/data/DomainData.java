package com.boha.crawley.data;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class DomainData implements  Comparable<DomainData>{
    private String domain;
    private String url;
    private String domainOwner;
    private String country;
    private String state;
    private String articleTitle;
    private String extractedText;
    private List<String> links;
    String date;

    public DomainData(String domain, String url, String domainOwner, String country, String state, String articleTitle, String extractedText, List<String> links, String date) {
        this.domain = domain;
        this.url = url;
        this.domainOwner = domainOwner;
        this.country = country;
        this.state = state;
        this.articleTitle = articleTitle;
        this.extractedText = extractedText;
        this.links = links;
        this.date = date;
    }

    @Override
    public int compareTo(@NotNull DomainData domainData) {
        if (this.domain.isEmpty()) {
            return 0;
        }
        if (domainData.getDomain() == null) {
            return 0;
        }
        return this.domain.compareTo(domainData.getDomain());
    }
}
