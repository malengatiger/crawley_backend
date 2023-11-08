package com.boha.crawley.data.whois;

import lombok.Data;

@Data
public class TechnicalContact{
    private String organization;
    private String state;
    private String country;
    private String countryCode;
    private String rawText;
}

