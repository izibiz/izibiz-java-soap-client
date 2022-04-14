package com.edonusum.client.adapter;

import com.edonusum.client.wsdl.reconciliation.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;

@Component
public class ReconciliationAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/ReconciliationWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.reconciliation";
    private final ObjectFactory of;

    public ReconciliationAdapter() {
        setContextPath(CONTEXT_PATH);
        of = new ObjectFactory();
    }

    public GetReconciliationStatusResponse getReconciliationStatus(GetReconciliationStatusRequest request) {
        JAXBElement<GetReconciliationStatusResponse> respObj = (JAXBElement<GetReconciliationStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetReconciliationStatusRequest(request));

        return respObj.getValue();
    }

    public SendReconciliationResponse sendReconciliation(SendReconciliationRequest request) {
        JAXBElement<SendReconciliationResponse> respObj = (JAXBElement<SendReconciliationResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendReconciliationRequest(request));

        return respObj.getValue();
    }

    public SendMailReconciliationResponse sendReconciliationMail(SendMailReconciliationRequest request) {
        JAXBElement<SendMailReconciliationResponse> respObj = (JAXBElement<SendMailReconciliationResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendMailReconciliationRequest(request));

        return respObj.getValue();
    }
}
