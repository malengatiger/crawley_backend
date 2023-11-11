package com.boha.crawley.data.serp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class RelatedQuestion {

    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("source")
    @Expose
    private Source source;
    @SerializedName("search")
    @Expose
    private Search search;
    @SerializedName("block_position")
    @Expose
    private Integer blockPosition;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Integer getBlockPosition() {
        return blockPosition;
    }

    public void setBlockPosition(Integer blockPosition) {
        this.blockPosition = blockPosition;
    }

}
