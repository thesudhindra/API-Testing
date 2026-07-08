package com.playground.playground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.playground.playground")
@EnableScheduling
public class PlaygroundApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaygroundApiApplication.class, args);
    }
}
