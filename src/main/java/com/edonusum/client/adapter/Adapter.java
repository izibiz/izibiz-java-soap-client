package com.edonusum.client.adapter;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class Adapter extends WebServiceGatewaySupport {
    public static final String URL = "https://efaturatest.izibiz.com.tr:443";
    private final Jaxb2Marshaller jaxb2;
    public static final String PATH_TO_DOCUMENTS = System.getProperty("user.home")+"\\documents\\izibiz";

    public Adapter() {
        jaxb2 = new Jaxb2Marshaller();
    }

    public void setContextPath(String path) {
        jaxb2.setContextPath(path);

        setMarshaller(jaxb2);
        setUnmarshaller(jaxb2);
    }
}
