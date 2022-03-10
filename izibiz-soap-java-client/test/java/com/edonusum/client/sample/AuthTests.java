package com.edonusum.client.sample;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.wsdl.auth.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class AuthTests {

    @Autowired
    AuthAdapter adapter;

    @Test
    public void canLogin_returnsSessionID() {
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
        checkUser_returnsResponse(resp.getSESSIONID());
        logoutRequest_returnsResponse(resp.getSESSIONID());
    }

    @Test
    public void logoutRequest_returnsResponse(String sessionId) {
        LogoutRequest req = new LogoutRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(sessionId);

        req.setREQUESTHEADER(header);

        LogoutResponse response = adapter.logout(req);

        Assertions.assertNotNull(response);

        System.out.println("\nLogout Return Code:");
        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void getGibUserListRequest_returnsResponse(String sessionId) {
        GetGibUserListRequest request = new GetGibUserListRequest();

        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(sessionId);
        header.setAPPLICATIONNAME("?");

        request.setALIASTYPE(ALIASTYPE.ALL);
        request.setTYPE("XML");
        request.setDOCUMENTTYPE("ALL");

        request.setREQUESTHEADER(header);

        GetGibUserListResponse resp = adapter.getGibUserList(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println("\n"+resp.getERRORTYPE());
    }

    @Test
    public void checkUser_returnsResponse(String sessionId) {
        String exampleId = "4840847211";

        CheckUserRequest request = new CheckUserRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        GIBUSER gibUser = new GIBUSER();

        header.setSESSIONID(sessionId);
        request.setREQUESTHEADER(header);

        gibUser.setIDENTIFIER(exampleId);
        request.setUSER(gibUser);

        request.setDOCUMENTTYPE("DESPATCHADVICE");

        CheckUserResponse response = adapter.checkUser(request);

        Assertions.assertNotNull(response.getUSER());

        System.out.println("\n"+response.getUSER().get(0).getTITLE());
        System.out.println(response.getUSER().get(0).getTYPE());
    }


}
