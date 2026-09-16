package com.example.designpatterns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Single entry point for the whole design-patterns project. Component
 * scanning starts here at the {@code com.example.designpatterns} root, so it
 * automatically covers {@code creational}, {@code behavioral}, and
 * {@code structural} as pattern demos are added to each.
 */
@SpringBootApplication
public class DesignPatternsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesignPatternsApplication.class, args);
    }
}
