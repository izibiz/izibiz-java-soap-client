package com.edonusum.client.adapter;

import com.edonusum.client.wsdl.auth.LogoutResponse;
import com.edonusum.client.wsdl.einvoice.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;

@Component
public class EinvoiceAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/EInvoiceWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.einvoice";
    private ObjectFactory of;

    public EinvoiceAdapter() {
        setContextPath(CONTEXT_PATH);
        of = new ObjectFactory();
    }

    public SendInvoiceResponse sendInvoice(SendInvoiceRequest sendInvoiceRequest) {
        JAXBElement<SendInvoiceResponse> respObj = (JAXBElement<SendInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendInvoiceRequest(sendInvoiceRequest));

        return respObj.getValue();
    }

    public GetInvoiceResponse getInvoice(GetInvoiceRequest getInvoiceRequest) {
        JAXBElement<GetInvoiceResponse> respObj = (JAXBElement<GetInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetInvoiceRequest(getInvoiceRequest));

        return respObj.getValue();
    }

    public GetInvoiceWithTypeResponse getInvoiceWithType(GetInvoiceWithTypeRequest request) {
        JAXBElement<GetInvoiceWithTypeResponse> respObj = (JAXBElement<GetInvoiceWithTypeResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetInvoiceWithTypeRequest(request));

        return respObj.getValue();
    }

    public LoadInvoiceResponse loadInvoice(LoadInvoiceRequest loadInvoiceRequest) {
        JAXBElement<LoadInvoiceResponse> respObj = (JAXBElement<LoadInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createLoadInvoiceRequest(loadInvoiceRequest));

        return respObj.getValue();
    }

    public MarkInvoiceResponse markInvoice(MarkInvoiceRequest markInvoiceRequest) {
        JAXBElement<MarkInvoiceResponse> respObj = (JAXBElement<MarkInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createMarkInvoiceRequest(markInvoiceRequest));

        return respObj.getValue();
    }

    public GetInvoiceStatusAllResponse getInvoiceStatusAll(GetInvoiceStatusAllRequest getInvoiceRequest) {
        JAXBElement<GetInvoiceStatusAllResponse> respObj = (JAXBElement<GetInvoiceStatusAllResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetInvoiceStatusAllRequest(getInvoiceRequest));

        return respObj.getValue();
    }

    public SendInvoiceResponseWithServerSignResponse sendInvoiceResponseWithServerSign(SendInvoiceResponseWithServerSignRequest request) {
        JAXBElement<SendInvoiceResponseWithServerSignResponse> respObj = (JAXBElement<SendInvoiceResponseWithServerSignResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendInvoiceResponseWithServerSignRequest(request));

        return respObj.getValue();
    }

    public GetInvoiceStatusResponse getInvoiceStatus(GetInvoiceStatusRequest request) {
        JAXBElement<GetInvoiceStatusResponse> respObj = (JAXBElement<GetInvoiceStatusResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetInvoiceStatusRequest(request));

        return respObj.getValue();
    }

}
