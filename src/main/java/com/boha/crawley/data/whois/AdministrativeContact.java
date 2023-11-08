package com.boha.crawley.data.whois;

import lombok.Data;

@Data
// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
public class AdministrativeContact{
    private String organization;
    private String state;
    private String country;
    private String countryCode;
    private String rawText;
}
