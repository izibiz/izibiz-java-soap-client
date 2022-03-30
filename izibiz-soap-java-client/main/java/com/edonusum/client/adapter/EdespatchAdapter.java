package com.edonusum.client.adapter;

import com.edonusum.client.wsdl.edespatch.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    public List<File> getDespatchAdvicesFromDisk() {
        String path = PATH_TO_DOCUMENTS + "\\edespatch\\getDespatchAdvice";
        File file = new File(path);

        if(! file.isDirectory()) {
            return null;
        }

        File temp;
        List<File> files = new ArrayList<>();
        for(File f : file.listFiles()) {
            temp = f.listFiles()[0];
            files.add(temp);
        }

        return files;
    }

    public GetDespatchAdviceResponse getDespatchAdvice(GetDespatchAdviceRequest request) throws IOException {
        JAXBElement<GetDespatchAdviceResponse> respObj = (JAXBElement<GetDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetDespatchAdviceRequest(request));

        String path = PATH_TO_DOCUMENTS + "\\edespatch\\getDespatchAdvice\\";
        File file = new File(path);
        file.mkdirs();

        File tempFile;
        String currentPath = "";
        for(DESPATCHADVICE despatchAdvice : respObj.getValue().getDESPATCHADVICE()) {
            currentPath = path + despatchAdvice.getID();
            tempFile = new File(currentPath);
            tempFile.mkdirs();

            Files.write(Paths.get(currentPath+"\\"+despatchAdvice.getID()+".xml"), despatchAdvice.getCONTENT().getValue());
        }

        return respObj.getValue();
    }

    public LoadDespatchAdviceResponse loadDespatchAdvice(LoadDespatchAdviceRequest request) {
        JAXBElement<LoadDespatchAdviceResponse> respObj = (JAXBElement<LoadDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createLoadDespatchAdviceRequest(request));

        return respObj.getValue();
    }

    public SendDespatchAdviceResponse sendDespatchAdvice(SendDespatchAdviceRequest request) {
        JAXBElement<SendDespatchAdviceResponse> respObj = (JAXBElement<SendDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendDespatchAdviceRequest(request));

        return respObj.getValue();
    }

    public MarkDespatchAdviceResponse markDespatchAdvice(MarkDespatchAdviceRequest request) {
        JAXBElement<MarkDespatchAdviceResponse> respObj = (JAXBElement<MarkDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createMarkDespatchAdviceRequest(request));

        return respObj.getValue();
    }

    public SendReceiptAdviceResponse sendReceiptAdvice(SendReceiptAdviceRequest request) {
        JAXBElement<SendReceiptAdviceResponse> respObj = (JAXBElement<SendReceiptAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendReceiptAdviceRequest(request));

        return respObj.getValue();
    }

    public LoadReceiptAdviceResponse loadReceiptAdvice(LoadReceiptAdviceRequest request) {
        JAXBElement<LoadReceiptAdviceResponse> respObj = (JAXBElement<LoadReceiptAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createLoadReceiptAdviceRequest(request));

        return respObj.getValue();
    }

    public GetReceiptAdviceResponse getReceiptAdvice(GetReceiptAdviceRequest request) {
        JAXBElement<GetReceiptAdviceResponse> respObj = (JAXBElement<GetReceiptAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetReceiptAdviceRequest(request));

        return respObj.getValue();
    }

    public GetReceiptAdviceStatusResponse getReceiptAdviceStatus(GetReceiptAdviceStatusRequest request) {
        JAXBElement<GetReceiptAdviceStatusResponse> respObj = (JAXBElement<GetReceiptAdviceStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetReceiptAdviceStatusRequest(request));

        return respObj.getValue();
    }

    public MarkReceiptAdviceResponse markReceiptAdvice(MarkReceiptAdviceRequest request) {
        JAXBElement<MarkReceiptAdviceResponse> respObj = (JAXBElement<MarkReceiptAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createMarkReceiptAdviceRequest(request));

        return respObj.getValue();
    }
}
