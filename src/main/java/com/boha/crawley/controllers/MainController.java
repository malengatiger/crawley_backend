package com.boha.crawley.controllers;

import com.boha.crawley.services.ArticleService;
import com.boha.crawley.services.ChatGPTService;
import com.boha.crawley.services.WhoIsService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
public class MainController {
    static final Logger logger = Logger.getLogger(MainController.class.getSimpleName());
    static final String mm = " \uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C MainController: ";

    @Autowired
    ArticleService articleService;
    @Autowired
    WhoIsService whoIsService;
    @Autowired
    ChatGPTService chatGPTService;

    @GetMapping("/")
    public String hello() {
        return "Hi! my name is StealthCannabis, the Ganja Warrior!!  \uD83D\uDD35\uD83D\uDD35 "
                + DateTime.now().toDateTimeISO().toString();
    }
    @GetMapping("sendChatGPTPrompt")
    public ResponseEntity<Object> sendChatGPTPrompt(@RequestParam String prompt) {
        try {
            var res = chatGPTService.saySomething(prompt);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomResponse(400,
                            "sendChatGPTPrompt failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }
    @GetMapping("getDomainInfo")
    public ResponseEntity<Object> getDomainInfo(@RequestParam String domain) {
        try {
            var res = whoIsService.getDomainDetails(domain);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomResponse(400,
                            "getDomainInfo failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }
    @GetMapping("/processDefaultArticles")
    public ResponseEntity<Object> processDefaultArticles() {
        try {
            logger.info(mm + " start the fucking processDefaultArticles ... ");
            var file = articleService.parseArticles(null);
            FileSystemResource resource = new FileSystemResource(file);

            // Set the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", file.getName());

            // Return the file as a ResponseEntity
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new CustomResponse(400,
                            "processDefaultArticles failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @PostMapping("/uploadArticles")
    public ResponseEntity<Object> uploadArticles(@RequestParam("file") @RequestPart MultipartFile file) throws IOException {
        // Check if the file is empty
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new CustomResponse(400,
                            "uploadArticles failed, file is empty: ",
                            new DateTime().toDateTimeISO().toString()));
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("text/csv")) {
            return ResponseEntity.badRequest().body(
                    new CustomResponse(400,
                            "uploadArticles failed, file is is not a csv format: ",
                            new DateTime().toDateTimeISO().toString()));
        }
        try {
            File articleFile = convertMultipartFileToFile(file);
            var domainFile = articleService.parseArticles(articleFile);
            FileSystemResource resource = new FileSystemResource(domainFile);

            // Set the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", domainFile.getName());

            // Return the file as a ResponseEntity
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new CustomResponse(400,
                            "uploadArticles failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    public static File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

    static class CustomResponse {
        int statusCode;
        String message;
        String date;

        public CustomResponse(int statusCode, String message, String date) {
            this.statusCode = statusCode;
            this.message = message;
            this.date = date;
        }
    }
}
