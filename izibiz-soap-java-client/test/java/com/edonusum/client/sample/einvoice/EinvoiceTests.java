package com.edonusum.client.sample.einvoice;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.adapter.EinvoiceAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.einvoice.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

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
    public void givenValidInvoice_then_sendInvoiceSucceeds() throws IOException { // sendInvoice
        SendInvoiceRequest request = new SendInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        header.setCOMPRESSED("Y");

        INVOICE i = new INVOICE();

        Base64Binary b64array = new Base64Binary();

        String desktopFile = System.getProperty("user.home")+"\\Desktop"+"\\test.zip";

        b64array.setValue(ZipUtils.zipToBase64(desktopFile));
        i.setCONTENT(b64array);
        INVOICE.HEADER h = new INVOICE.HEADER();
        h.setDIRECTION("OUT");
        i.setHEADER(h);

        request.getINVOICE().add(i);
        request.setREQUESTHEADER(header);

        SendInvoiceResponse resp = einvoiceAdapter.sendInvoice(request);

        Assertions.assertNull(resp.getERRORTYPE());
    }

    @DisplayName("E-Fatura Okuma")
    @Test
    public void givenInvoiceSearchKey_then_returnsInvoiceList() { // getInvoice
        GetInvoiceRequest request = new GetInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GetInvoiceRequest.INVOICESEARCHKEY key = new GetInvoiceRequest.INVOICESEARCHKEY();
        request.setINVOICESEARCHKEY(key);

        GetInvoiceResponse response = einvoiceAdapter.getInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICE().get(0).getHEADER().getSUPPLIER());
    }

    // Toplu fatura listesi ile test gerektiğinde kullanılmak üzere yazılmıştır, getInvoice ile aynı işi yapmaktadır
    private List<INVOICE> getInvoiceList(String sessionId) {
        GetInvoiceRequest request = new GetInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(sessionId);
        request.setREQUESTHEADER(header);

        GetInvoiceRequest.INVOICESEARCHKEY key = new GetInvoiceRequest.INVOICESEARCHKEY();
        request.setINVOICESEARCHKEY(key);

        GetInvoiceResponse response = einvoiceAdapter.getInvoice(request);

        return response.getINVOICE();
    }

    @DisplayName("Taslak Fatura Yükleme")
    @Test
    public void givenDraftInvoice_then_loadInvoiceSucceeds() throws IOException { // loadInvoice
        LoadInvoiceRequest request = new LoadInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        String desktopFile = System.getProperty("user.home")+"\\Desktop"+"\\fatura.zip";

        Base64Binary b64binary = new Base64Binary();
        b64binary.setValue(ZipUtils.zipToBase64(desktopFile));

        INVOICE inv = new INVOICE();
        inv.setCONTENT(b64binary);

        request.getINVOICE().add(inv);

        LoadInvoiceResponse response = einvoiceAdapter.loadInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    @DisplayName("Fatura Okundu İşaretleme")
    @Test
    public void givenInvoice_andGivenMarkValue_then_returnsTransactionCode() { // markInvoice
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
    public void givenInvoice_then_returnsStatus() { // getInvoiceStatus
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
    public void givenInvoiceList_then_returnsStatusList() { // getInvoiceStatusAll
        GetInvoiceStatusAllRequest request = new GetInvoiceStatusAllRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());

        request.setREQUESTHEADER(header);

        request.getUUID().add("c2916482-983d-4822-8846-33edfb77ddb0");

        GetInvoiceStatusAllResponse response = einvoiceAdapter.getInvoiceStatusAll(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICESTATUS().get(0).getID());
    }

    @DisplayName("Uygulama Yanıtı Gönderme (Sunucu imzası ile)")
    @Test
    public void sendInvoiceResponseWithServerSign_then_returnsErrorTypeNull() { // sendInvoiceResponseWithServerSign
        SendInvoiceResponseWithServerSignRequest request = new SendInvoiceResponseWithServerSignRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        request.setSTATUS("KABUL");

        List<INVOICE> invoices = getInvoiceList(header.getSESSIONID());

        request.getINVOICE().add(invoices.get(5));

        SendInvoiceResponseWithServerSignResponse response = einvoiceAdapter.sendInvoiceResponseWithServerSign(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getERRORTYPE());
    }

    @DisplayName("Fatura Görsel Okuma")
    @Test
    public void givenInvoiceSearchKeyWithType_then_returnsInvoiceList() { // getInvoiceWithType
        GetInvoiceWithTypeRequest request = new GetInvoiceWithTypeRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GetInvoiceWithTypeRequest.INVOICESEARCHKEY key = new GetInvoiceWithTypeRequest.INVOICESEARCHKEY();

        key.setTYPE("XML");
        key.setDIRECTION("IN");
        request.setINVOICESEARCHKEY(key);

        request.setHEADERONLY("N");

        GetInvoiceWithTypeResponse response = einvoiceAdapter.getInvoiceWithType(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICE().get(0).getHEADER().getSUPPLIER());
    }

}
