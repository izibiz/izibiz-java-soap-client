package com.edonusum.client.sample.eiarchive;

import com.edonusum.client.adapter.EiarchiveAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.eiarchive.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@DisplayName("E-Arşiv fatura servisi")
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EiarchiveTests {
    
    @Autowired
    private EiarchiveAdapter adapter;
    
    private static String loadEiarchiveUUID = "";
    private static String sendEiarchiveUUID = "";

    private static List<EARCHIVEINV> invoices;
    private static List<REPORT> reports;

    private static String SESSION_ID;

    @Test
    @Order(1)
    @DisplayName("Giriş Yapma")
    public void login() {
        SESSION_ID = AuthTests.login();
    }

    @Test
    @Order(2)
    @DisplayName("E-Arşiv yazma")
    public void writeToEiarchiveExtended_givenValidEiarchive_then_writesToArchive() throws IOException { // writeToEiArchiveExtended
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

        ArchiveInvoiceExtendedResponse resp = adapter.writeToArchiveExtended(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendEiarchiveUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(3)
    @DisplayName("E-Arşiv okuma")
    public void readFromArchive_givenInvoiceId_then_returnsArchiveInvoice() { // readFromArchive
        ArchiveInvoiceReadRequest request = new ArchiveInvoiceReadRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");
        header.setSESSIONID(SESSION_ID);

        request.setINVOICEID(sendEiarchiveUUID); // son gönderilen fatura
        request.setPORTALDIRECTION("OUT");
        request.setPROFILE("XML");

        request.setREQUESTHEADER(header);

        ArchiveInvoiceReadResponse resp = adapter.readFromArchive(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getINVOICE().get(0));
    }

    @Test
    @Order(4)
    @DisplayName("E-Arşiv Faturasını E-Posta Olarak Tekrar Gönderme")
    public void getEmailEarchiveInvoice_givenInvoiceUUID_andValidEmail_then_sendsInvoiceToEmail() { // getEmailEarchiveInvoice
        GetEmailEarchiveInvoiceRequest request = new GetEmailEarchiveInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setFATURAUUID(sendEiarchiveUUID); // example id
        request.setEMAIL("example@email.com"); // email address to send

        GetEmailEarchiveInvoiceResponse resp = adapter.getEmailEarchiveInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(5)
    @DisplayName("E-Arşiv durum sorgulama")
    public void getEarchiveStatus_givenInvoiceId_then_returnsEArchiveInvoiceStatus() { // getEArchiveStatus
        GetEArchiveInvoiceStatusRequest request = new GetEArchiveInvoiceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().add(sendEiarchiveUUID); // toplu status sorgulama

        GetEArchiveInvoiceStatusResponse resp = adapter.getEArchiveStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getINVOICE().get(0).getHEADER().getSTATUSDESC());
    }

    @Test
    @Order(6)
    @DisplayName("E-Arşiv iptal etme")
    public void cancelEarchiveInvoiceResponse_givenInvoiceUUID_then_cancelsInvoice() { // cancelEArchiveInvoice
        CancelEArchiveInvoiceRequest request = new CancelEArchiveInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);

        request.setREQUESTHEADER(header);

        CancelEArchiveInvoiceRequest.CancelEArsivInvoiceContent content = new CancelEArchiveInvoiceRequest.CancelEArsivInvoiceContent();
        content.setFATURAUUID(sendEiarchiveUUID);

        request.getCancelEArsivInvoiceContent().add(content);

        CancelEArchiveInvoiceResponse resp = adapter.cancelEArchiveInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getINTLTXNID());
    }

    @Test
    @Order(7)
    @DisplayName("E-Arşiv iptal etme (Delete)")
    public void cancelEarchiveInvoiceResponse_givenInvoiceUUID_andGivenDeleteFlag_then_cancelsInvoice() { // cancelEArchiveInvoice
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
        dökümanda istenen parametreler doldurulduktan sonra
        content.setUPLOADFLAG("Y")
        */

        request.getCancelEArsivInvoiceContent().add(content);

        CancelEArchiveInvoiceResponse resp = adapter.cancelEArchiveInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getINTLTXNID());
    }

    @Test
    @Order(8)
    @DisplayName("E-Arşiv rapor listesi çekme")
    public void getEarchiveReport_givenPeriod_andGivenFlagValues_then_returnsReportList() { // getEarchiveReport
        GetEArchiveReportRequest request = new GetEArchiveReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        DecimalFormat format = new DecimalFormat("#00");

        // period text
        String period = LocalDate.now().getYear() + format.format(LocalDate.now().minusDays(30).getMonthValue());  // 2018 mayıs = 201805 şeklinde formatlanmalı

        request.setREPORTPERIOD(period);

        request.setREPORTSTATUSFLAG("Y");

        GetEArchiveReportResponse resp = adapter.getEarchiveReport(request);

        Assertions.assertNull(resp.getERRORTYPE());

        reports = resp.getREPORT();

        System.out.println(resp.getREPORT().get(0).getREPORTSTATUS());
    }

    @Test
    @Order(9)
    @DisplayName("E-Arşiv raporu okuma")
    public void readArchiveReport_givenReportId_thenReturnsReportContent() throws Exception { // ReadArchiveReport
        ReadEArchiveReportRequest request = new ReadEArchiveReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setRAPORNO(reports.get(0).getREPORTNO());

        ReadEArchiveReportResponse response = adapter.readEarchiveReport(request);;

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(10)
    @DisplayName("Çıkış yapma")
    public void logout() {
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

}
