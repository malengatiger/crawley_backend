package com.boha.crawley;

import com.boha.crawley.services.ArticleService;
import com.boha.crawley.services.ChatGPTService;
import com.boha.crawley.services.WhoIsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@SpringBootApplication
@EnableAutoConfiguration
@EnableWebMvc
@ComponentScan
//@ComponentScan(basePackages = "com.boha")
//@ComponentScan(basePackages = "com.boha.crawley.configs")

//@EnableSwagger2

public class CrawleyApplication implements ApplicationListener<ApplicationReadyEvent> {
    static final Logger logger = Logger.getLogger(CrawleyApplication.class.getSimpleName());
    static final String mm = "\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E CrawleyApplication: ";

    public static void main(String[] args) {
        logger.info("CrawleyApplication starting ...  \uD83E\uDD22 \uD83E\uDD22 \uD83E\uDD22 \uD83E\uDD22");
        SpringApplication.run(CrawleyApplication.class, args);
        logger.info(mm + " \uD83D\uDD35\uD83D\uDD35 CrawleyApplication started up! " +
                " \uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B \n");

    }


//    @Autowired
//    ArticleService articleService;
//    @Autowired
//    ChatGPTService chatGPTService;
//    @Autowired
//    WhoIsService whoIsService;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        logger.info(mm + " onApplicationEvent ... " + event);
        String text = "Apple Inc. is a technology company headquartered in Cupertino, California. I think that Symantec and TSMC are producing semiconductor " +
                "products that are sold to Johnson & Jonson as well as Citibank and Chase Manhattan Bank. Remember that things are cool at Pinkerton Factory plants " +
                " and Google LLC is a multinational technology company specializing in Internet-related services and products. " +
                "Also, IBM is good at working with other companies such as Motorola and Baidu. " +
                "Also, a camp such Harties Resorts are booming while selling shit to Ford and other outfits such as Johnson LLC and Frank Renwick Inc";

        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                .getHandlerMethods();

        logger.info(mm +
                " \uD83D\uDD35\uD83D\uDD35 Total Endpoints: " + map.size());

        List<String> pints = new ArrayList<>();
        for (HandlerMethod info : map.values()) {
            var pc = info.getMethod().getName();
            var pp = info.getMethod().getParameterCount();
            pints.add(pc + " - parameters: " + pp);
        }
        Collections.sort(pints);
        for (String pint : pints) {
            logger.info(mm + " \uD83D\uDD35\uD83D\uDD35 endPoint: " + pint);
        }

//
    }
}
