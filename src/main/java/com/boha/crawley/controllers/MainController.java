package com.boha.crawley.controllers;

import com.boha.crawley.services.ArticleService;
import com.boha.crawley.services.ChatGPTService;
import com.boha.crawley.services.WhoIsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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
        logger.info(mm + " \uD83D\uDD35\uD83D\uDD35 \uD83D\uDD35\uD83D\uDD35 \uD83D\uDD35\uD83D\uDD35 \uD83D\uDD35\uD83D\uDD35 \uD83D\uDD35\uD83D\uDD35 " +
                "responding to hello! ... " +
                "  \uD83E\uDDE1 \uD83D\uDC9B \uD83D\uDC9A \uD83D\uDC99 \uD83D\uDC9C");

        return "Hi! my name is StealthCannabis, the Warrior!!  \uD83D\uDD35\uD83D\uDD35 "
                + df.format(new Date());
    }

    //    @Operation(summary = "sendChatGPTPrompt")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "sendChatGPTPrompt worked, funny!",
//                    content = {@Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ChatGPTService.class))}),
//
//    })
//    @GetMapping("sendChatGPTPrompt")
//    public ResponseEntity<Object> sendChatGPTPrompt(@RequestParam String prompt) {
//        try {
////            var res = chatGPTService.saySomething(prompt);
////            return ResponseEntity.ok().body(res);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(
//                    new CustomResponse(400,
//                            "sendChatGPTPrompt failed: " + e.getMessage(),
//                            df.format(new Date())));
//        }
//    }

    //    @Operation(summary = "Get Company data using domain")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "DomainInfo returned",
//                    content = {@Content(mediaType = "application/json",
//                            schema = @Schema(implementation = WhoIsService.class))}),
//
//    })
    DateFormat df = new SimpleDateFormat("MMMM dd yyyy HH:mm:ss");
    @GetMapping("/testThyme")
    public String testThyme(Model model) {
        return "index.html";
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
                            df.format(new Date())));
        }
    }
    @GetMapping("getLastResponse")
    public ResponseEntity<Object> getLastResponse(@RequestParam String email) {
        try {
            //var res = whoIsService.getDomainDetails(domain);
            return ResponseEntity.ok().body("Not implemented yet");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomResponse(400,
                            "getLastResponse failed: " + e.getMessage(),
                            df.format(new Date())));
        }
    }

    @GetMapping("/processDefaultArticles")
    @Async
    public CompletableFuture<String> processDefaultArticles(@RequestParam String email) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                articleService.parseArticlesAsync(null, email);
                return "Upload articles completed successfully";
            } catch (Exception e) {
                logger.severe("parseArticles failed: " + e.getMessage());
                throw new RuntimeException("Upload articles failed: " + e.getMessage());
            }
        });
    }

    @PostMapping("/uploadArticles")
    @Async
    public CompletableFuture<String> uploadArticles(@RequestParam String email, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        logger.info(mm + " start the fucking uploadArticles ... ");

        // Convert MultipartFile to File
        File file = convertMultipartFileToFile(multipartFile);

        return CompletableFuture.supplyAsync(() -> {
            try {
                articleService.parseArticlesAsync(file, email);
                return "Upload articles completed successfully";
            } catch (Exception e) {
                logger.severe("parseArticles failed: " + e.getMessage());
                throw new RuntimeException("Upload articles failed: " + e.getMessage());
            }
        });
    }

//    @Operation(summary = "Upload spreadsheet and scrape links found")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Spreadsheet processed OK",
//                    content = {@Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ArticleService.class))}),
//
//    })
//    @PostMapping("/uploadArticles")
//    public ResponseEntity<Object> uploadArticles(@RequestParam("file") @RequestPart MultipartFile file) {
//        // Check if the file is empty
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body(
//                    new CustomResponse(400,
//                            "uploadArticles failed, file is empty: ",
//                            df.format(new Date())));
//        }
//        String contentType = file.getContentType();
//        if (contentType != null && !contentType.equals("text/csv")) {
//            return ResponseEntity.badRequest().body(
//                    new CustomResponse(400,
//                            "uploadArticles failed, file is is not a csv format: ",
//                            df.format(new Date())));
//        }
//        try {
//            File articleFile = convertMultipartFileToFile(file);
//            var domainFile = articleService.parseArticles(articleFile);
//            FileSystemResource resource = new FileSystemResource(domainFile);
//
//            // Set the response headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDispositionFormData("attachment", domainFile.getName());
//
//            // Return the file as a ResponseEntity
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(resource);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body(
//                    new CustomResponse(400,
//                            "uploadArticles failed: " + e.getMessage(),
//                            df.format(new Date())));
//        }
//    }

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
