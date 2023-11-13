package com.boha.crawley.data.chatgpt;

import com.boha.crawley.data.Article;
import lombok.Data;

import java.util.List;
@Data
public class ProcessedChatGPTResponse {
    String responseId;
    String date;
    List<Address> addressList;
    List<Email> emailList;
    List<Phone> phoneList;
    String requesterEmail;
    Article article;
    List<String> companies;
    String requestId;

    public ProcessedChatGPTResponse(String responseId, String date, List<Address> addressList, List<Email> emailList, List<Phone> phoneList) {
        this.responseId = responseId;
        this.date = date;
        this.addressList = addressList;
        this.emailList = emailList;
        this.phoneList = phoneList;
    }

    public ProcessedChatGPTResponse() {
    }
}
