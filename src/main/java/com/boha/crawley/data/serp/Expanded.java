package com.boha.crawley.data.serp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Expanded {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("snippet")
    @Expose
    private String snippet;
    @SerializedName("date_raw")
    @Expose
    private Object dateRaw;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("answers")
    @Expose
    private Object answers;
    @SerializedName("answers_raw")
    @Expose
    private Object answersRaw;

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

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Object getDateRaw() {
        return dateRaw;
    }

    public void setDateRaw(Object dateRaw) {
        this.dateRaw = dateRaw;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Object getAnswers() {
        return answers;
    }

    public void setAnswers(Object answers) {
        this.answers = answers;
    }

    public Object getAnswersRaw() {
        return answersRaw;
    }

    public void setAnswersRaw(Object answersRaw) {
        this.answersRaw = answersRaw;
    }

}
