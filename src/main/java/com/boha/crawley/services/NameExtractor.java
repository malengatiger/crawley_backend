package com.boha.crawley.services;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class NameExtractor {
    private static final Logger logger = Logger.getLogger(NameExtractor.class.getName());
    private static final String mm = "\uD83E\uDD43\uD83E\uDD43 NameExtractor \uD83E\uDD43\uD83E\uDD43";
    public static String extractPossibleNames(String text) {
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
            if (!exclusionList.contains(name)) {
                possibleNames.add(name);
            }
        }
        var num = NumberFormat.getNumberInstance().format(text.length());
        logger.info(mm+" List of possible names from text: "
                + num + " bytes is " + possibleNames.size() + " names");
        StringBuilder b = new StringBuilder();
        for (String n : possibleNames) {
            logger.info(mm+" possible name: " + n);
            b.append(n).append(" ");
        }
        String res = b.toString();
        logger.info(mm+" length of concatenated names text " + res.length());
        logger.info(mm+" shaved  \uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E"
                + (text.length() - res.length()) + " bytes by filtering; original length: " + text.length());

        return res;
    }
    // List of common verbs to exclude
    static List<String> exclusionList = Arrays.asList(
            "It", "After", "Before", "In", "Out", "The", "This", "Then", "They", "He", "Him", "Her",
            "Is", "Are", "Was", "Were", "Has", "Have", "Do", "Does", "Did", "Can", "Could", "Will",
            "Would", "Should", "May", "Might", "Must", "Shall", "Ought", "Wouldn't", "Shouldn't",
            "Couldn't", "Can't", "Don't", "Doesn't", "Didn't", "Won't", "He", "She", "Let", "If", "So",
            "You", "Well", "To", "From", "Our", "About", "Where", "Your","For", "My", "That", "We", "Them",
            "Mightn't", "Mustn't", "Isn't", "Aren't", "Wasn't", "Weren't", "Hadn't", "Not",
            "Get", "Got", "Gets", "Getting", "Gotten", "Go", "Goes", "Going", "Went", "Gone",
            "Come", "Comes", "Coming", "Came", "Coming", "Become", "Becomes", "Becoming", "Became",
            "Become", "Make", "Makes", "Making", "Made", "Making", "Take", "Takes", "Taking", "Took",
            "Taking", "Give", "Gives", "Giving", "Gave", "Giving", "Over", "As", "Jr", "One", "Two", "Three",
            "Four","Five","Six", "Seven", "Eight", "OK", "Nine"
    );
}
