package com.boha.crawley.data.chatgpt;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@Generated("jsonschema2pojo")
public class Email {

    @SerializedName("email")
    @Expose
    private String email;
    private String company;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
