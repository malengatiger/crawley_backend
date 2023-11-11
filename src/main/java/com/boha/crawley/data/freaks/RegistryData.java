package com.boha.crawley.data.freaks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("jsonschema2pojo")
public class RegistryData {

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
    @SerializedName("domain_registrar")
    @Expose
    private DomainRegistrar__1 domainRegistrar;
    @SerializedName("name_servers")
    @Expose
    private List<String> nameServers;
    @SerializedName("domain_status")
    @Expose
    private List<String> domainStatus;
    @SerializedName("whois_raw_registery")
    @Expose
    private String whoisRawRegistery;

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

    public DomainRegistrar__1 getDomainRegistrar() {
        return domainRegistrar;
    }

    public void setDomainRegistrar(DomainRegistrar__1 domainRegistrar) {
        this.domainRegistrar = domainRegistrar;
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

    public String getWhoisRawRegistery() {
        return whoisRawRegistery;
    }

    public void setWhoisRawRegistery(String whoisRawRegistery) {
        this.whoisRawRegistery = whoisRawRegistery;
    }

}
