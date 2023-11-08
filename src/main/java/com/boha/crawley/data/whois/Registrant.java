package com.boha.crawley.data.whois;

import lombok.Data;

@Data
public class Registrant{
    private String organization;
    private String state;
    private String country;
    private String countryCode;
    private String rawText;
}
