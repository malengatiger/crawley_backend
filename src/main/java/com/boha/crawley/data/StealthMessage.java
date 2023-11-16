package com.boha.crawley.data;

import lombok.Data;

@Data
public class StealthMessage {
    String email;
    String date;
    String filePath;

    public StealthMessage(String email, String date, String filePath) {
        this.email = email;
        this.date = date;
        this.filePath = filePath;
    }

    public StealthMessage() {
    }
}
