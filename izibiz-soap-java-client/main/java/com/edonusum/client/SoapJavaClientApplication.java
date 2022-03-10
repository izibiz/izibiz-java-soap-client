package com.edonusum.client;

import com.edonusum.client.service.AuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoapJavaClientApplication {
    AuthService authService;

    public static void main(String[] args) {
        SpringApplication.run(SoapJavaClientApplication.class, args);
    }

}
