package com.boha.crawley.data;

import lombok.Data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

@Data
public class ChatResponse {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("system_fingerprint")
    @Expose
    private String systemFingerprint;
    @SerializedName("choices")
    @Expose
    private List<Choice> choices;
    @SerializedName("usage")
    @Expose
    private Usage usage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(String systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

}
