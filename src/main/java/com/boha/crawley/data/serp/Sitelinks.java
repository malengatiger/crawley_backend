package com.boha.crawley.data.serp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("jsonschema2pojo")
public class Sitelinks {

    @SerializedName("expanded")
    @Expose
    private List<Expanded> expanded;

    public List<Expanded> getExpanded() {
        return expanded;
    }

    public void setExpanded(List<Expanded> expanded) {
        this.expanded = expanded;
    }

}
