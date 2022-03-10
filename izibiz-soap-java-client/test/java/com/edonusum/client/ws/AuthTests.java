package com.edonusum.client.ws;

import com.edonusum.client.service.AuthService;
import com.edonusum.client.wsdl.auth.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

@SpringBootTest
public class AuthTests {

    @Autowired
    AuthService serv;

    @Test
    public void loginRequest_getsResponse() throws JAXBException, FileNotFoundException {
        LoginRequest req = new LoginRequest();

        req.setUSERNAME("izibiz-test2");
        req.setPASSWORD("izi321");

        REQUESTHEADERType reqh = new REQUESTHEADERType();
        reqh.setSESSIONID("?");

        req.setREQUESTHEADER(reqh);

        LoginResponse resp = serv.login(req);

        Assertions.assertNotNull(resp);

        System.out.println("\nSession ID:");

        System.out.println(resp.getSESSIONID());

        logoutRequest_returnsResponse(resp.getSESSIONID());
    }

    @Test
    public void logoutRequest_returnsResponse(String id) throws JAXBException, FileNotFoundException {
        LogoutRequest req = new LogoutRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(id);

        req.setREQUESTHEADER(header);

        LogoutResponse response = serv.logout(req);

        Assertions.assertNotNull(response);

        System.out.println("\nLogout Return Code:");
        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }
}
