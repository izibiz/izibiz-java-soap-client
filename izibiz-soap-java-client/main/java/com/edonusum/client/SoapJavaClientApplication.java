package com.edonusum.client;

import com.edonusum.client.adapter.AuthAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoapJavaClientApplication {

    private static AuthAdapter authAdapter;

    public static void main(String[] args) {
        SpringApplication.run(SoapJavaClientApplication.class, args);

        SoapJavaClientApplication client = new SoapJavaClientApplication();
    }


}
