package com.boha.crawley.services;

import com.boha.crawley.data.ExtractionBag;
import com.boha.crawley.data.chatgpt.Address;
import com.boha.crawley.data.chatgpt.*;
import com.google.api.client.util.DateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
Which of the following are companies? Dell, dSAA, Apple Inc, Sammy Sosa, Trees, IBM, Chase Manhattan, Frank Sinatra, Philadelphia,
OpenAI, Solly Sombra, Citi Bank, Massey Ferguson
 */
@Service
@RequiredArgsConstructor
public class ChatGPTService {
    @Value("${chatGPTKey}")
    private String chatGPTKey;
    static final Logger logger = Logger.getLogger(ArticleService.class.getSimpleName());
    static final String mm = "\uD83C\uDF6F ChatGPTService: \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F";
    private static final String API_URL =
            "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client;
    static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private SecretManagerUtil secretService;

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

//    public static void main(String[] args) {
//        ChatGPTService service = new ChatGPTService();
//        service.chatGPTKey = "";
//
//        String fromSERP = "Dell SAS 1 rond-point Benjamin Franklin 34938 Montpellier " +
//                "Cedex 9. France. Informations relatives à la société. " +
//                "Capital 1 806 109,23 Euro, 351 528 229 RCS ... " +
//                "Dell Technologies Global Offices · 345 Queen Street. Level 10 & 11 · " +
//                "West Corniche Road. Etihad Towers Tower 3, Level 36 · ADGM Square, Maryah Island Al Maqam " +
//                "... Dell est disponible pour vous aider sur un ensemble de sujets. " +
//                "Que votre problème concerne le support technique ou le support Commandes, " +
//                "nos experts ont la ... Street Address 1, Street Address 2. One Dell Way. City, State/Province/Country, ZIP/PostalCode. Round Rock, TEXAS, 78682. Relationship: X, " +
//                "Executive Officer ... Address: Have a letter you want delivered to Dell headquarters. " +
//                "Address your letter to: Dell Inc. 1 Dell Way, Round Rock, TX 78682. Phone Number: Contact Dell ... DELL PW91M - SD FLEX ADDRESS CMC 8GB - " +
//                "Envoi express, dès aujourd'hui, dans le monde entier. Stock: 5 article (s). Condition: " +
//                "Reconditionné. Computer Stores in 1 Dell Way, Round Rock, TX 78682. Dell Headquarters Address. " +
//                "If you want to reach out to the headquarters by sending a physical mail, you can do so " +
//                "by sending the mail to the following address. Dell Technologies is headquartered in " +
//                "Round Rock, 1 Dell Way, United States, and has 220 office locations. " +
//                "Locations. Country, City, Address. United States ... SUB-INDUSTRY. Technology Hardware ; " +
//                "INCORPORATED. 10/22/1987 ; ADDRESS. 1 Dell Way Round Rock, TX 78682 United States ; " +
//                "WEBSITE. www.dell.com ; NO. OF EMPLOYEES. -- ... \n";
//
//        String IBM = "Corporate address. 1 New Orchard Road Armonk, New York 10504-1722. United States. Contact HR. Phone numbers. General: 1-800-426-4968 (toll-free) Shopping ... Address IBM North America 590 Madison Avenue New York, NY 10022. United States. Support. Technical Support. Learn how to set up, use, and get the most out of ... To contact IBM, fill out this form. www.ibm.com/it-it. Address IBM Italia Circonvallazione Idroscalo 20054 Segrate (MI) Italy. Support. Technical Support. IBM ... ADDRESS OF si riferisce all'indirizzo calcolato di una voce di dati. L'elemento dati può essere modificato tramite riferimenti o sottoscritto. IBM Stockholder Relations, IBM Corporation | New Orchard Road, Armonk, NY 10504 · (914) 499-7777 · infoibm@us.ibm.com · Online contact form. ADDRESS OF refers to the calculated address of a data item. The data item can be reference modified or subscripted. You may take the ADDRESS OF any Data ... The address setting is the currently selected environment name. You can retrieve the current address setting by using the ADDRESS built-in function (see ADDRESS) ... Tel: +353-1-815-4000 1850-205-205. Address IBM Ireland Limited IBM, WeWork Block D, 2nd Floor Charlemont Exchange, Charlemont St, Saint Kevin's, Dublin, D02 ... +91 80 2678 8015. Fax: +91-80-4068 4225. Address IBM India Pvt Ltd No.12, Subramanya Arcade, Bannerghatta Main Road, Bengaluru India - 560 029. Support ... Location and travel information for visitors to the IBM office on York Road, London. London City Skyline at River Thames. Address; Travel information. \n";
//
//        service.findAddressOrPhoneOrEmail(IBM, PHONE);
//        logger.info(mm + "\n\n");
//        service.findAddressOrPhoneOrEmail(IBM, EMAIL);
//        logger.info(mm + "\n\n");
//        service.findAddressOrPhoneOrEmail(IBM, ADDRESS);
//        logger.info(mm + "\n\n");
////        service.findAddressOrPhoneOrEmail("Greetings to the folks at home." +
////                " The company is at 345 Main Street, Suite 556, Jackson City, Ohio, 677009 " +
////                "but I can be reached while I'm there by calling +1 556 555 1999. Here, I " +
////                "want to give you an alternate 5770 Holbrook Road Winchester Park Ohio 87665. Let's" +
////                "see you handle that! or send it to jackson@foxtrot.com", ADDRESS);
////
////        service.findCompanies("Sometimes, when you visit Apple, you never find products from other places, such as Samsung or Huawei. " +
////                "But if you keep looking, stuff from Microsoft can be found. Also, I think Dell makes some of the best computers but " +
////                "Sony is not far behind. ASML still makes semiconductors in competition with TSMC. " +
////                "There are signs that Toyota and Tesla are going at it. Micron or Johnson & Johnson might have something to say about that!");
////
////
////        service.findAddressOrPhoneOrEmail("Greetings to the folks at home." +
////                " The company is at 345 Main Street, Suite 556, Jackson City, Ohio, 677009 " +
////                "but I can be reached while I'm there by calling +1 556 555 1999. " +
////                "If you prefer roberto@gmail.com but you also can use rory_smith@gmail.com " +
////                " There's plenty of other garbage in the file, aubrey@gmail.com but let's see!, another " +
////                "number is +1 800 555 2345. Chew on that, sucker!", PHONE);
////
////        service.findAddressOrPhoneOrEmail("Greetings to the folks at home. Listen, fool! Send me a " +
////                "fucking note at thomasina_jones@slutmasters.io. " +
////                " The company is at 345 Main Street, Suite 556, Jackson City, Ohio, 677009 " +
////                "but I can be reached while I'm there by calling +1 556 555 1999. " +
////                "If you prefer roberto@gmail.com but you also can use rory_smith@gmail.com " +
////                " There's plenty of other garbage in the file, aubrey@gmail.com but let's see!, another " +
////                "number is +1 800 555 2345. Chew on that, sucker!", EMAIL);
////
////        service.findAddressOrPhoneOrEmail("IBM Stockholder Relations, IBM Corporation | New Orchard Road, Armonk, " +
////                "NY 10504; (914) 499-7777; infoibm@us.ibm.com; Online contact form.", ADDRESS);
////        service.findAddressOrPhoneOrEmail("IBM Stockholder Relations, IBM Corporation | New Orchard Road, Armonk, " +
////                "NY 10504; (914) 499-7777; infoibm@us.ibm.com; Online contact form.", PHONE);
////        service.findAddressOrPhoneOrEmail("IBM Stockholder Relations, IBM Corporation | New Orchard Road, Armonk, " +
////                "NY 10504; (914) 499-7777; infoibm@us.ibm.com; Online contact form.", EMAIL);
////
////
////        service.findAddressOrPhoneOrEmail("Three is nothing here, except for +27 72 566 7387, don't know if that's cool," +
////                "but you should send to whatsapp 072 530 6864", PHONE);
////        service.findAddressOrPhoneOrEmail("You can buy online or call (800) MY–APPLE (800–692–7753). You can get information about an order you placed " +
////                "on the Apple Online Store through the Order Status ", PHONE);
////
////        service.findAddressOrPhoneOrEmail("\"snippet\": \"Where to find us. Corporate Address: Dell Technologies 1 Dell Way Round Rock, TX 78664. " +
////                "Find your next Career \\u003e. Call with a sales tech advisor: Call: 1-877 9000...\"\n", ADDRESS);
////        service.findAddressOrPhoneOrEmail("\"snippet\": \"Where to find us. Corporate Address: Dell Technologies 1 Dell Way Round Rock, TX 78664. " +
////                "Find your next Career \\u003e. Call with a sales tech advisor: Call: 1-877 6705...\"\n", PHONE);
//        logger.info(mm + "ChatGPT requests completed!!");
//    }

    static final int PHONE = 0;
    static final int EMAIL = 1;
    static final int ADDRESS = 2;
    static final int EVERYTHING = 3;
    List<Address> addressList = new ArrayList<>();
    List<Email> emailList = new ArrayList<>();
    List<Phone> phoneList = new ArrayList<>();


    private List<String> getCompanyNamesFromList(List<String> possibleNames) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String name : possibleNames) {
            sb.append(name).append(" ");
        }
        List<String> companies = new ArrayList<>();
        ChatGPTRequest cr = new ChatGPTRequest();
        buildChatRequest(sb.toString(), cr, "Extract names of possible commercial companies from the text. " +
                "Return list of json objects. Each result object should have 1 JSON field: companyName");
        Request r = getRequest(cr);
        String responseBody = makeTheCall(r);
        if (responseBody != null) {
            ChatGPTResponse chatGPTResponse = G.fromJson(responseBody, ChatGPTResponse.class);

            if (chatGPTResponse != null) {
                for (Choice choice : chatGPTResponse.getChoices()) {
                    if (choice.getMessage().getContent().contains("[")) {
                        JSONArray array = new JSONArray(choice.getMessage().getContent());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            companies.add(object.getString("companyName"));
                        }
                    }
                }

            }
            logger.info(mm + " \uD83C\uDF88 chatGPT found " + companies.size() +
                    " possible company names \uD83C\uDF88\uD83C\uDF88");
            for (String company : companies) {
                logger.info(mm + " company, maybe? " + company + " \uD83C\uDF88\uD83C\uDF88\uD83C\uDF88");
            }
        }
        return companies;
    }

    public List<String> getCompanyNamesFromText(String textFromWebsite) throws IOException {
        logger.info(mm + "Getting chatGPT to extract possible companies from Website" +
                " .......... " + textFromWebsite.length() + " bytes from web page");
        List<String> companies = new ArrayList<>();
        if (textFromWebsite.isEmpty()) {
            return companies;
        }

        ChatGPTRequest cr = new ChatGPTRequest();
        buildChatRequest(textFromWebsite, cr, "Extract names of possible commercial companies from the textFromWebsite. " +
                "Return list of json objects. Each result object should have 1 JSON field: companyName. Exclude government and educational institutions");
        Request r = getRequest(cr);
        String responseBody = makeTheCall(r);
        if (responseBody != null) {
            try {
                ChatGPTResponse chatGPTResponse = G.fromJson(responseBody, ChatGPTResponse.class);
                if (chatGPTResponse != null) {
                    for (Choice choice : chatGPTResponse.getChoices()) {
                        if (choice.getMessage().getContent().contains("[")) {
                            JSONArray array = new JSONArray(choice.getMessage().getContent());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                companies.add(object.getString("companyName"));
                            }
                        }
                    }
                    logger.info(mm + " companies from raw textFromWebsite: " + companies.size());
                }
            } catch (JsonSyntaxException | JSONException e) {
                logger.severe(mm + "\uD83D\uDD34\uD83D\uDD34\uD83D\uDD34" +
                        "Unable to process response from ChatGPT ... ");
                e.printStackTrace();
                return companies;
            }
            logger.info(mm + " \uD83C\uDF88\uD83C\uDF88\uD83C\uDF88 chatGPT found " + companies.size()
                    + " possible company names \uD83C\uDF88\uD83C\uDF88");

            for (String company : companies) {
                logger.info(mm + "getCompanyNamesFromText: ✅ ✅ \uD83C\uDF4E " +
                        "company: \uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E" + company);
            }
        }
        return companies;
    }


    public ProcessedChatGPTResponse findCompanyDetailsFromText(String textFromSERP) {
        if (textFromSERP == null || textFromSERP.isEmpty()) {
            return null;
        }

        ProcessedChatGPTResponse response = new ProcessedChatGPTResponse(
                UUID.randomUUID().toString(),
                new DateTime(new Date()).toString(),
                addressList, emailList, phoneList
        );

        try {
            findCompanyDetails(textFromSERP, PHONE, response);
            findCompanyDetails(textFromSERP, EMAIL, response);
            findCompanyDetails(textFromSERP, EMAIL, response);
        } catch (Exception e) {
            logger.severe(mm+" Error digging for company details: " + e.getMessage());
            e.printStackTrace();
            return null;
        }


        return response;
    }

    private void findCompanyDetails(String textFromSERP, int type,  ProcessedChatGPTResponse response) {
        logger.info(mm + "... finding company details from textFromSERP, type: " + type);
        ChatGPTRequest cr = new ChatGPTRequest();
        String addressFormat = "Each of the result json objects should have 6 JSON fields: street, city, street, zip, state, country";
        String emailFormat = "Each of the result json objects should have 1 JSON field: email";
        String phoneFormat = "Each of the result json objects should have 1 JSON field: phone";

        String prompt;
        if (type == PHONE) {
            prompt = "Extract telephone number, return json list. " +
                    phoneFormat;
        } else if (type == ADDRESS) {
            prompt = "Extract address from text, return json list. " +
                    addressFormat;
        } else if (type == EMAIL) {
            prompt = "Extract email address, return json list. " +
                    emailFormat;
        } else {
            prompt = "Extract address, telephone and email";
        }

        buildChatRequest(textFromSERP, cr, prompt);
        try {
            logger.info(mm + " findCompanyDetailsFromText: calling ChatGPT ... " +
                    "\uD83D\uDD34 " + API_URL);
            Request request = getRequest(cr);
            String responseBody;
            responseBody = makeTheCall(request);

            if (responseBody == null) return;

            ChatGPTResponse chatGPTResponse = G.fromJson(responseBody, ChatGPTResponse.class);
            switch (type) {
                case PHONE:
                    processPhoneResponse(chatGPTResponse.getChoices(), response);
                    break;
                case EMAIL:
                    processEmailResponse(chatGPTResponse.getChoices(), response);
                    break;
                case ADDRESS:
                    processAddressResponse(chatGPTResponse.getChoices(), response);
                    break;
                default:
                    break;
            }

            if (firebaseService != null) {
                firebaseService.addChatGPTResponse(chatGPTResponse);
            }
            //logger.info(mm + " end of ChatGPT response!");
        } catch (IOException e) {
            logger.severe("\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F " + e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void processEmailResponse(List<Choice> choices,  ProcessedChatGPTResponse response) {
        emailList.clear();
        for (Choice choice : choices) {
            logger.info(mm + " role: " + choice.getMessage().getRole() +
                    "  \uD83D\uDEC4\uD83D\uDEC4\uD83D\uDEC4 content: " + choice.getMessage().getContent()
                    + " \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F " + getFinishReason(choice) + "\n\n");

            try {
                if (choice.getMessage().getContent().contains("[")) {
                    JSONArray messageArray = new JSONArray(choice.getMessage().getContent());
                    handleEmail(emailList, messageArray);
                } else {
                    if (choice.getMessage().getContent().contains("{")) {
                        JSONObject obj = new JSONObject(choice.getMessage().getContent());
                        JSONArray arr = new JSONArray();
                        arr.put(obj);
                        handleEmail(emailList, arr);
                    }
                }

            } catch (JSONException e) {
                logger.severe(mm + " Error: \uD83D\uDD34\uD83D\uDD34\uD83D\uDD34" + e.getMessage());
                logger.info(mm + " error in here, choice object from ChatGPT: : " + G.toJson(choice));
                e.printStackTrace();
            }
        }
        response.setEmailList(emailList);
    }

    private void processPhoneResponse(List<Choice> choices,  ProcessedChatGPTResponse response) {
        phoneList.clear();
        for (Choice choice : choices) {
            logger.info(mm + " role: " + choice.getMessage().getRole() +
                    "  \uD83D\uDEC4\uD83D\uDEC4\uD83D\uDEC4 content: " + choice.getMessage().getContent()
                    + " \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F " + getFinishReason(choice) + "\n\n");

            try {
                if (choice.getMessage().getContent().contains("[")) {
                    JSONArray messageArray = new JSONArray(choice.getMessage().getContent());
                    handlePhone(phoneList, messageArray);
                } else {
                    if (choice.getMessage().getContent().contains("{")) {
                        JSONObject obj = new JSONObject(choice.getMessage().getContent());
                        JSONArray arr = new JSONArray();
                        arr.put(obj);
                        handlePhone(phoneList, arr);
                    }
                }

            } catch (JSONException e) {
                logger.severe(mm + " Error: \uD83D\uDD34\uD83D\uDD34\uD83D\uDD34" + e.getMessage());
                e.printStackTrace();
            }
        }
        response.setPhoneList(phoneList);
    }

    private void processAddressResponse(List<Choice> choices,  ProcessedChatGPTResponse response) {
        //logger.info("\n\n\n" + mm + " processAddressResponse request type " + "\n\n");
        addressList.clear();
        for (Choice choice : choices) {
            logger.info(mm + " role: " + choice.getMessage().getRole() +
                    "  \uD83D\uDEC4\uD83D\uDEC4\uD83D\uDEC4 content: " + choice.getMessage().getContent()
                    + " \uD83C\uDF6F\uD83C\uDF6F\uD83C\uDF6F " + getFinishReason(choice) + "\n\n");

            try {
                if (choice.getMessage().getContent().contains("[")) {
                    JSONArray messageArray = new JSONArray(choice.getMessage().getContent());
                    handleAddress(addressList, messageArray);
                } else {
                    if (choice.getMessage().getContent().contains("{")) {
                        JSONObject obj = new JSONObject(choice.getMessage().getContent());
                        JSONArray arr = new JSONArray();
                        arr.put(obj);
                        handleAddress(addressList, arr);
                    }
                }
            } catch (JSONException e) {
                logger.severe(mm + " Error: \uD83D\uDD34\uD83D\uDD34\uD83D\uDD34" + e.getMessage());
                logger.info(mm + "Error in chatGPT choice : " + G.toJson(choice));
                e.printStackTrace();
            }
        }
        response.setAddressList(addressList);
    }

    @Nullable
    private String makeTheCall(Request request) throws IOException {
        logger.info(mm + " ............... makeTheCall for ChatGPT:  " +
                "\uD83D\uDC9A\uD83D\uDC9A\uD83D\uDC9A" +
                " Calling ChatGPT using OkHttpClient .......................  " +
                "\uD83D\uDC9A\uD83D\uDC9A");
        long start = System.currentTimeMillis();
        String responseBody;
        try (Response mResponse = client.newCall(request).execute()) {
            assert mResponse.body() != null;
            responseBody = mResponse.body().string();
        }
        if (responseBody.contains("error") || responseBody.contains("invalid_request_error")) {
            ChatGPTError error = G.fromJson(responseBody, ChatGPTError.class);
            logger.severe(mm + " \uD83D\uDD34 ERROR from chatGPT: "
                    + G.toJson(error) + " \uD83D\uDD34\uD83D\uDD34\uD83D\uDD34");
            return null;
        }

        logger.info(mm + " ChatGPT has responded!!  " +
                "response length: " + responseBody.length() +
                " bytes. \uD83D\uDC9A \uD83D\uDC9A \uD83D\uDC9A");
        printElapsed(start);
        return responseBody;
    }
    private static void printElapsed(long startTime) {
        //
        long endTime = System.currentTimeMillis();
        long elapsedTimeMillis = endTime - startTime;
        double elapsedTimeMinutes = elapsedTimeMillis / 1000.0 / 60;
        double elapsedTimeSeconds = elapsedTimeMillis / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String minutes = decimalFormat.format(elapsedTimeMinutes);
        String seconds = decimalFormat.format(elapsedTimeSeconds);
//10210 option 1 tekom technical
        logger.info(mm+"\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A ChatGPT complete: "
                + minutes + " elapsed minutes;  " + seconds + " seconds " +
                "\uD83E\uDD4F \uD83D\uDD35\uD83D\uDD35");
    }
    private static void buildChatRequest(String string, ChatGPTRequest cr, String prompt) {
        cr.setMessages(new ArrayList<>());
        cr.setModel("gpt-4");
        cr.getMessages().add(new Message("system", prompt));
        cr.getMessages().add(new Message("user", string));
    }

    private Request getRequest(ChatGPTRequest cr) {
        RequestBody body = RequestBody.create(mediaType,
                G.toJson(cr));

        return new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + chatGPTKey)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    private void handleEmail(List<Email> emailList, JSONArray obj) {
        logger.info(mm + " .... handling email ....................... JSONArray:" + obj.toString());

        for (int i = 0; i < obj.length(); i++) {
            JSONObject mObj = obj.getJSONObject(i);
            Email p = new Email();
            try {
                p.setEmail(mObj.getString("email"));
                emailList.add(p);
            } catch (JSONException e) {
                logger.severe(mm+ " JSON Error for email: " + e.getMessage());
            }
        }
        HashMap<String, Email> map = new HashMap<>();
        for (Email email : emailList) {
            map.put(email.getEmail(), email);
        }
        List<Email> mList = map.values().stream().toList();
        emailList.clear();
        emailList.addAll(mList);
    }

    private static void handlePhone(List<Phone> phoneList, JSONArray obj) {
        logger.info(mm + " .... handling phone ....................... JSONArray:" + obj.toString());
        for (int i = 0; i < obj.length(); i++) {
            JSONObject mObj = obj.getJSONObject(i);
            Phone p = new Phone();
            try {
                p.setPhone(mObj.getString("phone"));
                phoneList.add(p);
            } catch (JSONException e) {
                logger.severe(mm+ " JSON Error for phone: " + e.getMessage());
            }
        }
        HashMap<String, Phone> map = new HashMap<>();
        for (Phone p : phoneList) {
            map.put(p.getPhone(), p);
        }
        List<Phone> mList = map.values().stream().toList();
        phoneList.clear();
        phoneList.addAll(mList);
    }

    private static void handleAddress(List<Address> addressList, JSONArray obj) {
        logger.info(mm + " .... handling address ......................................... " +
                "JSONArray:" + obj.toString());

        for (int i = 0; i < obj.length(); i++) {

            try {
                JSONObject mObj = obj.getJSONObject(i);
                Address arr = new Address();
                assert mObj != null;

                arr.setCity(mObj.getString("city"));
                arr.setCountry(mObj.getString("country"));
                arr.setState(mObj.getString("state"));
                arr.setZip(mObj.getString("zip"));
                arr.setStreet(mObj.getString("street"));
                addressList.add(arr);
            } catch (JSONException e) {
                logger.severe(mm + " JSON ERROR while handling address: " + e.getMessage());
            }
        }

        HashMap<String, Address> map = new HashMap<>();
        for (Address addr : addressList) {
            map.put(addr.toString(), addr);
        }
        List<Address> mList = map.values().stream().toList();
        addressList.clear();
        addressList.addAll(mList);
    }


    private String getFinishReason(Choice choice) {
        return switch (choice.getFinishReason()) {
            case STOP -> "getFinishReason:  \uD83D\uDC99 Everything's cool with ChatGPT response";
            case LENGTH -> "getFinishReason:  \uD83D\uDC99 The prompt is too long, probably";
            case FUNCTION_CALL -> "getFinishReason:  \uD83D\uDC99 A function call had to be made";
            case CONTENT_FILTER -> "getFinishReason:  \uD83D\uDC99 Seems like you have a problem with the authorities";
            case NULL -> "getFinishReason:  \uD83D\uDC99 We are null; which is nothing, really ...";
            default -> "getFinishReason:  \uD83D\uDC99 Default, wtf!";
        };
    }

    static final String STOP = "stop";
    static final String LENGTH = "length";
    static final String FUNCTION_CALL = "function_call";
    static final String CONTENT_FILTER = "content_filter";
    static final String NULL = "null";


}
