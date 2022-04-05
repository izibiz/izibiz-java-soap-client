package com.edonusum.client.adapter;

import com.edonusum.client.util.FileUtils;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.eiarchive.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

@Component
public class EiarchiveAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/EIArchiveWS/EFaturaArchive";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.eiarchive";
    private static final String DOCUMENT_DIR = PATH_TO_DOCUMENTS + "\\eiarchive";
    private ObjectFactory of;

    public EiarchiveAdapter() {
        of = new ObjectFactory();
        setContextPath(CONTEXT_PATH);
    }

    private boolean isCompressed(REQUESTHEADERType header) {
        return ("Y".equals(header.getCOMPRESSED()) || null == header.getCOMPRESSED()) ? true : false;
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
    public ReadEArchiveReportResponse readEarchiveReport(ReadEArchiveReportRequest request) throws Exception {
        JAXBElement<ReadEArchiveReportResponse> respObj = (JAXBElement<ReadEArchiveReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createReadEArchiveReportRequest(request));

        String path = DOCUMENT_DIR + "\\readEarchiveReport\\";

        // reportlar 2 kez ziplenerek gönderildiği için (bug) her zaman zip kullanılmalı
        String ext = isCompressed(request.getREQUESTHEADER()) ? "zip" : "xml";

        List<byte[]> contents = respObj.getValue().getEARCHIVEREPORT().stream().map(report -> report.getValue()).collect(Collectors.toList());
        List<File> files = FileUtils.writeToFile(contents, path,"eiarchive_report", ext);

        // continue with files

        return respObj.getValue();
    }

    public ArchiveInvoiceExtendedResponse writeToArchiveExtended(ArchiveInvoiceExtendedRequest request) {
        JAXBElement<ArchiveInvoiceExtendedResponse> respObj = (JAXBElement<ArchiveInvoiceExtendedResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL,of.createArchiveInvoiceExtendedRequest(request));

        return respObj.getValue();
    }

}
