package com.edonusum.client.adapter;

import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.auth.*;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;

@Component
public class AuthAdapter extends Adapter {

    private static final String URL = "https://efaturatest.izibiz.com.tr:443/AuthenticationWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.auth";
    private ObjectFactory of;

    public AuthAdapter() {
        of = new ObjectFactory();
        setContextPath(CONTEXT_PATH);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        JAXBElement<LoginRequest> finalRequest = of.createLoginRequest(loginRequest);
        JAXBElement<LoginResponse> resp = (JAXBElement<LoginResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, finalRequest);

        return resp.getValue();
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) {
        JAXBElement<LogoutResponse> respObj = (JAXBElement<LogoutResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createLogoutRequest(logoutRequest));

        return respObj.getValue();
    }

    public GetGibUserListResponse getGibUserList(GetGibUserListRequest request) {
        JAXBElement<GetGibUserListResponse> response = (JAXBElement<GetGibUserListResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(URL, of.createGetGibUserListRequest(request));

        String pathToDocuments = System.getProperty("user.home")+"\\documents\\izibiz\\";
        ZipUtils.base64ToZip(response.getValue().getCONTENT().getValue(), pathToDocuments);

        ByteInputStream bis = new ByteInputStream(response.getValue().getCONTENT().getValue(), response.getValue().getCONTENT().getValue().length);

        return response.getValue();
    }

    public CheckUserResponse checkUser(CheckUserRequest request) {
        JAXBElement<CheckUserResponse> response = (JAXBElement<CheckUserResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(URL,of.createCheckUserRequest(request));

        return response.getValue();
    }
}
