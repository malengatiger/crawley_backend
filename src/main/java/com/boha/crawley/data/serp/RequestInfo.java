package com.boha.crawley.data.serp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class RequestInfo {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("credits_used")
    @Expose
    private Integer creditsUsed;
    @SerializedName("credits_used_this_request")
    @Expose
    private Integer creditsUsedThisRequest;
    @SerializedName("credits_remaining")
    @Expose
    private Integer creditsRemaining;
    @SerializedName("credits_reset_at")
    @Expose
    private String creditsResetAt;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCreditsUsed() {
        return creditsUsed;
    }

    public void setCreditsUsed(Integer creditsUsed) {
        this.creditsUsed = creditsUsed;
    }

    public Integer getCreditsUsedThisRequest() {
        return creditsUsedThisRequest;
    }

    public void setCreditsUsedThisRequest(Integer creditsUsedThisRequest) {
        this.creditsUsedThisRequest = creditsUsedThisRequest;
    }

    public Integer getCreditsRemaining() {
        return creditsRemaining;
    }

    public void setCreditsRemaining(Integer creditsRemaining) {
        this.creditsRemaining = creditsRemaining;
    }

    public String getCreditsResetAt() {
        return creditsResetAt;
    }

    public void setCreditsResetAt(String creditsResetAt) {
        this.creditsResetAt = creditsResetAt;
    }

}
