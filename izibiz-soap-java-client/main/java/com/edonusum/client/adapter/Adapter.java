package com.edonusum.client.adapter;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class Adapter extends WebServiceGatewaySupport {
    private Jaxb2Marshaller jaxb2;

    public Adapter() {
        jaxb2 = new Jaxb2Marshaller();
    }

    public void setContextPath(String path) {
        jaxb2.setContextPath(path);

        setMarshaller(jaxb2);
        setUnmarshaller(jaxb2);
    }

    public Jaxb2Marshaller jaxb2() {
        return this.jaxb2;
    }
}
