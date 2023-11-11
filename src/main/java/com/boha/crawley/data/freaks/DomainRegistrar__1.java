package com.boha.crawley.data.freaks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class DomainRegistrar__1 {

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

    @Generated("jsonschema2pojo")
    public static class AdministrativeContact {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("company")
        @Expose
        private String company;
        @SerializedName("street")
        @Expose
        private String street;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("zip_code")
        @Expose
        private String zipCode;
        @SerializedName("country_name")
        @Expose
        private String countryName;
        @SerializedName("country_code")
        @Expose
        private String countryCode;
        @SerializedName("email_address")
        @Expose
        private String emailAddress;
        @SerializedName("phone")
        @Expose
        private String phone;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

    }
}
