package com.boha.crawley.data.chatgpt;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ChatGPTResponse {

    @SerializedName("choices")
    @Expose
    private List<Choice> choices;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("usage")
    @Expose
    private Usage usage;

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

}
