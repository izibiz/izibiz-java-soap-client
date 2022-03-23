package com.edonusum.client.adapter;

import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.edespatch.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

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

    public GetDespatchAdviceResponse getDespatchAdvice(GetDespatchAdviceRequest request) throws IOException {
        JAXBElement<GetDespatchAdviceResponse> respObj = (JAXBElement<GetDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetDespatchAdviceRequest(request));

        String path = PATH_TO_DOCUMENTS + "\\edespatch\\getDespatchAdvice\\";
        File file = new File(path);
        file.mkdirs();

        File tempFile;
        ZipFile zip;
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
}
