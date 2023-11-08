package com.boha.crawley.data.whois;

import lombok.Data;

@Data
public class WhoIsRecord{
    private String createdDate;
    private String updatedDate;
    private String expiresDate;
    private Registrant registrant;
//    private AdministrativeContact administrativeContact;
//    private TechnicalContact technicalContact;
    private String domainName;
//    private NameServers nameServers;
    private String status;
    private String rawText;
    private int parseCode;
    private String header;
    private String strippedText;
    private String footer;
//    private Audit audit;
    private String registrarName;
    private String registrarIANAID;
    private String createdDateNormalized;
    private String updatedDateNormalized;
    private String expiresDateNormalized;
//    private RegistryData registryData;
    private String contactEmail;
    private String domainNameExt;
    private int estimatedDomainAge;
}

