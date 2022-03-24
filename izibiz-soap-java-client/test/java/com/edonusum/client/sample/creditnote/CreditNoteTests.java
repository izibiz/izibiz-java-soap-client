package com.edonusum.client.sample.creditnote;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.adapter.CreditNoteAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.crnote.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

@SpringBootTest
public class CreditNoteTests {
    private AuthAdapter authAdapter = new AuthAdapter();
    private CreditNoteAdapter creditNoteAdapter = new CreditNoteAdapter();

    private String getSessionId() {
        return authAdapter.login(AuthTests.prepareLoginRequest()).getSESSIONID();
    }

    @Test
    public void getCreditNote_canGetCreditNoteList_withGivenParameters() throws DatatypeConfigurationException { // getCreditNote
        GetCreditNoteRequest request = new GetCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        XMLGregorianCalendar end = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        XMLGregorianCalendar start = (XMLGregorianCalendar) end.clone();
        start.setMonth(start.getMonth()-1); // take last 30 days

        /* Query with period */
        GetCreditNoteRequest.CREDITNOTESEARCHKEY key = new GetCreditNoteRequest.CREDITNOTESEARCHKEY();
        key.setSTARTDATE(start);
        key.setENDDATE(end);

        /* Query with ID */
        // key.setUUID("");

        request.setCONTENTTYPE(CONTENTTYPE.PDF);

        request.setCREDITNOTESEARCHKEY(key);
        request.setHEADERONLY(FLAGVALUE.N);

        GetCreditNoteResponse resp = creditNoteAdapter.getCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getCREDITNOTE().get(0).getID());
    }

    @Test
    public void getCreditNoteStatus_canGetCreditNoteStatus_whenUUID_isGiven() { // getCreditNoteStatus
        GetCreditNoteStatusRequest request = new GetCreditNoteStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        request.getUUID().add("dc78fb96-6427-473b-a405-16babb6dd6ea");

        GetCreditNoteStatusResponse resp = creditNoteAdapter.getCreditNoteStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getCREDITNOTESTATUS().get(0).getHEADER().getSTATUS());
    }

    @Test
    public void sendCreditNote_canSendCreditNote_whenValidContentIsGiven() throws IOException { // sendCreditNote
        SendCreditNoteRequest request = new SendCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N"); // "Y" if sending zipped file
        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        CREDITNOTE cr = new CREDITNOTE();
        CREDITNOTE.HEADER crheader = new CREDITNOTE.HEADER();
        cr.setHEADER(crheader);

        File draft = new File("xml\\draft-creditNote.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, UUID.randomUUID(), IdentifierUtils.createInvoiceIdRandom("DMY"));

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(createdXml.toPath()));

        cr.setCONTENT(b64);

        CREDITNOTEPROPERTIES props = new CREDITNOTEPROPERTIES();
        props.setSENDINGTYPE(SENDINGTYPE.KAGIT);

        request.setCREDITNOTEPROPERTIES(props);
        request.getCREDITNOTE().add(cr);

        SendCreditNoteResponse resp = creditNoteAdapter.sendCreditNote(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void cancelCreditNote_canCancelCreditNote_whenGivenValidUUID() { // cancelCreditNote
        CancelCreditNoteRequest request = new CancelCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        String uuid = "381f3967-c6e0-4840-b9d5-643f5633038b"; // example id

        request.getUUID().add(uuid);

        CancelCreditNoteResponse resp = creditNoteAdapter.cancelCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void loadCreditNote_canLoadDraftCreditNote_whenGivenValidContent() throws IOException { // loadCreditNote
        LoadCreditNoteRequest request = new LoadCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N"); // "Y" if sending zipped file
        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        CREDITNOTEPROPERTIES props = new CREDITNOTEPROPERTIES();
        props.setSENDINGTYPE(SENDINGTYPE.KAGIT);
        props.setEMAILFLAG(FLAGVALUE.N);
        props.setSMSFLAG(FLAGVALUE.N);

        request.setCREDITNOTEPROPERTIES(props);

        CREDITNOTE cr = new CREDITNOTE();
        CREDITNOTE.HEADER crheader = new CREDITNOTE.HEADER();

        cr.setHEADER(crheader);

        File draft = new File("xml\\draft-creditNote.xml");
        File created = XMLUtils.createXmlFromDraftInvoice(draft, UUID.randomUUID(), IdentifierUtils.createInvoiceIdRandom("X01"));

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(created.toPath()));

        cr.setCONTENT(b64);

        request.getCREDITNOTE().add(cr);

        created.delete();

        LoadCreditNoteResponse resp = creditNoteAdapter.loadCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void markCreditNote_canMarkCreditNote_withGivenUUID() { // markCreditNote
        MarkCreditNoteRequest request = new MarkCreditNoteRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        MarkCreditNoteRequest.MARK mark = new MarkCreditNoteRequest.MARK();
        mark.setValue("READ");
        mark.getUUID().add("25a01b42-de89-40d4-95cc-e12dd2af0d1c"); // Query with ID

        request.setMARK(mark);

        MarkCreditNoteResponse resp = creditNoteAdapter.markCreditNote(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void getCreditNoteReport_givenValidUUID_then_canGetReport() throws DatatypeConfigurationException { // getCreditNoteReport
        GetCreditNoteReportRequest request = new GetCreditNoteReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        XMLGregorianCalendar end = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        XMLGregorianCalendar start = (XMLGregorianCalendar) end.clone();
        start.setMonth(start.getMonth()-1); // take last 30 days

        request.setSTARTDATE(start);
        request.setENDDATE(end);

        request.setHEADERONLY(FLAGVALUE.N);

        GetCreditNoteReportResponse resp = creditNoteAdapter.getCreditNoteReport(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getCREDITNOTEREPORT().get(0).getHEADER().getSTATUS());
    }
}
