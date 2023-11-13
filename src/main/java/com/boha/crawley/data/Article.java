package com.boha.crawley.data;

import lombok.Data;

@Data
public class Article {
    String link;
    String title;

    public Article(String link, String title) {
        this.link = link;
        this.title = title;
    }

    public Article() {
    }
}