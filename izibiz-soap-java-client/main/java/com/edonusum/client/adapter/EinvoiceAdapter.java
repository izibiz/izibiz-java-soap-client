package com.edonusum.client.adapter;

import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.auth.LogoutResponse;
import com.edonusum.client.wsdl.einvoice.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipFile;

@Component
public class EinvoiceAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/EInvoiceWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.einvoice";
    private ObjectFactory of;

    public EinvoiceAdapter() {
        setContextPath(CONTEXT_PATH);
        of = new ObjectFactory();
    }

    private void extractInvoices(Class clazz, String dir, JAXBElement element, List<INVOICE> invoices) {
        MarshallToXML(clazz, dir, "invoices.xml", element);

        List<ZipFile> zips = new ArrayList<>();

        try {
            ZipFile zip;
            byte[] content;
            String pathToInvoices = dir+"\\invoices\\";
            String currentPath = "";

            for(INVOICE inv : invoices) {
                currentPath = pathToInvoices+inv.getID() + "\\";

                content = inv.getCONTENT().getValue();
                zip = ZipUtils.base64ToZip(content, currentPath, inv.getID()+".zip");
                ZipUtils.UnZipAllFiles(zip, currentPath);

                zips.add(zip);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void MarshallToXML(Class clazz, String path, String fileName, Object o) {
        File dir = new File(path);
        dir.mkdirs();

        try {
            marshaller(clazz).marshal(o, new FileOutputStream(dir.getPath()+ "\\" + fileName));
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public SendInvoiceResponse sendInvoice(SendInvoiceRequest sendInvoiceRequest) {
        JAXBElement<SendInvoiceResponse> respObj = (JAXBElement<SendInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendInvoiceRequest(sendInvoiceRequest));

        return respObj.getValue();
    }

    public GetInvoiceResponse getInvoice(GetInvoiceRequest getInvoiceRequest) throws JAXBException, FileNotFoundException {
        JAXBElement<GetInvoiceResponse> respObj = (JAXBElement<GetInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetInvoiceRequest(getInvoiceRequest));

        String dir = PATH_TO_DOCUMENTS+"\\einvoice\\getInvoice\\";

        extractInvoices(GetInvoiceResponse.class, dir, respObj, respObj.getValue().getINVOICE());

        return respObj.getValue();
    }

    public GetInvoiceWithTypeResponse getInvoiceWithType(GetInvoiceWithTypeRequest request) {
        JAXBElement<GetInvoiceWithTypeResponse> respObj = (JAXBElement<GetInvoiceWithTypeResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetInvoiceWithTypeRequest(request));

        String dir = PATH_TO_DOCUMENTS+"\\einvoice\\getInvoiceWithType\\";

        extractInvoices(GetInvoiceWithTypeResponse.class ,dir,respObj, respObj.getValue().getINVOICE());

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
