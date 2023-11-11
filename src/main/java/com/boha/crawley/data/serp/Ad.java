package com.boha.crawley.data.serp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("jsonschema2pojo")
public class Ad {

    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("block_position")
    @Expose
    private String blockPosition;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("tracking_link")
    @Expose
    private String trackingLink;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("sitelinks")
    @Expose
    private List<Sitelink> sitelinks;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getBlockPosition() {
        return blockPosition;
    }

    public void setBlockPosition(String blockPosition) {
        this.blockPosition = blockPosition;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTrackingLink() {
        return trackingLink;
    }

    public void setTrackingLink(String trackingLink) {
        this.trackingLink = trackingLink;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Sitelink> getSitelinks() {
        return sitelinks;
    }

    public void setSitelinks(List<Sitelink> sitelinks) {
        this.sitelinks = sitelinks;
    }

}
