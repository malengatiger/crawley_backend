package com.boha.crawley.data;

import lombok.Data;

import java.util.List;

@Data
public class PossibleCompanyNames {

    String date;
    String searchId;
    List<String> companyNames;

    public PossibleCompanyNames(String date, String searchId, List<String> companyNames) {
        this.date = date;
        this.searchId = searchId;
        this.companyNames = companyNames;
    }

    public PossibleCompanyNames() {
    }
}
