package com.edonusum.client.sample.creditnote;

import com.edonusum.client.SoapJavaClientApplication;
import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.adapter.CreditNoteAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.crnote.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
public class CreditNoteTests {
    private static SoapJavaClientApplication client = new SoapJavaClientApplication();

    private String loadCreditNoteUUID = "";
    private String sendCreditNoteUUID = "";

    private List<CREDITNOTE> creditnotes;

    private static String SESSION_ID;

    @Test
    public void runAllTests() throws Exception{
        // login
        login();

        // getCreditNote
        getCreditNote_canGetCreditNoteList_withGivenParameters();

        // loadCreditNote
        loadCreditNote_canLoadDraftCreditNote_whenGivenValidContent();

        // sendCreditNote
        sendCreditNote_canSendCreditNote_whenValidContentIsGiven();

        // getCreditNoteStatus
        getCreditNoteStatus_canGetCreditNoteStatus_whenUUID_isGiven();

        // cancelCreditNote
        cancelCreditNote_canCancelCreditNote_whenGivenValidUUID();

        // getCreditNoteReport
        getCreditNoteReport_givenValidUUID_then_canGetReport();

        // markCreditNote
        markCreditNote_canMarkCreditNote_withGivenUUID();

        // logout
        logout();
    }
    
    private void login() {
        SESSION_ID = AuthTests.login();
    }
    
    private void logout() {
        AuthTests.logout(SESSION_ID);
        
        SESSION_ID = "";
    }

    private void getCreditNote_canGetCreditNoteList_withGivenParameters() throws DatatypeConfigurationException { // getCreditNote
        GetCreditNoteRequest request = new GetCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        /* Tarihe göre sorgulama */
        GetCreditNoteRequest.CREDITNOTESEARCHKEY key = new GetCreditNoteRequest.CREDITNOTESEARCHKEY();
        key.setSTARTDATE(DateUtils.minusDays(30));
        key.setENDDATE(DateUtils.now());

        /* Query with ID */
        // key.setUUID("");

        /* Okunmuş belgelerin alınması */
        key.setREADINCLUDED(FLAGVALUE.Y);

        request.setCONTENTTYPE(CONTENTTYPE.PDF);

        request.setCREDITNOTESEARCHKEY(key);
        request.setHEADERONLY(FLAGVALUE.N);

        GetCreditNoteResponse resp = client.creditNoteWS().getCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        creditnotes = resp.getCREDITNOTE();

        System.out.println(resp.getCREDITNOTE().get(0).getID());
    }

    private void getCreditNoteStatus_canGetCreditNoteStatus_whenUUID_isGiven() { // getCreditNoteStatus
        GetCreditNoteStatusRequest request = new GetCreditNoteStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(creditnotes.stream().map(c -> c.getUUID()).collect(Collectors.toList())); // toplu status sorgulama

        GetCreditNoteStatusResponse resp = client.creditNoteWS().getCreditNoteStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getCREDITNOTESTATUS().get(0).getHEADER().getSTATUS());
    }

    private void sendCreditNote_canSendCreditNote_whenValidContentIsGiven() throws IOException { // sendCreditNote
        SendCreditNoteRequest request = new SendCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N"); // "Y" if sending zipped file
        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        CREDITNOTE cr = new CREDITNOTE();
        CREDITNOTE.HEADER crheader = new CREDITNOTE.HEADER();
        cr.setHEADER(crheader);

        // id
        UUID uuid = UUID.randomUUID();
        String id = IdentifierUtils.createInvoiceIdRandom("DMY");

        File draft = new File("xml\\draft-creditNote.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(createdXml.toPath()));

        cr.setCONTENT(b64);

        CREDITNOTEPROPERTIES props = new CREDITNOTEPROPERTIES();
        props.setSENDINGTYPE(SENDINGTYPE.KAGIT);

        request.setCREDITNOTEPROPERTIES(props);
        request.getCREDITNOTE().add(cr);

        SendCreditNoteResponse resp = client.creditNoteWS().sendCreditNote(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendCreditNoteUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    private void cancelCreditNote_canCancelCreditNote_whenGivenValidUUID() { // cancelCreditNote
        CancelCreditNoteRequest request = new CancelCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        String uuid = sendCreditNoteUUID; // son gönderilen creditNote

        request.getUUID().add(uuid);

        CancelCreditNoteResponse resp = client.creditNoteWS().cancelCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    private void loadCreditNote_canLoadDraftCreditNote_whenGivenValidContent() throws IOException { // loadCreditNote
        LoadCreditNoteRequest request = new LoadCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N"); // "Y" if sending zipped file
        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        CREDITNOTEPROPERTIES props = new CREDITNOTEPROPERTIES();
        props.setSENDINGTYPE(SENDINGTYPE.KAGIT);
        props.setEMAILFLAG(FLAGVALUE.N);
        props.setSMSFLAG(FLAGVALUE.N);

        request.setCREDITNOTEPROPERTIES(props);

        CREDITNOTE cr = new CREDITNOTE();
        CREDITNOTE.HEADER crheader = new CREDITNOTE.HEADER();

        cr.setHEADER(crheader);

        // id
        String id = IdentifierUtils.createInvoiceIdRandom("X01");
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-creditNote.xml");
        File created = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(created.toPath()));

        cr.setCONTENT(b64);

        request.getCREDITNOTE().add(cr);

        created.delete();

        LoadCreditNoteResponse resp = client.creditNoteWS().loadCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        loadCreditNoteUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    private void markCreditNote_canMarkCreditNote_withGivenUUID() { // markCreditNote
        MarkCreditNoteRequest request = new MarkCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        MarkCreditNoteRequest.MARK mark = new MarkCreditNoteRequest.MARK();
        mark.setValue("READ");
        mark.getUUID().addAll(creditnotes.stream().map(c -> c.getUUID()).collect(Collectors.toList())); // toplu creditNote işaretleme

        request.setMARK(mark);

        MarkCreditNoteResponse resp = client.creditNoteWS().markCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    private void getCreditNoteReport_givenValidUUID_then_canGetReport() throws DatatypeConfigurationException { // getCreditNoteReport
        GetCreditNoteReportRequest request = new GetCreditNoteReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setSTARTDATE(DateUtils.minusDays(30));
        request.setENDDATE(DateUtils.now());

        request.setHEADERONLY(FLAGVALUE.N);

        GetCreditNoteReportResponse resp = client.creditNoteWS().getCreditNoteReport(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getCREDITNOTEREPORT().get(0).getHEADER().getSTATUS());
    }

}
