package com.dev.quikkkk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Slf4j
public class FitHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitHubApplication.class, args);
    }

}
