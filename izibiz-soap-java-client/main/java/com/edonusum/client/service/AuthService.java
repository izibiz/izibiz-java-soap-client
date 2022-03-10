package com.edonusum.client.service;

import com.edonusum.client.wsdl.auth.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;

@Component
public class AuthService extends WebServiceGatewaySupport {

    private static final String URL = "https://efaturatest.izibiz.com.tr:443/AuthenticationWS";

    public LoginResponse login(LoginRequest loginRequest) {
        ObjectFactory of = new ObjectFactory();

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.edonusum.client.wsdl.auth");

        setMarshaller(marshaller);
        setUnmarshaller(marshaller);

        JAXBElement<LoginRequest> finalRequest = of.createLoginRequest(loginRequest);

        JAXBElement<LoginResponse> resp = (JAXBElement<LoginResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, finalRequest);

        return resp.getValue();
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.edonusum.client.wsdl.auth");

        setMarshaller(marshaller);
        setUnmarshaller(marshaller);

        ObjectFactory of = new ObjectFactory();

        JAXBElement<LogoutResponse> respObj = (JAXBElement<LogoutResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createLogoutRequest(logoutRequest));

        return respObj.getValue();
    }
}
