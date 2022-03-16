package com.edonusum.client;

import com.edonusum.client.adapter.AuthAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoapJavaClientApplication {
    private AuthAdapter authAdapter;

    @Autowired
    public SoapJavaClientApplication() {
        this.authAdapter = new AuthAdapter();
        //TODO: declarations
    }

    public static void main(String[] args) {
        SpringApplication.run(SoapJavaClientApplication.class, args);
    }


    public AuthAdapter auth() {
        return this.authAdapter;
    }
}
