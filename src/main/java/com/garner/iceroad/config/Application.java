package com.garner.iceroad.config;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.garner.iceroad")
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);

    }
}
