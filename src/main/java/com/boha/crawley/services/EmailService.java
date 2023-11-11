package com.boha.crawley.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class EmailService {
    static final Logger logger = Logger.getLogger(EmailService.class.getSimpleName());
    static final String mm = "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E" +
            " EmailService: \uD83C\uDF4E";
    @Value("${gmailAddress}")
    private String gmailAddress;
    @Value("${gmailPassword}")
    private String gmailPassword;
    public void sendEmail(String recipient, String subject, String content) throws MessagingException {
        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Set up authentication

        // Create a mail session
        javax.mail.Session session = javax.mail.Session.getInstance(properties);

        // Create a new email message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(gmailAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        message.setText(content);
        message.setContent(content, "text/html");


        // Authenticate with the SMTP server
        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", 587, gmailAddress, gmailPassword);

        // Send the email
        transport.sendMessage(message, message.getAllRecipients());
        logger.info(mm + " Email has been sent to : " + recipient);
        // Close the connection
        transport.close();
    }
}