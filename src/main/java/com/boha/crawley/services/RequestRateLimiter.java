package com.boha.crawley.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

//@Component
public class RequestRateLimiter {
    private final Semaphore semaphore;
//    @Value("${rateLimiter.maxRequestsPerInterval}")
//    private int maxRequestsPerInterval;

    @Value("${rateLimiter.requestInterval}")
    private long requestInterval;
    public RequestRateLimiter(int maxRequestsPerInterval, long requestInterval) {
        this.semaphore = new Semaphore(maxRequestsPerInterval);
        this.requestInterval = requestInterval;
        //this.maxRequestsPerInterval = maxRequestsPerInterval;
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire(); // Acquire a permit
        Thread.sleep(requestInterval); // Pause for the specified interval
    }

    public void release() {
        semaphore.release(); // Release a permit
    }
}