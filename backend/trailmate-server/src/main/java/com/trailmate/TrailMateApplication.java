package com.trailmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.trailmate")
public class TrailMateApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrailMateApplication.class, args);
    }
}

