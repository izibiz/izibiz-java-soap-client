package com.edonusum.client.sample.einvoice;

import com.edonusum.client.adapter.EinvoiceAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.ubl.InvoiceUBL;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.einvoice.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E-Fatura servisi")
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
class EinvoiceTests {
    
    @Autowired
    private EinvoiceAdapter adapter;
    
    private static String sendEinvoiceUUID = "";
    private static String sendEinvoiceID = "";
    
    private static List<INVOICE> invoices;
    
    private static String SESSION_ID = "";

    @Test
    @Order(1)
    @DisplayName("Giriş yapma")
    void login() { // login
        SESSION_ID = AuthTests.login();

        Assertions.assertNotEquals("", SESSION_ID);
        Assertions.assertNotNull(SESSION_ID);
    }

    @Test
    @Order(2)
    @DisplayName("E-Fatura listesi çekme")
    void canGetInvoiceList() throws Exception { // getInvoice
        GetInvoiceRequest request = new GetInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetInvoiceRequest.INVOICESEARCHKEY searchKey = new GetInvoiceRequest.INVOICESEARCHKEY();
        searchKey.setDIRECTION("OUT");
        searchKey.setLIMIT(100);

        request.setINVOICESEARCHKEY(searchKey);

        /* okunmuş faturaları alma */
        searchKey.setREADINCLUDED(true);

        // Tarihe göre alma
        searchKey.setSTARTDATE(DateUtils.minusDays(30));
        searchKey.setENDDATE(DateUtils.now());

        // ID ye göre alma
        /*
        searchKey.setUUID("EXAMPLE");
        */

        GetInvoiceResponse response = adapter.getInvoice(request);

        invoices = response.getINVOICE();

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICE().get(0).getHEADER().getSUPPLIER());
    }

    @Test
    @Order(3)
    @DisplayName("E-Fatura okuma (Tip ile))")
    void canGetInvoiceList_withType() throws Exception{ // getInvoiceWithType
        GetInvoiceWithTypeRequest request = new GetInvoiceWithTypeRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetInvoiceWithTypeRequest.INVOICESEARCHKEY searchKey = new GetInvoiceWithTypeRequest.INVOICESEARCHKEY();

        searchKey.setTYPE("PDF");
        searchKey.setDIRECTION("OUT");
        // Tarihe göre alma
        searchKey.setSTARTDATE(DateUtils.minusDays(30));
        searchKey.setENDDATE(DateUtils.now());

        /* Okunmuş faturaların alınması */
        searchKey.setREADINCLUDED(true);

        request.setINVOICESEARCHKEY(searchKey);

        request.setHEADERONLY("N");

        GetInvoiceWithTypeResponse response = adapter.getInvoiceWithType(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICE().get(0).getHEADER().getSUPPLIER());
    }

    @Test
    @Order(4)
    @DisplayName("Taslak e-fatura yükleme")
    void canLoadInvoice() throws IOException { // loadInvoice
        LoadInvoiceRequest request = new LoadInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        INVOICE inv = new INVOICE();

        // invoice header
        INVOICE.HEADER invoiceHeader = new INVOICE.HEADER();
        invoiceHeader.setDIRECTION("OUT");
        inv.setHEADER(invoiceHeader);

        // id
        String id = IdentifierUtils.createInvoiceIdRandom("DMY") + "0";
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-invoice.xml");
        File created = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary b64binary = new Base64Binary();
        b64binary.setValue(Files.readAllBytes(created.toPath()));

        inv.setCONTENT(b64binary);

        request.getINVOICE().add(inv);

        header.setCOMPRESSED("N");

        LoadInvoiceResponse response = adapter.loadInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        created.delete();

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(5)
    @DisplayName("E-Fatura gönderme")
    void canSendInvoice() throws Exception { // sendInvoice
        SendInvoiceRequest request = new SendInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("N");
        request.setREQUESTHEADER(header);

        INVOICE inv = new INVOICE();

        //invoice header
        INVOICE.HEADER invoiceHeader = new INVOICE.HEADER();

        inv.setHEADER(invoiceHeader);

        Base64Binary base64Binary = new Base64Binary();

        // take data from ubl
        InvoiceType ubl = new InvoiceUBL().getInvoice();
        File file = new File(System.getProperty("user.home")+"\\Desktop\\x.xml");

        ObjectFactory o = new ObjectFactory();
        JAXB.marshal(o.createInvoice(ubl), file);

        base64Binary.setValue(Files.readAllBytes(file.toPath()));

        file.delete();

        inv.setCONTENT(base64Binary);

        request.getINVOICE().add(inv);

        SendInvoiceResponse resp = adapter.sendInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        sendEinvoiceUUID = ubl.getUUID().getValue();
        sendEinvoiceID = ubl.getID().getValue();
    }


    @Test
    @Order(6)
    @DisplayName("E-Fatura yanıtı gönderme (Server imzalı)")
    void canSendInvoiceResponse_withServerSign() { // sendInvoiceResponseWithServerSign
        SendInvoiceResponseWithServerSignRequest request = new SendInvoiceResponseWithServerSignRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setSTATUS("KABUL");

        INVOICE invoice = new INVOICE();
        invoice.setUUID(sendEinvoiceUUID);
        invoice.setID(sendEinvoiceID);

        request.getINVOICE().add(invoice);

        // Debug modunda bu satıra gelmeden önce son gönderilen fatura portal gelen kutusunda görünene kadar bekleyiniz
        SendInvoiceResponseWithServerSignResponse response = adapter.sendInvoiceResponseWithServerSign(request);

        // Belirtilen ID ye sahip bir fatura bulunamadı
        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getERRORTYPE());
    }

    @Test
    @Order(7)
    @DisplayName("E-Fatura işaretleme")
    void canMarkInvoice() { // markInvoice
        MarkInvoiceRequest request = new MarkInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        MarkInvoiceRequest.MARK mark = new MarkInvoiceRequest.MARK();
        mark.setValue("UNREAD");

        mark.getINVOICE().addAll(invoices);
        request.setMARK(mark);

        request.setREQUESTHEADER(header);

        MarkInvoiceResponse response = adapter.markInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getINTLTXNID()); // transaction id
    }

    @Test
    @Order(8)
    @DisplayName("E-Fatura durum sorgulama")
    void canGetInvoiceStatus() { // getInvoiceStatus
        GetInvoiceStatusRequest request = new GetInvoiceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setINVOICE(invoices.get(0));

        GetInvoiceStatusResponse response = adapter.getInvoiceStatus(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICESTATUS().getSTATUSDESCRIPTION());
    }

    @Test
    @Order(9)
    @DisplayName("E-Fatura toplu durum sorgulama")
    void canGetInvoiceStatus_multiple() { // getInvoiceStatusAll
        GetInvoiceStatusAllRequest request = new GetInvoiceStatusAllRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);

        request.setREQUESTHEADER(header);

        // getting status for all invoices that returns from getInvoice
        request.getUUID().addAll(invoices.stream().map(INVOICE::getUUID).collect(Collectors.toList()));

        GetInvoiceStatusAllResponse response = adapter.getInvoiceStatusAll(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICESTATUS().get(0).getHEADER().getSTATUS());
    }

    @Test
    @Order(10)
    @DisplayName("Çıkış yapma")
    void logout() { // logout
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

}
