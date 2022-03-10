package com.edonusum.client.adapter;

import com.edonusum.client.wsdl.auth.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;

@Component
public class AuthAdapter extends Adapter {

    private static final String URL = "https://efaturatest.izibiz.com.tr:443/AuthenticationWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.auth";
    private ObjectFactory of;

    public AuthAdapter() {
        of = new ObjectFactory();

        setMarshaller(jaxb2());
        setUnmarshaller(jaxb2());

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
        return response.getValue();
    }
}
