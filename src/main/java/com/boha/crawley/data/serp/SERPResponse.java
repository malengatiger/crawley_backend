package com.boha.crawley.data.serp;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data

@Generated("jsonschema2pojo")
public class SERPResponse {

    @SerializedName("request_info")
    @Expose
    private RequestInfo requestInfo;
    @SerializedName("search_parameters")
    @Expose
    private SearchParameters searchParameters;
    @SerializedName("search_metadata")
    @Expose
    private SearchMetadata searchMetadata;
    @SerializedName("search_information")
    @Expose
    private SearchInformation searchInformation;
    @SerializedName("inline_images")
    @Expose
    private List<InlineImage> inlineImages;
    @SerializedName("inline_image_suggestions")
    @Expose
    private List<InlineImageSuggestion> inlineImageSuggestions;
    @SerializedName("ads")
    @Expose
    private List<Ad> ads;
    @SerializedName("related_searches")
    @Expose
    private List<RelatedSearch> relatedSearches;
    @SerializedName("related_questions")
    @Expose
    private List<RelatedQuestion> relatedQuestions;
    @SerializedName("organic_results")
    @Expose
    private List<OrganicResult> organicResults;

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(SearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }

    public SearchMetadata getSearchMetadata() {
        return searchMetadata;
    }

    public void setSearchMetadata(SearchMetadata searchMetadata) {
        this.searchMetadata = searchMetadata;
    }

    public SearchInformation getSearchInformation() {
        return searchInformation;
    }

    public void setSearchInformation(SearchInformation searchInformation) {
        this.searchInformation = searchInformation;
    }

    public List<InlineImage> getInlineImages() {
        return inlineImages;
    }

    public void setInlineImages(List<InlineImage> inlineImages) {
        this.inlineImages = inlineImages;
    }

    public List<InlineImageSuggestion> getInlineImageSuggestions() {
        return inlineImageSuggestions;
    }

    public void setInlineImageSuggestions(List<InlineImageSuggestion> inlineImageSuggestions) {
        this.inlineImageSuggestions = inlineImageSuggestions;
    }

    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
    }

    public List<RelatedSearch> getRelatedSearches() {
        return relatedSearches;
    }

    public void setRelatedSearches(List<RelatedSearch> relatedSearches) {
        this.relatedSearches = relatedSearches;
    }

    public List<RelatedQuestion> getRelatedQuestions() {
        return relatedQuestions;
    }

    public void setRelatedQuestions(List<RelatedQuestion> relatedQuestions) {
        this.relatedQuestions = relatedQuestions;
    }

    public List<OrganicResult> getOrganicResults() {
        return organicResults;
    }

    public void setOrganicResults(List<OrganicResult> organicResults) {
        this.organicResults = organicResults;
    }

}
