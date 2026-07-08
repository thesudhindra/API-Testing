package com.playground.enterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.playground.enterprise")
@EnableScheduling
public class EnterpriseApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseApiApplication.class, args);
    }
}
