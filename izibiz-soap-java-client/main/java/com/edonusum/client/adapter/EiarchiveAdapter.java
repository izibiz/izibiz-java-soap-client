package com.edonusum.client.adapter;

import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.eiarchive.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipFile;

@Component
public class EiarchiveAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/EIArchiveWS/EFaturaArchive";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.eiarchive";
    private ObjectFactory of;

    public EiarchiveAdapter() {
        of = new ObjectFactory();
        setContextPath(CONTEXT_PATH);
    }

    public ArchiveInvoiceReadResponse readFromArchive(ArchiveInvoiceReadRequest request) {
        JAXBElement<ArchiveInvoiceReadResponse> respObj = (JAXBElement<ArchiveInvoiceReadResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createArchiveInvoiceReadRequest(request));

        return respObj.getValue();
    }

    public GetEArchiveInvoiceStatusResponse getEArchiveStatus(GetEArchiveInvoiceStatusRequest request) {
        JAXBElement<GetEArchiveInvoiceStatusResponse> respObj = (JAXBElement<GetEArchiveInvoiceStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createGetEArchiveInvoiceStatusRequest(request));

        return respObj.getValue();
    }

    public CancelEArchiveInvoiceResponse cancelEArchiveInvoice(CancelEArchiveInvoiceRequest request) {
        JAXBElement<CancelEArchiveInvoiceResponse> respObj = (JAXBElement<CancelEArchiveInvoiceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createCancelEArchiveInvoiceRequest(request));

        return respObj.getValue();
    }

    public GetEmailEarchiveInvoiceResponse getEmailEarchiveInvoice(GetEmailEarchiveInvoiceRequest request) {
        JAXBElement<GetEmailEarchiveInvoiceResponse> respObj = (JAXBElement<GetEmailEarchiveInvoiceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createGetEmailEarchiveInvoiceRequest(request));

        return respObj.getValue();
    }

    public GetEArchiveReportResponse getEarchiveReport(GetEArchiveReportRequest request) {
        JAXBElement<GetEArchiveReportResponse> respObj = (JAXBElement<GetEArchiveReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createGetEArchiveReportRequest(request));

        return respObj.getValue();
    }

    private static int index = 0;
    public ReadEArchiveReportResponse readEarchiveReport(ReadEArchiveReportRequest request) throws IOException {
        JAXBElement<ReadEArchiveReportResponse> respObj = (JAXBElement<ReadEArchiveReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createReadEArchiveReportRequest(request));

        String path = PATH_TO_DOCUMENTS + "\\eiarchive\\readEarchiveReport\\";

        File file;

        String currentPath = "";
        for(Base64Binary binary : respObj.getValue().getEARCHIVEREPORT()) {
            currentPath = path + "report"+index+"\\";
            file = new File(currentPath);
            file.mkdirs();

            Files.write(Paths.get(currentPath+"\\report.zip"), binary.getValue());

            ZipFile zf = ZipUtils.base64ToZip(binary.getValue(),currentPath,"report.zip");

            index++;
        }

        return respObj.getValue();
    }

    public ArchiveInvoiceExtendedResponse writeToArchiveExtended(ArchiveInvoiceExtendedRequest request) {
        JAXBElement<ArchiveInvoiceExtendedResponse> respObj = (JAXBElement<ArchiveInvoiceExtendedResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createArchiveInvoiceExtendedRequest(request));

        return respObj.getValue();
    }

}
