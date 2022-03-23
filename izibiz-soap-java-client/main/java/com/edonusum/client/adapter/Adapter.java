package com.edonusum.client.adapter;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Adapter extends WebServiceGatewaySupport {
    private Jaxb2Marshaller jaxb2;
    public static final String PATH_TO_DOCUMENTS = System.getProperty("user.home")+"\\documents\\izibiz";

    public Adapter() {
        jaxb2 = new Jaxb2Marshaller();
    }

    public void setContextPath(String path) {
        jaxb2.setContextPath(path);

        setMarshaller(jaxb2);
        setUnmarshaller(jaxb2);
    }

    public Marshaller marshaller(Class... clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Marshaller marshaller = context.createMarshaller();

        return marshaller;
    }

    public Unmarshaller unmarshaller(Class... clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        return unmarshaller;
    }
}
