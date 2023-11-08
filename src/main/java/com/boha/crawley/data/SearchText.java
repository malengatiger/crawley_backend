package com.boha.crawley.data;

import lombok.Data;

import java.util.List;

@Data
public class SearchText {

    String date;
    String searchId;
    String domainOwner;
    String state;
    String country;
    List<String> searchTexts;

    public SearchText(String date, String searchId, String domainOwner, String state, String country, List<String> searchTexts) {
        this.date = date;
        this.searchId = searchId;
        this.domainOwner = domainOwner;
        this.state = state;
        this.country = country;
        this.searchTexts = searchTexts;
    }
}
