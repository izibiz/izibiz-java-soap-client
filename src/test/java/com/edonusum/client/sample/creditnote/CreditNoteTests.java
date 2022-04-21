package com.edonusum.client.sample.creditnote;

import com.edonusum.client.adapter.CreditNoteAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.ubl.CreditNoteUBL;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.crnote.*;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.ObjectFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXB;
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
class CreditNoteTests {

    @Autowired
    private CreditNoteAdapter adapter;

    private static String sendCreditNoteUUID = "";

    private static List<CREDITNOTE> creditnotes;

    private static String SESSION_ID;


    @Test
    @Order(1)
    @DisplayName("Giriş yapma")
    void login() {
        SESSION_ID = AuthTests.login();

        Assertions.assertNotEquals("", SESSION_ID);
    }

    @Test
    @Order(2)
    @DisplayName("E-Müstahsil okuma")
    void canGetCreditNoteList() throws Exception { // getCreditNote
        GetCreditNoteRequest request = new GetCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        /* Tarihe göre sorgulama */
        GetCreditNoteRequest.CREDITNOTESEARCHKEY searchKey = new GetCreditNoteRequest.CREDITNOTESEARCHKEY();
        searchKey.setSTARTDATE(DateUtils.minusDays(30));
        searchKey.setENDDATE(DateUtils.now());
        searchKey.setLIMIT(100);

        /* Query with ID */
        // searchKey.setUUID("");

        /* Okunmuş belgelerin alınması */
        searchKey.setREADINCLUDED(FLAGVALUE.Y);

        request.setCONTENTTYPE(CONTENTTYPE.XML);

        request.setCREDITNOTESEARCHKEY(searchKey);
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
    void canLoadCreditNote() throws IOException { // loadCreditNote
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

        CREDITNOTE creditNote = new CREDITNOTE();
        CREDITNOTE.HEADER crheader = new CREDITNOTE.HEADER();

        creditNote.setHEADER(crheader);

        // id
        String id = IdentifierUtils.createInvoiceIdRandom("X01");
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-creditNote.xml");
        File created = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(created.toPath()));

        creditNote.setCONTENT(base64Binary);

        request.getCREDITNOTE().add(creditNote);

        created.delete();

        LoadCreditNoteResponse resp = adapter.loadCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(4)
    @DisplayName("E-Müstahsil gönderme")
    void canSendCreditNote() throws Exception { // sendCreditNote
        SendCreditNoteRequest request = new SendCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N"); // "Y" if sending zipped file
        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        CREDITNOTE creditnote = new CREDITNOTE();
        CREDITNOTE.HEADER crheader = new CREDITNOTE.HEADER();
        creditnote.setHEADER(crheader);

        //File draft = new File("xml\\draft-creditNote.xml");
        //File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        ObjectFactory o = new ObjectFactory();
        CreditNoteUBL creditNote = new CreditNoteUBL();

        File file = new File(System.getProperty("user.home")+"\\Desktop\\x.xml");

        JAXB.marshal(o.createCreditNote(creditNote.getCreditNote()), file);

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(file.toPath()));

        creditnote.setCONTENT(base64Binary);

        CREDITNOTEPROPERTIES props = new CREDITNOTEPROPERTIES();
        props.setSENDINGTYPE(SENDINGTYPE.KAGIT);

        request.setCREDITNOTEPROPERTIES(props);
        request.getCREDITNOTE().add(creditnote);

        SendCreditNoteResponse resp = adapter.sendCreditNote(request);

        //createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendCreditNoteUUID = creditNote.getCreditNote().getUUID().getValue();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(5)
    @DisplayName("E-Müstahsil durum sorgulama")
    void canGetCreditNoteStatus() { // getCreditNoteStatus
        GetCreditNoteStatusRequest request = new GetCreditNoteStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(creditnotes.stream().map(CREDITNOTE::getUUID).collect(Collectors.toList())); // toplu status sorgulama

        GetCreditNoteStatusResponse resp = adapter.getCreditNoteStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getCREDITNOTESTATUS().get(0).getHEADER().getSTATUS());
    }

    @Test
    @Order(6)
    @DisplayName("E-Müstahsil iptal etme")
    void canCancelCreditNote() { // cancelCreditNote
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
    void canGetCreditNoteReport() throws Exception { // getCreditNoteReport
        GetCreditNoteReportRequest request = new GetCreditNoteReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setSTARTDATE(DateUtils.minusDays(30));
        request.setENDDATE(DateUtils.now());

        request.setHEADERONLY(FLAGVALUE.N);

        GetCreditNoteReportResponse resp = adapter.getCreditNoteReport(request);

        Assertions.assertNull(resp.getERRORTYPE());
        Assertions.assertNotEquals(0, resp.getCREDITNOTEREPORT().size());

        System.out.println(resp.getCREDITNOTEREPORT().get(0).getHEADER().getSTATUS());
    }

    @Test
    @Order(8)
    @DisplayName("E-Müstahsil işaretleme")
    void canMarkCreditNote() { // markCreditNote
        MarkCreditNoteRequest request = new MarkCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        MarkCreditNoteRequest.MARK mark = new MarkCreditNoteRequest.MARK();
        mark.setValue("READ");
        mark.getUUID().addAll(creditnotes.stream().map(CREDITNOTE::getUUID).collect(Collectors.toList())); // toplu creditNote işaretleme

        request.setMARK(mark);

        MarkCreditNoteResponse resp = adapter.markCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(9)
    @DisplayName("Çıkış yapma")
    void logout() {
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

}
