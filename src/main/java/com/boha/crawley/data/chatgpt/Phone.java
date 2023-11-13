package com.boha.crawley.data.chatgpt;


import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@Generated("jsonschema2pojo")
public class Phone {

    @SerializedName("phone")
    @Expose
    private String phone;
    private String company;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
