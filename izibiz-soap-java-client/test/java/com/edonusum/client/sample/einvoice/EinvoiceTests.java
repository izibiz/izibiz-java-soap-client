package com.edonusum.client.sample.einvoice;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.adapter.EinvoiceAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.einvoice.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
public class EinvoiceTests {

    private AuthAdapter authAdapter = new AuthAdapter();
    private EinvoiceAdapter einvoiceAdapter = new EinvoiceAdapter();

    private String getSessionId() {
        String sessionId = authAdapter.login(AuthTests.prepareLoginRequest()).getSESSIONID();
        return sessionId;
    }

    @DisplayName("Fatura Gönderme")
    @Test
    public void sendInvoice_givenValidInvoice_then_sendInvoiceSucceeds() throws IOException { // sendInvoice
        SendInvoiceRequest request = new SendInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        header.setCOMPRESSED("N");
        request.setREQUESTHEADER(header);

        INVOICE inv = new INVOICE();

        //invoice header
        INVOICE.HEADER invoiceHeader = new INVOICE.HEADER();
        invoiceHeader.setDIRECTION("IN");
        inv.setHEADER(invoiceHeader);

        //invoice content
        File draft = new File("xml\\draft-invoice.xml"); // draft invoice
        File createdXML = XMLUtils.createXmlFromDraftInvoice(draft, UUID.randomUUID(), IdentifierUtils.createInvoiceIdRandom("DMY"));

        Base64Binary b64array = new Base64Binary();
        b64array.setValue(Files.readAllBytes(createdXML.toPath()));

        inv.setCONTENT(b64array);

        request.getINVOICE().add(inv);

        SendInvoiceResponse resp = einvoiceAdapter.sendInvoice(request);

        createdXML.delete();

        Assertions.assertNull(resp.getERRORTYPE());
    }

    @DisplayName("E-Fatura Okuma")
    @Test
    public void getInvoice_givenInvoiceSearchKey_then_returnsInvoiceList() throws JAXBException, IOException { // getInvoice
        GetInvoiceRequest request = new GetInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GetInvoiceRequest.INVOICESEARCHKEY key = new GetInvoiceRequest.INVOICESEARCHKEY();
        key.setDIRECTION("OUT");
        request.setINVOICESEARCHKEY(key);

        GetInvoiceResponse response = einvoiceAdapter.getInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICE().get(0).getHEADER().getSUPPLIER());
    }

    // Toplu fatura listesi ile test gerektiğinde kullanılmak üzere yazılmıştır, getInvoice ile aynı işi yapmaktadır
    private List<INVOICE> getInvoiceList(String sessionId) throws JAXBException {
        GetInvoiceRequest request = new GetInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(sessionId);
        request.setREQUESTHEADER(header);

        GetInvoiceRequest.INVOICESEARCHKEY key = new GetInvoiceRequest.INVOICESEARCHKEY();
        key.setDIRECTION("OUT");
        request.setINVOICESEARCHKEY(key);

        GetInvoiceResponse response = einvoiceAdapter.getInvoice(request);

        return response.getINVOICE();
    }

    @DisplayName("Fatura Görsel Okuma")
    @Test
    public void getInvoiceWithType_givenInvoiceSearchKey_andGivenType_then_returnsInvoiceList() { // getInvoiceWithType
        GetInvoiceWithTypeRequest request = new GetInvoiceWithTypeRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GetInvoiceWithTypeRequest.INVOICESEARCHKEY key = new GetInvoiceWithTypeRequest.INVOICESEARCHKEY();

        key.setTYPE("PDF");
        key.setDIRECTION("OUT");
        request.setINVOICESEARCHKEY(key);

        request.setHEADERONLY("N");

        GetInvoiceWithTypeResponse response = einvoiceAdapter.getInvoiceWithType(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICE().get(0).getHEADER().getSUPPLIER());
    }

    @DisplayName("Taslak Fatura Yükleme")
    @Test
    public void loadInvoice_givenValidDraftInvoice_then_loadInvoiceSucceeds() throws IOException { // loadInvoice
        LoadInvoiceRequest request = new LoadInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        INVOICE inv = new INVOICE();

        // invoice header
        INVOICE.HEADER invoiceHeader = new INVOICE.HEADER();
        invoiceHeader.setDIRECTION("IN");
        inv.setHEADER(invoiceHeader);

        File draft = new File("xml\\taslak.zip");

        Base64Binary b64binary = new Base64Binary();
        b64binary.setValue(Files.readAllBytes(draft.toPath()));

        inv.setCONTENT(b64binary);

        request.getINVOICE().add(inv);

        header.setCOMPRESSED("Y");

        LoadInvoiceResponse response = einvoiceAdapter.loadInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    @DisplayName("Fatura Okundu İşaretleme")
    @Test
    public void markInvoice_givenValidInvoice_andGivenMarkValue_then_marksInvoice() throws JAXBException, FileNotFoundException { // markInvoice
        MarkInvoiceRequest request = new MarkInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        MarkInvoiceRequest.MARK mark = new MarkInvoiceRequest.MARK();
        mark.setValue("READ");

        mark.getINVOICE().add(getInvoiceList(header.getSESSIONID()).get(0));
        request.setMARK(mark);

        request.setREQUESTHEADER(header);

        MarkInvoiceResponse response = einvoiceAdapter.markInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getINTLTXNID()); // transaction id
    }

    @DisplayName("Fatura Durum Sorgulama")
    @Test
    public void getInvoiceStatus_givenValidInvoice_then_returnsStatus() throws JAXBException, FileNotFoundException { // getInvoiceStatus
        GetInvoiceStatusRequest request = new GetInvoiceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        request.setINVOICE(getInvoiceList(header.getSESSIONID()).get(0));

        GetInvoiceStatusResponse response = einvoiceAdapter.getInvoiceStatus(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICESTATUS().getSTATUSDESCRIPTION());
    }

    @DisplayName("Toplu Fatura Durum Sorgulama")
    @Test
    public void getInvoiceStatusAll_givenInvoiceList_then_returnsStatusList() throws JAXBException, FileNotFoundException { // getInvoiceStatusAll
        GetInvoiceStatusAllRequest request = new GetInvoiceStatusAllRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());

        request.setREQUESTHEADER(header);

        // getting status for all invoices that returns from getInvoice
        request.getUUID().addAll(getInvoiceList(header.getSESSIONID()).stream().map(inv -> inv.getUUID()).collect(Collectors.toList()));

        GetInvoiceStatusAllResponse response = einvoiceAdapter.getInvoiceStatusAll(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICESTATUS().get(0).getHEADER().getSTATUS());
    }

    @DisplayName("Uygulama Yanıtı Gönderme (Sunucu imzası ile)")
    @Test
    public void sendInvoiceResponseWithServerSign_givenStatus_then_sendsResponse() { // sendInvoiceResponseWithServerSign
        SendInvoiceResponseWithServerSignRequest request = new SendInvoiceResponseWithServerSignRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        request.setSTATUS("KABUL");

        INVOICE inv = new INVOICE();
        inv.setID("AKN2022000000076"); // Portal üzerinden oluşturulan fatura ile denenmiştir. ID değeri değiştirilmelidir.

        request.getINVOICE().add(inv);

        SendInvoiceResponseWithServerSignResponse response = einvoiceAdapter.sendInvoiceResponseWithServerSign(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getERRORTYPE());
    }

}
