package com.edonusum.client.sample.auth;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.wsdl.auth.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;


@SpringBootTest
public class AuthTests {

    AuthAdapter adapter = new AuthAdapter();

    // session id gereken testlerde kullanılması için yazılmıştır, login ile aynı işi yapmaktadır
    public static LoginRequest prepareLoginRequest() {
        LoginRequest req = new LoginRequest();

        req.setUSERNAME("izibiz-test2");
        req.setPASSWORD("izi321");

        REQUESTHEADERType reqh = new REQUESTHEADERType();
        reqh.setSESSIONID("?");

        req.setREQUESTHEADER(reqh);

        return req;
    }

    @DisplayName("Oturum Açma")
    @Test
    public void givenLoginRequest_then_returnsSessionId() { // login
        LoginRequest req = prepareLoginRequest();

        LoginResponse resp = adapter.login(req);

        Assertions.assertNotNull(resp);

        System.out.println(resp.getSESSIONID());
    }

    @DisplayName("Oturum Kapatma")
    @Test
    public void givenSessionId_then_logoutSuccess() { // logout
        LoginRequest req = prepareLoginRequest();

        String sessionId = adapter.login(req).getSESSIONID();

        LogoutRequest logoutReq = new LogoutRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(sessionId);

        logoutReq.setREQUESTHEADER(header);

        LogoutResponse response = adapter.logout(logoutReq);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    @DisplayName("Mükellef Listesi Çekme")
    @Test
    public void givenAlias_andGivenType_then_returnsGibUserList() throws JAXBException, IOException, XMLStreamException { // getGibUserList
        LoginRequest req = prepareLoginRequest();

        String sessionId = adapter.login(req).getSESSIONID();

        GetGibUserListRequest request = new GetGibUserListRequest();

        REQUESTHEADERType header = new REQUESTHEADERType();
        header.setSESSIONID(sessionId);

        request.setALIASTYPE(ALIASTYPE.ALL);
        request.setTYPE("XML");
        request.setDOCUMENTTYPE("ALL");

        request.setREQUESTHEADER(header);

        GetGibUserListResponse resp = adapter.getGibUserList(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getERRORTYPE());
    }

    @DisplayName("E-Fatura Mükellefi Sorgulama")
    @Test
    public void givenGibUserId_then_returnsGibUser() { // checkUser
        LoginRequest req = prepareLoginRequest();

        String sessionId = adapter.login(req).getSESSIONID();

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

        System.out.println(response.getUSER().get(0).getTITLE());
    }

}
