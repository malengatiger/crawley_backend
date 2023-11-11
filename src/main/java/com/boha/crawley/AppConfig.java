package com.boha.crawley;

import com.boha.crawley.services.RequestRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.logging.Logger;

@Configuration
@PropertySource("classpath:application.properties")
@EnableAsync
public class AppConfig {
    static final Logger logger = Logger.getLogger(AppConfig.class.getSimpleName());
    @Value("${rateLimiter.maxRequestsPerInterval}")
    private int maxRequestsPerInterval;

    @Value("${rateLimiter.requestInterval}")
    private long requestInterval;

    public AppConfig() {
        logger.info("\uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B AppConfig constructor: maxRequestsPerInterval: "
                + maxRequestsPerInterval + " requestInterval: " + requestInterval);
    }

    @Bean
    public RequestRateLimiter rateLimiter() {
        logger.info("\uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B \uD83D\uDC9B AppConfig: RateLimiter: " +
                "maxRequestsPerInterval: " + maxRequestsPerInterval
                + " requestInterval: " + requestInterval);
        return new RequestRateLimiter(maxRequestsPerInterval, requestInterval);
    }

    // Other bean configurations...
}
