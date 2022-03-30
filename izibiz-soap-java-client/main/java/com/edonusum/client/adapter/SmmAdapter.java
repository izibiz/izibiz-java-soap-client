package com.edonusum.client.adapter;

import com.edonusum.client.wsdl.smm.*;

import javax.xml.bind.JAXBElement;

public class SmmAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/SmmWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.smm";
    private ObjectFactory of;

    public SmmAdapter() {
        setContextPath(CONTEXT_PATH);
        of = new ObjectFactory();
    }

    public GetSmmResponse getSmm(GetSmmRequest request) {
        JAXBElement<GetSmmResponse> respObj = (JAXBElement<GetSmmResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetSmmRequest(request));

        return respObj.getValue();
    }

    public GetSmmStatusResponse getSmmStatus(GetSmmStatusRequest request) {
        JAXBElement<GetSmmStatusResponse> respObj = (JAXBElement<GetSmmStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetSmmStatusRequest(request));

        return respObj.getValue();
    }

    public GetSmmReportResponse getSmmReport(GetSmmReportRequest request) {
        JAXBElement<GetSmmReportResponse> respObj = (JAXBElement<GetSmmReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetSmmReportRequest(request));

        return respObj.getValue();
    }

    public LoadSmmResponse loadSmm(LoadSmmRequest request) {
        JAXBElement<LoadSmmResponse> respObj = (JAXBElement<LoadSmmResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createLoadSmmRequest(request));

        return respObj.getValue();
    }

    public SendSmmResponse sendSmm(SendSmmRequest request) {
        JAXBElement<SendSmmResponse> respObj = (JAXBElement<SendSmmResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendSmmRequest(request));

        return respObj.getValue();
    }

    public CancelSmmResponse cancelSmm(CancelSmmRequest request) {
        JAXBElement<CancelSmmResponse> respObj = (JAXBElement<CancelSmmResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createCancelSmmRequest(request));

        return respObj.getValue();
    }
}
