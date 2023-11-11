package com.boha.crawley.data;

import lombok.Data;

import java.util.List;
@Data
public class ExtractionBag {
    private String text;
    private List<String> links;
    private List<String> names;
    private List<DomainData> domainDataList;
    private Article article;

    public ExtractionBag(String text, List<String> links, List<String> names, List<DomainData> domainDataList, Article article) {
        this.text = text;
        this.links = links;
        this.names = names;
        this.domainDataList = domainDataList;
        this.article = article;
    }
}
