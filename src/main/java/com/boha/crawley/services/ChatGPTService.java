package com.boha.crawley.services;

import com.boha.crawley.data.chatgpt.ChatGPTRequest;
import com.boha.crawley.data.chatgpt.ChatGPTResponse;
import com.boha.crawley.data.chatgpt.Choice;
import com.boha.crawley.data.chatgpt.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Which of the following are companies? Dell, dSAA, Apple Inc, Sammy Sosa, Trees, IBM, Chase Manhattan, Frank Sinatra, Philadelphia,
OpenAI, Solly Sombra, Citi Bank, Massey Ferguson
 */
@Service
@RequiredArgsConstructor
public class ChatGPTService {
    static final Logger logger = Logger.getLogger(ArticleService.class.getSimpleName());
    static final String mm = "\uD83C\uDF6F ChatGPTService: \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F";
    //https://api.openai.com/v1/chat/completions
    private static final String API_URL =
            "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client;
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private FirebaseService firebaseService;
    @Value("${chatGPTKey}")
    public String chatGPTKey;

    public ChatGPTService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Set the maximum time to establish a connection
                .readTimeout(600, TimeUnit.SECONDS) // Set the maximum time to read data from the server
                .writeTimeout(600, TimeUnit.SECONDS) // Set the maximum time to write data to the server
                .retryOnConnectionFailure(true)
                .build();
    }

    String prefix = "Pick out company names from the text and return list";
    String suffix = ". Return results in a list of strings";
    static final MediaType mediaType = MediaType.parse("application/json");

    public static void main(String[] args) {
        ChatGPTService service = new ChatGPTService();
        service.chatGPTKey = "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";

        service.findAddressOrPhone("Greetings to the folks at home." +
                " The company is at 345 Main Street, Suite 556, Jackson City, Ohio, 677009 " +
                "but I can be reached while I'm there by calling +1 556 555 1999. Here, I " +
                "want to give you an alternate 5770 Holbrook Road Winchester Park Ohio 87665. Let's" +
                "see you handle that! or send it to jackson@foxtrot.com", ADDRESS);

        service.findAddressOrPhone("Greetings to the folks at home." +
                " The company is at 345 Main Street, Suite 556, Jackson City, Ohio, 677009 " +
                "but I can be reached while I'm there by calling +1 556 555 1999. " +
                "If you prefer roberto@gmail.com but you also can use rory_smith@gmail.com " +
                " There's plenty of other garbage in the file, aubrey@gmail.com but let's see!, another " +
                "number is +1 800 555 2345. Chew on that, sucker!", PHONE);

        service.findAddressOrPhone("Greetings to the folks at home. Listen, fool! Send me a " +
                "fucking note at thomasina_jones@slutmasters.io. " +
                " The company is at 345 Main Street, Suite 556, Jackson City, Ohio, 677009 " +
                "but I can be reached while I'm there by calling +1 556 555 1999. " +
                "If you prefer roberto@gmail.com but you also can use rory_smith@gmail.com " +
                " There's plenty of other garbage in the file, aubrey@gmail.com but let's see!, another " +
                "number is +1 800 555 2345. Chew on that, sucker!", EMAIL);

//        service.findAddressOrPhone("Greetings to the folks at home. Listen, fool! Send me a " +
//                "fucking note at thomasina_jones@slutmasters.io. " +
//                " The company is at 345 Main Street, Suite 556, Jackson City, Ohio, 677009 " +
//                "but I can be reached while I'm there by calling +1 556 555 1999. " +
//                "If you prefer roberto@gmail.com but you also can use rory_smith@gmail.com. I also have a " +
//                "house at  543 Jupiter Road Peachtree Park GA 87009 " +
//                " There's plenty of other garbage in the file, aubrey@gmail.com but let's see!, another " +
//                "number is +1 800 555 2345. Chew on that, sucker!", EVERYTHING);

        service.findAddressOrPhone("Three is nothing here, except for +27 72 566 7387, don't know if that's cool," +
                "but you should send to whatsapp 072 530 6864",PHONE);

    }

    static final int PHONE = 0;
    static final int ADDRESS = 1;
    static final int EMAIL = 2;
    static final int EVERYTHING = 3;


    public String findAddressOrPhone(String string, int phone) {
        ChatGPTRequest cr = new ChatGPTRequest();
        String prompt;
        switch (phone) {
            case PHONE -> prompt = "Extract telephone number, return json list with property phone";
            case ADDRESS ->
                    prompt = "Extract addresses, return json list with properties address { street, city, zip, state, country }";
            case EMAIL -> prompt = "Extract email address, return json list with property email";
            case EVERYTHING -> prompt = "Extract address, telephone and email return json list with properties address: (street, city, zip, state, country), " +
                    "email as json list, phone as json list. the addresses, phones and emails in separate bins";
            default -> prompt = "mmmExtract address, telephone and email";
        }

        cr.setMessages(new ArrayList<>());
        cr.setModel("gpt-4");
        cr.getMessages().add(new Message("system", prompt));
        cr.getMessages().add(new Message("user", string));
        try {
            logger.info(mm + " calling ChatGPT ... \uD83D\uDD34 " + API_URL);
            RequestBody body = RequestBody.create(mediaType,
                    G.toJson(cr));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + chatGPTKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            String responseBody;
            try (Response response = client.newCall(request).execute()) {
                assert response.body() != null;
                responseBody = response.body().string();
            }
            logger.info(mm + mm + mm + " ChatGPT has responded!");
            ChatGPTResponse chatGPTResponse = G.fromJson(responseBody, ChatGPTResponse.class);
//                logger.info(G.toJson(chatGPTResponse));
            for (Choice choice : chatGPTResponse.getChoices()) {
                logger.info(mm + " role: " + choice.getMessage().getRole() +
                        "  \uD83D\uDEC4\uD83D\uDEC4\uD83D\uDEC4 content: " + choice.getMessage().getContent()
                        + " \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F " + getFinishReason(choice));
            }

            if (firebaseService != null) {
                firebaseService.addChatGPTResponse(chatGPTResponse);
            }
            logger.info(mm + " end of ChatGPT response!");
        } catch (IOException e) {
            logger.severe("\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F " + e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "not yet";
    }

    public List<String> findCompanies(List<String> strings) {
        logger.info(mm + " findCompanies using ChatGPT ... \uD83D\uDD34 "
                + strings.size() + " strings to check \uD83D\uDD34");
        List<Message> messages = new ArrayList<>();
        List<String> companies = new ArrayList<>();
        buildExclusions();

        for (String mString : strings) {
            logger.info(mm + " prompt string length : " + (mString.length() / 1024) + "K bytes");
            ChatGPTRequest cr = new ChatGPTRequest();
            messages.clear();
            messages.add(new Message("system", prefix));

            cr.setModel("gpt-4");
            cr.setMessages(messages);
            List<String> names = extractPossibleNames(mString);
            String promptString = getPromptString(names);
            messages.add(new Message("user", promptString));

            try {
                logger.info(mm + " calling ChatGPT ... \uD83D\uDD34 " + API_URL);
                RequestBody body = RequestBody.create(mediaType,
                        G.toJson(cr));
                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + chatGPTKey)
                        .addHeader("Content-Type", "application/json")
                        .build();

                String responseBody;
                try (Response response = client.newCall(request).execute()) {
                    assert response.body() != null;
                    responseBody = response.body().string();
                }
                logger.info(mm + mm + mm + " ChatGPT has responded!");
                ChatGPTResponse chatGPTResponse = G.fromJson(responseBody, ChatGPTResponse.class);
//                logger.info(G.toJson(chatGPTResponse));
                for (Choice choice : chatGPTResponse.getChoices()) {
                    logger.info(mm + " role: " + choice.getMessage().getRole() +
                            " \uD83E\uDD66 content: " + choice.getMessage().getContent()
                            + " \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F " + getFinishReason(choice));
                }
                List<String> extractedNames = extractListFromJson(chatGPTResponse.getChoices().get(0).getMessage().getContent());
                for (String n : extractedNames) {
                    logger.info(mm + " \uD83D\uDD35 possible company name: \uD83D\uDD35 " + n + " \uD83D\uDD34\uD83D\uDD34");
                }
                firebaseService.addChatGPTResponse(chatGPTResponse);
                logger.info(mm + " end of ChatGPT response!");
            } catch (IOException e) {
                logger.severe("\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F " + e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return companies;
    }

    private List<String> extractListFromJson(String json) {
        List<String> result = new ArrayList<>();

        try {
            Gson gson = new Gson();
            result = G.fromJson(json, List.class);
        } catch (Exception e) {
            // If an exception occurs, return an empty list
            return new ArrayList<>();
        }

        return result;
    }

    private String getPromptString(List<String> strings) {
        logger.info(mm + " filtering list of \uD83D\uDD34" + strings.size());
//        HashMap<String, String> map = new HashMap<>();
//        for (String string : strings) {
//            map.put(string, string);
//        }
//        List<String> filteredList = map.values().stream().toList();
//        logger.info(mm + " filtered list: \uD83D\uDD34 " + filteredList.size());
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s).append(" ");
        }

        var promptString = sb.toString();
        logger.info(mm + " getPromptString \uD83D\uDD35\uD83D\uDD35"
                + (promptString.length()) + "  bytes \uD83D\uDD35\uD83D\uDD35");
        logger.info(mm + " Prompt String \uD83D\uDD35\uD83D\uDD35"
                + (promptString) + "   \uD83D\uDD35\uD83D\uDD35");
        return promptString;
    }

    private String getFinishReason(Choice choice) {
        /*
        Every response will include a finish_reason.
        The possible values for finish_reason are:

        stop: API returned complete message, or a message terminated by one of the stop sequences
            provided via the stop parameter
        length: Incomplete model output due to max_tokens parameter or token limit
        function_call: The model decided to call a function
        content_filter: Omitted content due to a flag from our content filters
        null: API response still in progress or incomplete
        Depending on input parameters, the model response may include different information.
         */
        return switch (choice.getFinishReason()) {
            case STOP -> "Everything's cool with ChatGPT response";
            case LENGTH -> "The prompt is too long, probably";
            case FUNCTION_CALL -> "A function call had to be made";
            case CONTENT_FILTER -> "Seems like you have a problem with the authorities";
            case NULL -> "We are null; which is nothing, really ...";
            default -> "Default, wtf!";
        };
    }

    static final String STOP = "stop";
    static final String LENGTH = "length";
    static final String FUNCTION_CALL = "function_call";
    static final String CONTENT_FILTER = "content_filter";
    static final String NULL = "null";

    public List<String> extractPossibleNames(String text) {
        List<String> possibleNames = new ArrayList<>();

        // Regular expression pattern to match possible names
        String regex = "\\b[A-Z][a-zA-Z]+\\b";

        // Create a Pattern object with the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object with the input text
        Matcher matcher = pattern.matcher(text);

        // Iterate through the matches and add them to the list
        while (matcher.find()) {
            String name = matcher.group();
            possibleNames.add(name);
        }
        List<String> fNames = new ArrayList<>();
        for (String n : possibleNames) {
            if (include(n)) {
                fNames.add(n);
                //logger.info(mm + " possible name: " + n);
            }
        }

        return fNames;
    }

    HashMap<String, String> exclusionMap = new HashMap<>();

    private boolean include(String name) {
        return !exclusionMap.containsKey(name);
    }

    private void buildExclusions() {
        exclusionMap.put("It", "It");
        exclusionMap.put("No", "No");
        exclusionMap.put("Yes", "Yes");
        exclusionMap.put("So", "So");
        exclusionMap.put("You", "You");
        exclusionMap.put("Wow", "Wow");
        exclusionMap.put("He", "He");
        exclusionMap.put("She", "She");
        exclusionMap.put("Name", "Name");
        exclusionMap.put("My", "My");
        exclusionMap.put("What", "What");
        exclusionMap.put("Comment", "Comment");
        exclusionMap.put("Oh", "Oh");
        exclusionMap.put("Your", "Your");
        exclusionMap.put("And", "And");
        exclusionMap.put("We", "We");
        exclusionMap.put("On", "On");
        exclusionMap.put("Me", "Me");
        exclusionMap.put("While", "While");
        exclusionMap.put("They", "They");
        exclusionMap.put("From", "From");
        exclusionMap.put("His", "His");
        exclusionMap.put("The", "The");
        exclusionMap.put("Up", "Up");
        exclusionMap.put("When", "When");
        exclusionMap.put("For", "For");
        exclusionMap.put("Now", "Now");
        exclusionMap.put("This", "This");
        exclusionMap.put("By", "By");
        exclusionMap.put("If", "If");

    }
}
