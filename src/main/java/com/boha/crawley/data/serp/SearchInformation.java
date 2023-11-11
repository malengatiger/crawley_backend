package com.boha.crawley.data.serp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class SearchInformation {

    @SerializedName("original_query_yields_zero_results")
    @Expose
    private Boolean originalQueryYieldsZeroResults;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    public Boolean getOriginalQueryYieldsZeroResults() {
        return originalQueryYieldsZeroResults;
    }

    public void setOriginalQueryYieldsZeroResults(Boolean originalQueryYieldsZeroResults) {
        this.originalQueryYieldsZeroResults = originalQueryYieldsZeroResults;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

}
