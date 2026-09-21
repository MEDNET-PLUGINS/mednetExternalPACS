package com.mednet.externalpacs.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mednet.externalpacs")
public class ExternalPacsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalPacsApplication.class, args);
    }
}
