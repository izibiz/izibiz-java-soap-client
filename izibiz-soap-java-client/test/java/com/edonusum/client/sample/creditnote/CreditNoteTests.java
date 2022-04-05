package com.edonusum.client.sample.creditnote;

import com.edonusum.client.adapter.CreditNoteAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.crnote.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
@DisplayName("E-Müstahsil servisi")
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreditNoteTests {

    @Autowired
    private CreditNoteAdapter adapter;

    private static String loadCreditNoteUUID = "";
    private static String sendCreditNoteUUID = "";

    private static List<CREDITNOTE> creditnotes;

    private static String SESSION_ID;


    @Test
    @Order(1)
    @DisplayName("Giriş yapma")
    public void login() {
        SESSION_ID = AuthTests.login();
    }

    @Test
    @Order(2)
    @DisplayName("E-Müstahsil okuma")
    public void getCreditNote_canGetCreditNoteList_withGivenParameters() throws Exception { // getCreditNote
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

        header.setSESSIONID(AuthTests.login());
        GetCreditNoteResponse resp = adapter.getCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        creditnotes = resp.getCREDITNOTE();

        System.out.println(resp.getCREDITNOTE().get(0).getID());
    }

    @Test
    @Order(3)
    @DisplayName("Taslak e-müstahsil yükleme")
    public void loadCreditNote_canLoadDraftCreditNote_whenGivenValidContent() throws IOException { // loadCreditNote
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

        LoadCreditNoteResponse resp = adapter.loadCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        loadCreditNoteUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(4)
    @DisplayName("E-Müstahsil gönderme")
    public void sendCreditNote_canSendCreditNote_whenValidContentIsGiven() throws IOException { // sendCreditNote
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

        SendCreditNoteResponse resp = adapter.sendCreditNote(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendCreditNoteUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(5)
    @DisplayName("E-Müstahsil durum sorgulama")
    public void getCreditNoteStatus_canGetCreditNoteStatus_whenUUID_isGiven() { // getCreditNoteStatus
        GetCreditNoteStatusRequest request = new GetCreditNoteStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(creditnotes.stream().map(c -> c.getUUID()).collect(Collectors.toList())); // toplu status sorgulama

        GetCreditNoteStatusResponse resp = adapter.getCreditNoteStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getCREDITNOTESTATUS().get(0).getHEADER().getSTATUS());
    }

    @Test
    @Order(6)
    @DisplayName("E-Müstahsil iptal etme")
    public void cancelCreditNote_canCancelCreditNote_whenGivenValidUUID() { // cancelCreditNote
        CancelCreditNoteRequest request = new CancelCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        String uuid = sendCreditNoteUUID; // son gönderilen creditNote

        request.getUUID().add(uuid);

        CancelCreditNoteResponse resp = adapter.cancelCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(7)
    @DisplayName("E-Müstahsil raporu çekme")
    public void getCreditNoteReport_givenValidUUID_then_canGetReport() throws Exception { // getCreditNoteReport
        GetCreditNoteReportRequest request = new GetCreditNoteReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setSTARTDATE(DateUtils.minusDays(30));
        request.setENDDATE(DateUtils.now());

        request.setHEADERONLY(FLAGVALUE.N);

        GetCreditNoteReportResponse resp = adapter.getCreditNoteReport(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getCREDITNOTEREPORT().get(0).getHEADER().getSTATUS());
    }

    @Test
    @Order(8)
    @DisplayName("E-Müstahsil işaretleme")
    public void markCreditNote_canMarkCreditNote_withGivenUUID() { // markCreditNote
        MarkCreditNoteRequest request = new MarkCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        MarkCreditNoteRequest.MARK mark = new MarkCreditNoteRequest.MARK();
        mark.setValue("READ");
        mark.getUUID().addAll(creditnotes.stream().map(c -> c.getUUID()).collect(Collectors.toList())); // toplu creditNote işaretleme

        request.setMARK(mark);

        MarkCreditNoteResponse resp = adapter.markCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(9)
    @DisplayName("Çıkış yapma")
    public void logout() {
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

}
