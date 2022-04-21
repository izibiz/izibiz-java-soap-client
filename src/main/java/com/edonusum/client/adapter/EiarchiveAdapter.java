package com.edonusum.client.adapter;

import com.edonusum.client.util.FileUtils;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.eiarchive.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EiarchiveAdapter extends Adapter{
    private static final String URL_ENDPOINT = URL + "/EFaturaArchive";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.eiarchive";
    private static final String DOCUMENT_DIR = PATH_TO_DOCUMENTS + "\\eiarchive";
    private final ObjectFactory of;

    public EiarchiveAdapter() {
        of = new ObjectFactory();
        setContextPath(CONTEXT_PATH);
    }

    private boolean isCompressed(REQUESTHEADERType header) {
        return ("Y".equals(header.getCOMPRESSED()) || null == header.getCOMPRESSED());
    }

    public ArchiveInvoiceReadResponse readFromArchive(ArchiveInvoiceReadRequest request) throws Exception{
        JAXBElement<ArchiveInvoiceReadResponse> respObj = (JAXBElement<ArchiveInvoiceReadResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT,of.createArchiveInvoiceReadRequest(request));

        String dir = DOCUMENT_DIR + "\\readFromArchive";

        String extension = ("Y".equals(request.getREQUESTHEADER().getCOMPRESSED()) || null == request.getREQUESTHEADER().getCOMPRESSED()) ? "zip" : request.getPROFILE().toLowerCase();

        List<File> files = FileUtils.writeToFile(respObj.getValue().getINVOICE().stream().map(Base64Binary::getValue).collect(Collectors.toList()), dir,"invoice", extension);

        if("zip".equals(extension)) {
            files = ZipUtils.unzipMultiple(files); // extracted files
        }

        if(files.size() != 0 && request.getPROFILE().equalsIgnoreCase("xml")) {
            List<InvoiceType> invoiceList = new ArrayList<>();
            for(File xml : files) {
                invoiceList.add(JAXB.unmarshal(xml, InvoiceType.class));
            }

            // TODO: do some business with invoices
        }

        return respObj.getValue();
    }

    public GetEArchiveInvoiceStatusResponse getEArchiveStatus(GetEArchiveInvoiceStatusRequest request) {
        JAXBElement<GetEArchiveInvoiceStatusResponse> respObj = (JAXBElement<GetEArchiveInvoiceStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT,of.createGetEArchiveInvoiceStatusRequest(request));

        return respObj.getValue();
    }

    public CancelEArchiveInvoiceResponse cancelEArchiveInvoice(CancelEArchiveInvoiceRequest request) {
        JAXBElement<CancelEArchiveInvoiceResponse> respObj = (JAXBElement<CancelEArchiveInvoiceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT,of.createCancelEArchiveInvoiceRequest(request));

        return respObj.getValue();
    }

    public GetEmailEarchiveInvoiceResponse getEmailEarchiveInvoice(GetEmailEarchiveInvoiceRequest request) {
        JAXBElement<GetEmailEarchiveInvoiceResponse> respObj = (JAXBElement<GetEmailEarchiveInvoiceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT,of.createGetEmailEarchiveInvoiceRequest(request));

        return respObj.getValue();
    }

    public GetEArchiveReportResponse getEarchiveReport(GetEArchiveReportRequest request) {
        JAXBElement<GetEArchiveReportResponse> respObj = (JAXBElement<GetEArchiveReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT,of.createGetEArchiveReportRequest(request));

        return respObj.getValue();
    }

    public ReadEArchiveReportResponse readEarchiveReport(ReadEArchiveReportRequest request) throws Exception {
        JAXBElement<ReadEArchiveReportResponse> respObj = (JAXBElement<ReadEArchiveReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT,of.createReadEArchiveReportRequest(request));

        String path = DOCUMENT_DIR + "\\readEarchiveReport\\";

        // reportlar 2 kez ziplenerek gönderildiği için (bug) her zaman zip kullanılmalı
        String ext = isCompressed(request.getREQUESTHEADER()) ? "zip" : "xml";

        List<byte[]> contents = respObj.getValue().getEARCHIVEREPORT().stream().map(Base64Binary::getValue).collect(Collectors.toList());
        List<File> files = FileUtils.writeToFile(contents, path,"eiarchive_report", ext);

        // dosyalar ile devam edilir

        return respObj.getValue();
    }

    public ArchiveInvoiceExtendedResponse writeToArchiveExtended(ArchiveInvoiceExtendedRequest request) {
        JAXBElement<ArchiveInvoiceExtendedResponse> respObj = (JAXBElement<ArchiveInvoiceExtendedResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT,of.createArchiveInvoiceExtendedRequest(request));

        return respObj.getValue();
    }

}
