package com.boha.crawley.data;

import lombok.Data;

import java.util.List;
@Data
public class DomainDataBag {
    List<DomainData> domainDataList;
    String date;
    String id;

    public DomainDataBag(List<DomainData> domainDataList, String date, String id) {
        this.domainDataList = domainDataList;
        this.date = date;
        this.id = id;
    }
}
