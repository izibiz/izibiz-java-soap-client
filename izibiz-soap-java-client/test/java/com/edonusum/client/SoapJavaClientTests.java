package com.edonusum.client;

import com.edonusum.client.wsdl.auth.LoginRequest;
import com.edonusum.client.wsdl.auth.LoginResponse;
import com.edonusum.client.wsdl.auth.REQUESTHEADERType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class SoapJavaClientTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void canInitializeClient() {
        SoapJavaClientApplication client = new SoapJavaClientApplication();

        LoginRequest request = new LoginRequest();
        request.setREQUESTHEADER(new REQUESTHEADERType());
        request.setUSERNAME("izibiz-test2");
        request.setPASSWORD("izi321");

        LoginResponse response = client.auth().login(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getSESSIONID());
    }

}
