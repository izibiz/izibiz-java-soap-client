package com.edonusum.client.adapter;

import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.crnote.*;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipFile;

public class CreditNoteAdapter extends Adapter{
    private static final String URL = "https://efaturatest.izibiz.com.tr:443/CreditNoteWS/CreditNote";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.crnote";
    private ObjectFactory of;

    public CreditNoteAdapter() {
        setContextPath(CONTEXT_PATH);
        of = new ObjectFactory();
    }

    public GetCreditNoteResponse getCreditNote(GetCreditNoteRequest request) {
        JAXBElement<GetCreditNoteResponse> respObj = (JAXBElement<GetCreditNoteResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetCreditNoteRequest(request));

        try {
            String path = PATH_TO_DOCUMENTS + "\\creditnote\\getCreditNote\\";
            String currentPath;
            File dir;
            ZipFile zf;
            for (CREDITNOTE cr : respObj.getValue().getCREDITNOTE()) {
                currentPath = path + cr.getID();

                dir = new File(currentPath);
                dir.mkdirs();

                zf = ZipUtils.base64ToZip(cr.getCONTENT().getValue(), currentPath, cr.getID()+".zip");
                ZipUtils.UnZipAllFiles(zf, currentPath);
            }
        } catch(Exception e) {
            e.printStackTrace();
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

    public GetCreditNoteReportResponse getCreditNoteReport(GetCreditNoteReportRequest request) {
        JAXBElement<GetCreditNoteReportResponse> respObj = (JAXBElement<GetCreditNoteReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL, of.createGetCreditNoteReportRequest(request));

        String path = PATH_TO_DOCUMENTS + "\\creditnote\\getCreditNoteReport";
        String currentPath;
        int index = 0;
        File dir, temp;
        for(REPORT report : respObj.getValue().getCREDITNOTEREPORT()) {
            currentPath = path + "\\" + "report"+index;
            dir = new File(currentPath);
            dir.mkdirs();

            try {
                temp = new File(dir, "report.xml");
                temp.createNewFile();
                dir.createNewFile();
                Files.write(temp.toPath(), report.getCONTENT().getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
            index++;
        }

        return respObj.getValue();
    }
}
