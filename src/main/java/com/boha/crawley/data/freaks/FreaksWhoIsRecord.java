package com.boha.crawley.data.freaks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("jsonschema2pojo")
public class FreaksWhoIsRecord {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("domain_name")
    @Expose
    private String domainName;
    @SerializedName("query_time")
    @Expose
    private String queryTime;
    @SerializedName("whois_server")
    @Expose
    private String whoisServer;
    @SerializedName("domain_registered")
    @Expose
    private String domainRegistered;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("domain_registrar")
    @Expose
    private DomainRegistrar domainRegistrar;
    @SerializedName("registrant_contact")
    @Expose
    private RegistrantContact registrantContact;
    @SerializedName("administrative_contact")
    @Expose
    private DomainRegistrar__1.AdministrativeContact administrativeContact;
    @SerializedName("technical_contact")
    @Expose
    private TechnicalContact technicalContact;
    @SerializedName("name_servers")
    @Expose
    private List<String> nameServers;
    @SerializedName("domain_status")
    @Expose
    private List<String> domainStatus;
    @SerializedName("whois_raw_domain")
    @Expose
    private String whoisRawDomain;
    @SerializedName("registry_data")
    @Expose
    private RegistryData registryData;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(String queryTime) {
        this.queryTime = queryTime;
    }

    public String getWhoisServer() {
        return whoisServer;
    }

    public void setWhoisServer(String whoisServer) {
        this.whoisServer = whoisServer;
    }

    public String getDomainRegistered() {
        return domainRegistered;
    }

    public void setDomainRegistered(String domainRegistered) {
        this.domainRegistered = domainRegistered;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public DomainRegistrar getDomainRegistrar() {
        return domainRegistrar;
    }

    public void setDomainRegistrar(DomainRegistrar domainRegistrar) {
        this.domainRegistrar = domainRegistrar;
    }

    public RegistrantContact getRegistrantContact() {
        return registrantContact;
    }

    public void setRegistrantContact(RegistrantContact registrantContact) {
        this.registrantContact = registrantContact;
    }

    public DomainRegistrar__1.AdministrativeContact getAdministrativeContact() {
        return administrativeContact;
    }

    public void setAdministrativeContact(DomainRegistrar__1.AdministrativeContact administrativeContact) {
        this.administrativeContact = administrativeContact;
    }

    public TechnicalContact getTechnicalContact() {
        return technicalContact;
    }

    public void setTechnicalContact(TechnicalContact technicalContact) {
        this.technicalContact = technicalContact;
    }

    public List<String> getNameServers() {
        return nameServers;
    }

    public void setNameServers(List<String> nameServers) {
        this.nameServers = nameServers;
    }

    public List<String> getDomainStatus() {
        return domainStatus;
    }

    public void setDomainStatus(List<String> domainStatus) {
        this.domainStatus = domainStatus;
    }

    public String getWhoisRawDomain() {
        return whoisRawDomain;
    }

    public void setWhoisRawDomain(String whoisRawDomain) {
        this.whoisRawDomain = whoisRawDomain;
    }

    public RegistryData getRegistryData() {
        return registryData;
    }

    public void setRegistryData(RegistryData registryData) {
        this.registryData = registryData;
    }

}