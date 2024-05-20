package com.ll.clearpath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ClearpathApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClearpathApplication.class, args);
    }

}
