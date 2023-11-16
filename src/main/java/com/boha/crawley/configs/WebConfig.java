package com.boha.crawley.configs;

import com.google.cloud.tasks.v2.CloudTasksClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.util.logging.Logger;

;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    public WebConfig() {
        logger.info("\uD83E\uDDE1 \uD83D\uDC9B \uD83D\uDC9A \uD83D\uDC99 \uD83D\uDC9C" +
                " WebConfig constructed");
    }
    static final Logger logger = Logger.getLogger(WebConfig.class.getSimpleName());
    static final String mm = "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E WebConfig " +
            "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E";
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        return resolver;
    }
    @Bean
    public CloudTasksClient cloudTasksClient() throws IOException {
        return CloudTasksClient.create();
    }

    // Create a message channel for messages arriving from the subscription `sub-one`.
//    @Bean
//    public MessageChannel inputMessageChannel() {
//        logger.info(mm+ " Message Channel Bean");
//        return new PublishSubscribeChannel();
//    }

    // Create an inbound channel adapter to listen to the subscription `sub-one` and send
// messages to the input message channel.
//    @Bean
//    public PubSubInboundChannelAdapter inboundChannelAdapter(
//            @Qualifier("inputMessageChannel") MessageChannel messageChannel,
//            PubSubTemplate pubSubTemplate) {
//        PubSubInboundChannelAdapter adapter =
//                new PubSubInboundChannelAdapter(pubSubTemplate, "projects/stealthcannabis/subscriptions/stealthTopic-sub");
//        adapter.setOutputChannel(messageChannel);
//        adapter.setAckMode(AckMode.MANUAL);
//        adapter.setPayloadType(String.class);
//        adapter.setBeanName("pubSubInboundChannelAdapter"); // Set the beanName property
//
//        logger.info(mm+ " PubSubInboundChannelAdapter " + adapter.getBeanDescription());
//
//        return adapter;
//    }
//    @Bean
//    public MessageChannel pubsubInputChannel() {
//        logger.info(mm+ " Message Channel Bean: pubsubInputChannel DirectChannel");
//
//        return new DirectChannel();
//    }
//
//    // Define what happens to the messages arriving in the message channel.
//    @Bean
//    @ServiceActivator(inputChannel = "pubsubInputChannel")
//    public MessageHandler messageReceiver() {
//        logger.info(mm+"MessageHandler messageReceiver");
//        return message -> {
//            logger.info("Message arrived! Payload: " + new String((byte[]) message.getPayload()));
//            BasicAcknowledgeablePubsubMessage originalMessage =
//                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
//            originalMessage.ack();
//        };
//    }
//    @Bean
//    @ServiceActivator(inputChannel = "pubsubOutputChannel")
//    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
//        logger.info(mm+"MessageHandler messageSender: csvTopic");
//
//        return new PubSubMessageHandler(pubsubTemplate, "csvTopic");
//    }
//    @MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
//    public interface PubsubOutboundGateway {
//
//        void sendToPubsub(String text);
//    }
    @Bean
    public FileTemplateResolver templateResolver() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
}
