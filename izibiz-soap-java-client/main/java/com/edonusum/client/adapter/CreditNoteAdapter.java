package com.edonusum.client.adapter;

import com.edonusum.client.util.FileUtils;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.crnote.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreditNoteAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/CreditNoteWS/CreditNote";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.crnote";
    private static final String DOCUMENTS_DIR = PATH_TO_DOCUMENTS + "\\creditnote";
    private ObjectFactory of;

    public CreditNoteAdapter() {
        setContextPath(CONTEXT_PATH);
        of = new ObjectFactory();
    }

    private boolean isCompressed(REQUESTHEADERType header) {
        return ("Y".equals(header.getCOMPRESSED()) || null == header.getCOMPRESSED()) ? true : false;
    }

    public GetCreditNoteResponse getCreditNote(GetCreditNoteRequest request) throws Exception{
        JAXBElement<GetCreditNoteResponse> respObj = (JAXBElement<GetCreditNoteResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetCreditNoteRequest(request));

        String path = DOCUMENTS_DIR + "\\getCreditNote\\";

        if(null == request.getCONTENTTYPE()) request.setCONTENTTYPE(CONTENTTYPE.XML);

        String ext = isCompressed(request.getREQUESTHEADER()) ? "zip" : request.getCONTENTTYPE().value();

        List<byte[]> contents = new ArrayList<>();
        contents.addAll(respObj.getValue().getCREDITNOTE().stream().map(cn -> cn.getCONTENT().getValue()).collect(Collectors.toList()));

        List<File> files = FileUtils.writeToFile(contents,path, "credit_note", ext);
        if(ext.equals("zip")) {
            ZipUtils.unzipMultiple(files);
        }

        return respObj.getValue();
    }

    public GetCreditNoteStatusResponse getCreditNoteStatus(GetCreditNoteStatusRequest request) {
        JAXBElement<GetCreditNoteStatusResponse> respObj = (JAXBElement<GetCreditNoteStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetCreditNoteStatusRequest(request));

        return respObj.getValue();
    }

    public SendCreditNoteResponse sendCreditNote(SendCreditNoteRequest request) {
        JAXBElement<SendCreditNoteResponse> respObj = (JAXBElement<SendCreditNoteResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createSendCreditNoteRequest(request));

        return respObj.getValue();
    }

    public CancelCreditNoteResponse cancelCreditNote(CancelCreditNoteRequest request) {
        JAXBElement<CancelCreditNoteResponse> respObj = (JAXBElement<CancelCreditNoteResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createCancelCreditNoteRequest(request));

        return respObj.getValue();
    }

    public LoadCreditNoteResponse loadCreditNote(LoadCreditNoteRequest request) {
        JAXBElement<LoadCreditNoteResponse> respObj = (JAXBElement<LoadCreditNoteResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createLoadCreditNoteRequest(request));

        return respObj.getValue();
    }

    public MarkCreditNoteResponse markCreditNote(MarkCreditNoteRequest request) {
        JAXBElement<MarkCreditNoteResponse> respObj = (JAXBElement<MarkCreditNoteResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createMarkCreditNoteRequest(request));

        return respObj.getValue();
    }

    public GetCreditNoteReportResponse getCreditNoteReport(GetCreditNoteReportRequest request) throws Exception{
        JAXBElement<GetCreditNoteReportResponse> respObj = (JAXBElement<GetCreditNoteReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetCreditNoteReportRequest(request));

        String path = DOCUMENTS_DIR + "\\getCreditNoteReport";

        String ext = isCompressed(request.getREQUESTHEADER()) ? "zip" : "xml";

        List<byte[]> contents = new ArrayList<>();
        contents.addAll(respObj.getValue().getCREDITNOTEREPORT().stream().map(report -> report.getCONTENT().getValue()).collect(Collectors.toList()));

        List<File> files = FileUtils.writeToFile(contents, path, "credit_note_report",ext);

        if("zip".equals(ext)) ZipUtils.unzipMultiple(files);

        return respObj.getValue();
    }
}
