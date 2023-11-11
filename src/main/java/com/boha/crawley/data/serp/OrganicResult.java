package com.boha.crawley.data.serp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class OrganicResult {

    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("displayed_link")
    @Expose
    private String displayedLink;
    @SerializedName("prerender")
    @Expose
    private Boolean prerender;
    @SerializedName("cached_page_link")
    @Expose
    private String cachedPageLink;
    @SerializedName("sitelinks")
    @Expose
    private Sitelinks sitelinks;
    @SerializedName("sitelinks_search_box")
    @Expose
    private Boolean sitelinksSearchBox;
    @SerializedName("block_position")
    @Expose
    private Integer blockPosition;
    @SerializedName("snippet")
    @Expose
    private String snippet;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDisplayedLink() {
        return displayedLink;
    }

    public void setDisplayedLink(String displayedLink) {
        this.displayedLink = displayedLink;
    }

    public Boolean getPrerender() {
        return prerender;
    }

    public void setPrerender(Boolean prerender) {
        this.prerender = prerender;
    }

    public String getCachedPageLink() {
        return cachedPageLink;
    }

    public void setCachedPageLink(String cachedPageLink) {
        this.cachedPageLink = cachedPageLink;
    }

    public Sitelinks getSitelinks() {
        return sitelinks;
    }

    public void setSitelinks(Sitelinks sitelinks) {
        this.sitelinks = sitelinks;
    }

    public Boolean getSitelinksSearchBox() {
        return sitelinksSearchBox;
    }

    public void setSitelinksSearchBox(Boolean sitelinksSearchBox) {
        this.sitelinksSearchBox = sitelinksSearchBox;
    }

    public Integer getBlockPosition() {
        return blockPosition;
    }

    public void setBlockPosition(Integer blockPosition) {
        this.blockPosition = blockPosition;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

}
