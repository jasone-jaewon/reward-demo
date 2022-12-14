package com.example.rewarddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RewardDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RewardDemoApplication.class, args);
    }

}
