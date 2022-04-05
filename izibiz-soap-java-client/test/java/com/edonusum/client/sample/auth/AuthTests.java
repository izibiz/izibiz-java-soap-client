package com.edonusum.client.sample.auth;

import com.edonusum.client.SoapJavaClientApplication;
import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.wsdl.auth.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@DisplayName("Kimlik doğrulama servisi")
public class AuthTests {
    private static SoapJavaClientApplication client = new SoapJavaClientApplication();
    
    @Autowired
    private AuthAdapter adapter;

    private static String SESSION_ID = "";
    private static final String USERNAME = "izibiz-test2";
    private static final String PASSWORD = "izi321";

    @Test
    @Order(1)
    @DisplayName("Giriş yapma")
    public void login_givenLoginRequest_then_returnsSessionId() { // login
        LoginRequest req = new LoginRequest();
        
        req.setPASSWORD(PASSWORD);
        req.setUSERNAME(USERNAME);
        
        LoginResponse resp = adapter.login(req);

        Assertions.assertNull(resp.getERRORTYPE());
        
        SESSION_ID = resp.getSESSIONID();

        System.out.println(resp.getSESSIONID());
    }

    @Test
    @Order(2)
    @DisplayName("Mükellef listesi çekme")
    public void getGibUserList_GivenSearchParameters_then_returnsUserList() throws Exception { // getGibUserList
        GetGibUserListRequest request = new GetGibUserListRequest();

        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(SESSION_ID);

        request.setALIASTYPE(ALIASTYPE.ALL);
        request.setTYPE("XML");
        request.setDOCUMENTTYPE("ALL");

        request.setREQUESTHEADER(header);

        GetGibUserListResponse resp = adapter.getGibUserList(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getERRORTYPE());
    }

    @Test
    @Order(3)
    @DisplayName("Mükellef sorgulama")
    public void checkUser_givenGibUserId_then_returnsGibUser() { // checkUser
        String exampleId = "4840847211";

        CheckUserRequest request = new CheckUserRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        GIBUSER gibUser = new GIBUSER();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        gibUser.setIDENTIFIER(exampleId);
        request.setUSER(gibUser);

        request.setDOCUMENTTYPE("DESPATCHADVICE");

        CheckUserResponse response = adapter.checkUser(request);

        Assertions.assertNotNull(response.getUSER());

        System.out.println(response.getUSER().get(0).getTITLE());
    }

    @Test
    @Order(4)
    @DisplayName("Çıkış yapma")
    public void logout_givenSessionId_then_logoutSucceeds() { // logout
        LogoutRequest logoutReq = new LogoutRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(SESSION_ID);

        logoutReq.setREQUESTHEADER(header);

        LogoutResponse response = adapter.logout(logoutReq);

        Assertions.assertNull(response.getERRORTYPE());
        Assertions.assertEquals(0, response.getREQUESTRETURN().getRETURNCODE());

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
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

}
