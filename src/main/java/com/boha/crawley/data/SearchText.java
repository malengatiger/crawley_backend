package com.boha.crawley.data;

import lombok.Data;

import java.util.List;

@Data
public class SearchText {

    String date;
    String searchId;

    List<String> searchTexts;

    public SearchText(String date, String searchId, List<String> searchTexts) {
        this.date = date;
        this.searchId = searchId;
        this.searchTexts = searchTexts;
    }

    public SearchText() {
    }
}
