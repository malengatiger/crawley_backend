package com.boha.crawley.data;

import lombok.Data;

import java.util.List;
@Data
public class ExtractionBag {
    private Article article;
    private List<String> links;
    private String text;

    public ExtractionBag(Article article, List<String> links, String text) {
        this.article = article;
        this.links = links;
        this.text = text;
    }
}
