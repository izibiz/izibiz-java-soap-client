package com.edonusum.client.sample;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.wsdl.auth.*;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@SpringBootTest
public class AuthTests {

    @Autowired
    AuthAdapter adapter;

    @Test
    public void loginRequest_getsResponse() throws IOException {
        LoginRequest req = new LoginRequest();

        req.setUSERNAME("izibiz-test2");
        req.setPASSWORD("izi321");

        REQUESTHEADERType reqh = new REQUESTHEADERType();
        reqh.setSESSIONID("?");

        req.setREQUESTHEADER(reqh);

        LoginResponse resp = adapter.login(req);

        Assertions.assertNotNull(resp);

        System.out.println("\nSession ID:");

        System.out.println(resp.getSESSIONID());

        getGibUserListRequest_returnsResponse(resp.getSESSIONID());
    }

    @Test
    public void logoutRequest_returnsResponse(String id) {
        LogoutRequest req = new LogoutRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(id);

        req.setREQUESTHEADER(header);

        LogoutResponse response = adapter.logout(req);

        Assertions.assertNotNull(response);

        System.out.println("\nLogout Return Code:");
        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void getGibUserListRequest_returnsResponse(String id) throws IOException {
        GetGibUserListRequest ggulr = new GetGibUserListRequest();

        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(id);
        header.setAPPLICATIONNAME("?");

        ggulr.setALIASTYPE(ALIASTYPE.ALL);
        ggulr.setTYPE("XML");
        ggulr.setDOCUMENTTYPE("ALL");

        ggulr.setREQUESTHEADER(header);

        GetGibUserListResponse response = adapter.getGibUserList(ggulr);

        Assertions.assertNotNull(response.getCONTENT());
    }
}
