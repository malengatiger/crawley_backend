package com.boha.crawley.data;

import lombok.Data;

import java.util.List;
@Data
public class ExtractionBag {
    private String text;
    private List<String> links;
    private List<String> names;

    public ExtractionBag(String text, List<String> links, List<String> names) {
        this.text = text;
        this.links = links;
        this.names = names;
    }
}
