package com.boha.crawley;

import com.boha.crawley.services.ArticleService;
import com.boha.crawley.services.ChatGPTService;
import com.boha.crawley.data.DomainData;
import com.boha.crawley.services.WhoIsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.logging.Logger;

@SpringBootApplication

public class CrawleyApplication implements ApplicationListener<ApplicationReadyEvent> {
    static final Logger logger = Logger.getLogger(CrawleyApplication.class.getSimpleName());
    static final String mm = "\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E CrawleyApplication: ";
    public static void main(String[] args) {
        logger.info("CrawleyApplication starting ...  \uD83E\uDD22 \uD83E\uDD22 \uD83E\uDD22 \uD83E\uDD22");
        SpringApplication.run(CrawleyApplication.class, args);
        logger.info(mm + " CrawleyApplication started up!  \uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B");

    }


    @Autowired
    ArticleService articleService;
    @Autowired
    ChatGPTService chatGPTService;
    @Autowired
    WhoIsService whoIsService;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        logger.info(mm + " onApplicationEvent ... " + event);
        String text = "Apple Inc. is a technology company headquartered in Cupertino, California. I think that Symantec and TSMC are producing semiconductor " +
                "products that are sold to Johnson & Jonson as well as Citibank and Chase Manhattan Bank. Remember that things are cool at Pinkerton Factory plants " +
                " and Google LLC is a multinational technology company specializing in Internet-related services and products. " +
                "Also, IBM is good at working with other companies such as Motorola and Baidu. " +
                "Also, a camp such Harties Resorts are booming while selling shit to Ford and other outfits such as Johnson LLC and Frank Renwick Inc";

        try {
              var det = whoIsService.getDomainDetails("apple.com");
              logger.info(mm + " whoIs owner: " + det.getRegistrant().getOrganization());
//            var names = articleService.extractCompanyNames(text);
//            for (String name : names) {
//                logger.info(mm + mm + " test name found: " + name);
//            }
////            chatGPTService.saySomething(
////                    "Tell me how to get to Johannesburg, South Africa from Cleveland, Ohio");
//            List<DomainData> list = articleService.parseArticles();
//            logger.info(mm + " DomainData created: " + list.size() + "  \uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}