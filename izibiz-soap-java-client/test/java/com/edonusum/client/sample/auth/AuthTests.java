package com.edonusum.client.sample.auth;

import com.edonusum.client.SoapJavaClientApplication;
import com.edonusum.client.wsdl.auth.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBException;
import java.io.IOException;


@SpringBootTest
public class AuthTests {

    private static SoapJavaClientApplication client = new SoapJavaClientApplication();

    public static String SESSION_ID = "";
    public static String USERNAME = "izibiz-test2";
    public static String PASSWORD = "izi321";

    @Test
    public void runAllTestsWithOrder() throws Exception {
        // login
        login_givenLoginRequest_then_returnsSessionId();

        // getGibUserList
        getGibUserList_GivenSearchParameters_then_returnsUserList();

        // checkUser
        checkUser_givenGibUserId_then_returnsGibUser();

        // logout
        logout_givenSessionId_then_logoutSucceeds();

    }

    private void login_givenLoginRequest_then_returnsSessionId() { // login
        LoginRequest req = new LoginRequest();
        
        req.setPASSWORD(PASSWORD);
        req.setUSERNAME(USERNAME);

        LoginResponse resp = client.authWS().login(req);

        Assertions.assertNull(resp.getERRORTYPE());
        
        SESSION_ID = resp.getSESSIONID();

        System.out.println(resp.getSESSIONID());
    }

    public static String login() {
        LoginRequest req = new LoginRequest();

        req.setPASSWORD(PASSWORD);
        req.setUSERNAME(USERNAME);

        return client.authWS().login(req).getSESSIONID();
    }

    public static void logout(String session) {
        LogoutRequest req = new LogoutRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(session);
        req.setREQUESTHEADER(header);

        client.authWS().logout(req);
    }

    private void logout_givenSessionId_then_logoutSucceeds() { // logout
        LogoutRequest logoutReq = new LogoutRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(SESSION_ID);

        logoutReq.setREQUESTHEADER(header);

        LogoutResponse response = client.authWS().logout(logoutReq);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    private void getGibUserList_GivenSearchParameters_then_returnsUserList() throws JAXBException, IOException { // getGibUserList
        GetGibUserListRequest request = new GetGibUserListRequest();

        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(SESSION_ID);

        request.setALIASTYPE(ALIASTYPE.ALL);
        request.setTYPE("XML");
        request.setDOCUMENTTYPE("ALL");

        request.setREQUESTHEADER(header);

        GetGibUserListResponse resp = client.authWS().getGibUserList(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getERRORTYPE());
    }
    
    private void checkUser_givenGibUserId_then_returnsGibUser() { // checkUser
        String exampleId = "4840847211";

        CheckUserRequest request = new CheckUserRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        GIBUSER gibUser = new GIBUSER();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        gibUser.setIDENTIFIER(exampleId);
        request.setUSER(gibUser);

        request.setDOCUMENTTYPE("DESPATCHADVICE");

        CheckUserResponse response = client.authWS().checkUser(request);

        Assertions.assertNotNull(response.getUSER());

        System.out.println(response.getUSER().get(0).getTITLE());
    }

}
