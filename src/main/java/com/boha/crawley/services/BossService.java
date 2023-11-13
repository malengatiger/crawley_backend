package com.boha.crawley.services;

import com.boha.crawley.data.chatgpt.ProcessedChatGPTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BossService {
    private final ChatGPTService chatGPTService;
    private final SERPService serpService;


    public ProcessedChatGPTResponse digForData(List<String> possibleCompanyNames) {
        ProcessedChatGPTResponse processedChatGPTResponse = null;
        List<String> names = new ArrayList<>();
        try {
            for (String possibleCompanyName : possibleCompanyNames) {
                String possibleAddressData = serpService.getPossibleAddresses(possibleCompanyName);
                if (possibleAddressData != null) {
                    processedChatGPTResponse = chatGPTService.findCompanyDetails(possibleAddressData);
                    if (processedChatGPTResponse != null) {
                        boolean ok1 = !processedChatGPTResponse.getAddressList().isEmpty();
                        boolean ok2 = !processedChatGPTResponse.getEmailList().isEmpty();
                        boolean ok3 = !processedChatGPTResponse.getPhoneList().isEmpty();
                        if (ok1 || ok2 || ok3) {
                            names.add(possibleCompanyName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (processedChatGPTResponse != null) {
            processedChatGPTResponse.setCompanies(names);
        }
        return processedChatGPTResponse;
    }
}
