package com.boha.crawley.controllers;

import com.boha.crawley.data.Article;
import com.boha.crawley.services.ArticleService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class ScrapeController {
    private final ArticleService articleService;
    static final Logger logger = Logger.getLogger(ScrapeController.class.getSimpleName());
    static final String mm = " \uD83C\uDF0E ScrapeController:  \uD83C\uDF0E \uD83C\uDF0E \uD83C\uDF0E";

    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @PostMapping("/scrape")
    public ResponseEntity<String> handleScrapeTask(@RequestBody String payload,
                                                   @RequestHeader("email") String email) {
        logger.info(mm+" handleScrapeTask, will start ArticleService ... email: " + email);

        List<Article> articles = G.fromJson(payload,
                new TypeToken<List<Article>>() {
                }.getType());
        try {
            articleService.parseArticles(articles, email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(mm+"Scrape task completed successfully");
    }


}
