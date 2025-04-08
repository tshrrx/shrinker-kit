package com.shrinker_kit.shrinker_kit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.shrinker_kit")
@EntityScan("com.shrinker_kit.model")   // Add this line
@EnableJpaRepositories("com.shrinker_kit.repository")  
public class ShrinkerKitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShrinkerKitApplication.class, args);
    }
}