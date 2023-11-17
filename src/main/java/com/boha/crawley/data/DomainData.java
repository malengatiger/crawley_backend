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
    private List<String> links;
    private List<String> names;
    private String date;
    private String email;
    private int numberOfChatGPTRequests;
    private int numberOfGoogleNLPRequests;




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
