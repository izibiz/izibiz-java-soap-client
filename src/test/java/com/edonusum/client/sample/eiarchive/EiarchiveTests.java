package com.edonusum.client.sample.eiarchive;

import com.edonusum.client.adapter.EiarchiveAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.ubl.ArchiveInvoiceUBL;
import com.edonusum.client.wsdl.eiarchive.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXB;
import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@DisplayName("E-Arşiv fatura servisi")
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EiarchiveTests {
    
    @Autowired
    private EiarchiveAdapter adapter;

    private static String sendEiarchiveUUID = "";

    private static List<REPORT> reports;

    private static String SESSION_ID;

    @Test
    @Order(1)
    @DisplayName("Giriş Yapma")
    void login() {
        SESSION_ID = AuthTests.login();

        Assertions.assertNotEquals("", SESSION_ID);
    }

    @Test
    @Order(2)
    @DisplayName("E-Arşiv yazma")
    void canWriteToArchive() throws Exception { // writeToEiArchiveExtended
        ArchiveInvoiceExtendedRequest request = new ArchiveInvoiceExtendedRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("N");
        request.setREQUESTHEADER(header);

        ArchiveInvoiceExtendedContent content = new ArchiveInvoiceExtendedContent();
        ArchiveInvoiceExtendedContent.INVOICEPROPERTIES props = new ArchiveInvoiceExtendedContent.INVOICEPROPERTIES();

        props.setEARSIVFLAG(FLAGVALUE.Y);

        // id
        //UUID uuid = UUID.randomUUID();
        //String id = IdentifierUtils.createInvoiceIdRandom("X01"); // taslak değil ise createInvoiceIdRandomPrefix() kullanılmalı

        //File draftFile = new File("xml\\draft-eiarchive.xml");
        //File createdXml = XMLUtils.createXmlFromDraftInvoice(draftFile, uuid, id);

        ObjectFactory o = new ObjectFactory();
        ArchiveInvoiceUBL ubl = new ArchiveInvoiceUBL();
        File file = new File(System.getProperty("user.home") + "\\Desktop\\x.xml");

        JAXB.marshal(o.createInvoice(ubl.getInvoice()), file);

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(file.toPath()));

        props.setINVOICECONTENT(base64Binary);

        EARSIVPROPERTIES eiarchiveProps = new EARSIVPROPERTIES();
        eiarchiveProps.setEARSIVTYPE(EARSIVTYPEVALUE.NORMAL);
        eiarchiveProps.setSUBSTATUS(SUBSTATUSVALUE.NEW); // new veya draft

        props.setEARSIVPROPERTIES(eiarchiveProps);

        content.getINVOICEPROPERTIES().add(props);

        request.setArchiveInvoiceExtendedContent(content);

        ArchiveInvoiceExtendedResponse resp = adapter.writeToArchiveExtended(request);

        Assertions.assertNull(resp.getERRORTYPE());

        sendEiarchiveUUID = ubl.getInvoice().getUUID().getValue();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(3)
    @DisplayName("E-Arşiv okuma")
    void canReadFromArchive() throws Exception{ // readFromArchive
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
    void canSendEmailInvoice() { // getEmailEarchiveInvoice
        GetEmailEarchiveInvoiceRequest request = new GetEmailEarchiveInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setFATURAUUID(sendEiarchiveUUID); // last sent invoice
        request.setEMAIL("example@email.com"); // email address to send. must be valid to get this working

        GetEmailEarchiveInvoiceResponse resp = adapter.getEmailEarchiveInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(5)
    @DisplayName("E-Arşiv durum sorgulama")
    void canGetArchiveStatus() { // getEArchiveStatus
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
    void canCancelArchiveInvoice() { // cancelEArchiveInvoice
        CancelEArchiveInvoiceRequest request = new CancelEArchiveInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);

        request.setREQUESTHEADER(header);

        CancelEArchiveInvoiceRequest.CancelEArsivInvoiceContent content = new CancelEArchiveInvoiceRequest.CancelEArsivInvoiceContent();
        content.setFATURAUUID(sendEiarchiveUUID); // son gönderilen faturanın UUID değeri

        request.getCancelEArsivInvoiceContent().add(content);

        // Debug modunda bu satıra gelmeden bekleyiniz, son gönderilen fatura portalde gelen kutusuna düşmedikçe bu metod çalışmayacaktır.
        CancelEArchiveInvoiceResponse resp = adapter.cancelEArchiveInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getINTLTXNID());
    }

    @Test
    @Order(7)
    @DisplayName("E-Arşiv iptal etme (Delete)")
    void canCancelArchiveInvoice_withDeleteFlag() { // cancelEArchiveInvoice
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
    void canGetArchiveReport() { // getEarchiveReport
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
    void canReadArchiveReport() throws Exception { // ReadArchiveReport
        ReadEArchiveReportRequest request = new ReadEArchiveReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setRAPORNO(reports.get(0).getREPORTNO());

        ReadEArchiveReportResponse response = adapter.readEarchiveReport(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(10)
    @DisplayName("Çıkış yapma")
    void logout() {
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

}
