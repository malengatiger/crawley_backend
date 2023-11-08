package com.boha.crawley.data;


import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@Generated("jsonschema2pojo")
public class Message {

    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("content")
    @Expose
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}