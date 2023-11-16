package com.boha.crawley.services;

import com.boha.crawley.data.chatgpt.Address;
import com.boha.crawley.data.chatgpt.Email;
import com.boha.crawley.data.chatgpt.Phone;
import com.boha.crawley.data.chatgpt.ProcessedChatGPTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class BossService {
    private final ChatGPTService chatGPTService;
    private final SERPService serpService;

    static final Logger logger = Logger.getLogger(BossService.class.getSimpleName());
    static final String mm = "\uD83E\uDD6C\uD83E\uDD6C BossService: " +
            "\uD83E\uDD6C\uD83E\uDD6C\uD83E\uDD6C\uD83E\uDD6C";

    public ProcessedChatGPTResponse digForData(List<String> possibleCompanyNames) {
        logger.info(mm + " digForData: ......... companies: " + possibleCompanyNames.size());
        ProcessedChatGPTResponse processedChatGPTResponse = null;
        List<String> names = new ArrayList<>();
        try {
            for (String possibleCompanyName : possibleCompanyNames) {
                String textFromSERP = serpService.getPossibleAddresses(possibleCompanyName);
                if (textFromSERP != null && !textFromSERP.isEmpty()) {
                    processedChatGPTResponse = chatGPTService.findCompanyDetailsFromText(textFromSERP);
                    if (processedChatGPTResponse != null) {
                        setCompanyName(processedChatGPTResponse, possibleCompanyName);
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

    private static void setCompanyName(ProcessedChatGPTResponse processedChatGPTResponse, String possibleCompanyName) {
        for (Address address : processedChatGPTResponse.getAddressList()) {
            address.setCompany(possibleCompanyName);
        }
        for (Email email : processedChatGPTResponse.getEmailList()) {
            email.setCompany(possibleCompanyName);
        }
        for (Phone phone : processedChatGPTResponse.getPhoneList()) {
            phone.setCompany(possibleCompanyName);
        }
    }
}
