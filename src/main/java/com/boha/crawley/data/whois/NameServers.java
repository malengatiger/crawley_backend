package com.boha.crawley.data.whois;

import lombok.Data;

import java.util.List;
@Data
public class NameServers{
    private String rawText;
    private List<String> hostNames;
    private List<Object> ips;
}
