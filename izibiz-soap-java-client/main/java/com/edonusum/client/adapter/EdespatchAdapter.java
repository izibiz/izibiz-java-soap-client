package com.edonusum.client.adapter;

import com.edonusum.client.wsdl.edespatch.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;

@Component
public class EdespatchAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/EIrsaliyeWS/EIrsaliye";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.edespatch";
    private ObjectFactory of;

    public EdespatchAdapter() {
        of = new ObjectFactory();
        setContextPath(CONTEXT_PATH);
    }

    public GetDespatchAdviceStatusResponse getDespatchAdviseStatus(GetDespatchAdviceStatusRequest request) {
        JAXBElement<GetDespatchAdviceStatusResponse> respObj = (JAXBElement<GetDespatchAdviceStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetDespatchAdviceStatusRequest(request));

        return respObj.getValue();
    }

    public GetDespatchAdviceResponse getDespatchAdvice(GetDespatchAdviceRequest request) {
        JAXBElement<GetDespatchAdviceResponse> respObj = (JAXBElement<GetDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetDespatchAdviceRequest(request));

        return respObj.getValue();
    }

    public LoadDespatchAdviceResponse loadDespatchAdvice(LoadDespatchAdviceRequest request) {
        JAXBElement<LoadDespatchAdviceResponse> respObj = (JAXBElement<LoadDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createLoadDespatchAdviceRequest(request));

        return respObj.getValue();
    }
}
