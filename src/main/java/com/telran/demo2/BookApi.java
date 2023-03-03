package com.telran.demo2;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookApi {

    public static void main(String[] args) {
        SpringApplication.run(BookApi.class, args);
    }

    @PreDestroy
    private void logShutdown() {
        System.out.println("Application is stopping.");
    }
}
