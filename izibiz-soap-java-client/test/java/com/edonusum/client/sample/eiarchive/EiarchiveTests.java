package com.edonusum.client.sample.eiarchive;

import com.edonusum.client.SoapJavaClientApplication;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.eiarchive.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class EiarchiveTests {
    private static SoapJavaClientApplication client = new SoapJavaClientApplication();

    private String loadEiarchiveUUID = "";
    private String sendEiarchiveUUID = "";

    private List<EARCHIVEINV> invoices;
    private List<REPORT> reports;

    private static String SESSION_ID;


    @Test
    public void runAllTests() throws Exception{
        // login
        login();

        // writeToEiarchiveExtended (send or load eiarchive)
        writeToEiarchiveExtended_givenValidEiarchive_then_writesToArchive();

        // readFromArchive
        readFromArchive_givenInvoiceId_then_returnsArchiveInvoice();

        // getEarchiveInvoice
        getEmailEarchiveInvoice_givenInvoiceUUID_andValidEmail_then_sendsInvoiceToEmail();

        // getEmailEarchiveInvoice
        getEmailEarchiveInvoice_givenInvoiceUUID_andValidEmail_then_sendsInvoiceToEmail();

        // getEarchiveStatus
        getEarchiveStatus_givenInvoiceId_then_returnsEArchiveInvoiceStatus();

        // cancelEarchiveInvoice
        cancelEarchiveInvoiceResponse_givenInvoiceUUID_then_cancelsInvoice();

        // cancelEarchiveInvoice (with delete flag)
        cancelEarchiveInvoiceResponse_givenInvoiceUUID_andGivenDeleteFlag_then_cancelsInvoice();

        // getEarchiveReport
        getEarchiveReport_givenPeriod_andGivenFlagValues_then_returnsReportList();

        // readArchiveReport
        readArchiveReport_givenReportId_thenReturnsReportContent();

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

    private void readFromArchive_givenInvoiceId_then_returnsArchiveInvoice() { // readFromArchive
        ArchiveInvoiceReadRequest request = new ArchiveInvoiceReadRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");
        header.setSESSIONID(SESSION_ID);

        request.setINVOICEID(sendEiarchiveUUID); // son gönderilen fatura
        request.setPORTALDIRECTION("OUT");
        request.setPROFILE("XML");

        request.setREQUESTHEADER(header);

        ArchiveInvoiceReadResponse resp = client.eiarchiveWS().readFromArchive(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getINVOICE().get(0));
    }

    private void getEarchiveStatus_givenInvoiceId_then_returnsEArchiveInvoiceStatus() { // getEArchiveStatus
        GetEArchiveInvoiceStatusRequest request = new GetEArchiveInvoiceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().add(sendEiarchiveUUID); // toplu status sorgulama

        GetEArchiveInvoiceStatusResponse resp = client.eiarchiveWS().getEArchiveStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getINVOICE().get(0).getHEADER().getSTATUSDESC());
    }

    private void cancelEarchiveInvoiceResponse_givenInvoiceUUID_then_cancelsInvoice() { // cancelEArchiveInvoiceResponse
        CancelEArchiveInvoiceRequest request = new CancelEArchiveInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);

        request.setREQUESTHEADER(header);

        CancelEArchiveInvoiceRequest.CancelEArsivInvoiceContent content = new CancelEArchiveInvoiceRequest.CancelEArsivInvoiceContent();
        content.setFATURAUUID(sendEiarchiveUUID);

        request.getCancelEArsivInvoiceContent().add(content);

        CancelEArchiveInvoiceResponse resp = client.eiarchiveWS().cancelEArchiveInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getINTLTXNID());
    }

    private void cancelEarchiveInvoiceResponse_givenInvoiceUUID_andGivenDeleteFlag_then_cancelsInvoice() { // cancelEArchiveInvoice
        CancelEArchiveInvoiceRequest request = new CancelEArchiveInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);

        request.setREQUESTHEADER(header);

        CancelEArchiveInvoiceRequest.CancelEArsivInvoiceContent content = new CancelEArchiveInvoiceRequest.CancelEArsivInvoiceContent();
        content.setFATURAUUID(sendEiarchiveUUID);
        /* GiB'e hiçbir zaman raporlanmamalı ise: */
        content.setDELETEFLAG("Y");

        /* GiB'e iptal olarak raporlanmalı ise:
        * content.setDELETEFLAG("N");
        */

        /* E_Arşiv platformunda bulunmayan bir faturanın iptali için:
        dökümantasyonda istenen parametreler doldurulduktan sonra
        content.setUPLOADFLAG("Y")
        */
        request.getCancelEArsivInvoiceContent().add(content);

        CancelEArchiveInvoiceResponse resp = client.eiarchiveWS().cancelEArchiveInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getINTLTXNID());
    }

    private void getEmailEarchiveInvoice_givenInvoiceUUID_andValidEmail_then_sendsInvoiceToEmail() { // getEmailEarchiveInvoice
        GetEmailEarchiveInvoiceRequest request = new GetEmailEarchiveInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setFATURAUUID(sendEiarchiveUUID); // example id
        request.setEMAIL("example@email.com"); // email address to send

        GetEmailEarchiveInvoiceResponse resp = client.eiarchiveWS().getEmailEarchiveInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    private void getEarchiveReport_givenPeriod_andGivenFlagValues_then_returnsReportList() { // getEarchiveReport
        GetEArchiveReportRequest request = new GetEArchiveReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        DecimalFormat format = new DecimalFormat("#00");

        // period text
        String period = LocalDate.now().getYear() + format.format(LocalDate.now().getMonthValue());  // 2018 mayıs = 201805 şeklinde formatlanmalı

        request.setREPORTPERIOD(period);

        request.setREPORTSTATUSFLAG("Y");

        GetEArchiveReportResponse resp = client.eiarchiveWS().getEarchiveReport(request);

        Assertions.assertNull(resp.getERRORTYPE());

        reports = resp.getREPORT();

        System.out.println(resp.getREPORT().get(0).getREPORTSTATUS());
    }

    private void readArchiveReport_givenReportId_thenReturnsReportContent() throws IOException { // ReadArchiveReport
        ReadEArchiveReportRequest request = new ReadEArchiveReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);

        request.setREQUESTHEADER(header);

        ReadEArchiveReportResponse response;

        for (REPORT rep : reports) {
            request.setRAPORNO(rep.getREPORTNO());
            response = client.eiarchiveWS().readEarchiveReport(request);

            Assertions.assertNull(response.getERRORTYPE());

            System.out.println(response.getREQUESTRETURN().getRETURNCODE());
        }
    }

    private void writeToEiarchiveExtended_givenValidEiarchive_then_writesToArchive() throws IOException { // writeToEiArchiveExtended
        ArchiveInvoiceExtendedRequest request = new ArchiveInvoiceExtendedRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("N");
        request.setREQUESTHEADER(header);

        ArchiveInvoiceExtendedContent content = new ArchiveInvoiceExtendedContent();
        ArchiveInvoiceExtendedContent.INVOICEPROPERTIES props = new ArchiveInvoiceExtendedContent.INVOICEPROPERTIES();

        props.setEARSIVFLAG(FLAGVALUE.Y);

        // id
        UUID uuid = UUID.randomUUID();
        String id = IdentifierUtils.createInvoiceIdRandom("X01");

        File draftFile = new File("xml\\draft-eiarchive.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draftFile, uuid, id);

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(createdXml.toPath()));

        props.setINVOICECONTENT(b64);

        EARSIVPROPERTIES eiarchiveProps = new EARSIVPROPERTIES();
        eiarchiveProps.setEARSIVTYPE(EARSIVTYPEVALUE.NORMAL);
        eiarchiveProps.setSUBSTATUS(SUBSTATUSVALUE.NEW); // new or draft

        props.setEARSIVPROPERTIES(eiarchiveProps);

        content.getINVOICEPROPERTIES().add(props);

        request.setArchiveInvoiceExtendedContent(content);

        ArchiveInvoiceExtendedResponse resp = client.eiarchiveWS().writeToArchiveExtended(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendEiarchiveUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

}
