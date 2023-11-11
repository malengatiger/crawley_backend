package com.boha.crawley.data.freaks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class DomainRegistrar {

    @SerializedName("iana_id")
    @Expose
    private String ianaId;
    @SerializedName("registrar_name")
    @Expose
    private String registrarName;
    @SerializedName("whois_server")
    @Expose
    private String whoisServer;
    @SerializedName("website_url")
    @Expose
    private String websiteUrl;
    @SerializedName("email_address")
    @Expose
    private String emailAddress;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    public String getIanaId() {
        return ianaId;
    }

    public void setIanaId(String ianaId) {
        this.ianaId = ianaId;
    }

    public String getRegistrarName() {
        return registrarName;
    }

    public void setRegistrarName(String registrarName) {
        this.registrarName = registrarName;
    }

    public String getWhoisServer() {
        return whoisServer;
    }

    public void setWhoisServer(String whoisServer) {
        this.whoisServer = whoisServer;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
