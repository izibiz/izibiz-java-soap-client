package com.edonusum.client.sample.einvoice;

import com.edonusum.client.SoapJavaClientApplication;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.einvoice.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
public class EinvoiceTests {
    private static SoapJavaClientApplication client = new SoapJavaClientApplication();
    
    private String sendEinvoiceUUID = "";
    private String loadEinvoiceUUID = "";
    
    private List<INVOICE> invoices;
    
    private static String SESSION_ID = "";

    @Test
    public void runAllTests() throws Exception{
        // login
        login();

        // getInvoice
        getInvoice_givenInvoiceSearchKey_then_returnsInvoiceList();

        // getInvoiceWithType
        getInvoiceWithType_givenInvoiceSearchKey_andGivenType_then_returnsInvoiceList();

        // loadInvoice
        loadInvoice_givenValidDraftInvoice_then_loadInvoiceSucceeds();

        // sendInvoice
        sendInvoice_givenValidInvoice_then_sendInvoiceSucceeds();

        // markInvoice
        markInvoice_givenValidInvoice_andGivenMarkValue_then_marksInvoice();

        // sendInvoiceResponseWithServerSign
        sendInvoiceResponseWithServerSign_givenStatus_then_sendsResponse();

        // getInvoiceStatus
        getInvoiceStatus_givenValidInvoice_then_returnsStatus();

        // getInvoiceStatusAll
        getInvoiceStatusAll_givenInvoiceList_then_returnsStatusList();

        // logout
        logout();
    }
    

    private void login() { // login
        SESSION_ID = AuthTests.login();
    }

    private void logout() { // logout
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

    private void sendInvoice_givenValidInvoice_then_sendInvoiceSucceeds() throws IOException { // sendInvoice
        SendInvoiceRequest request = new SendInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("N");
        request.setREQUESTHEADER(header);

        INVOICE inv = new INVOICE();

        //invoice header
        INVOICE.HEADER invoiceHeader = new INVOICE.HEADER();
        invoiceHeader.setDIRECTION("IN");
        inv.setHEADER(invoiceHeader);

        // id
        UUID uuid = UUID.randomUUID();
        String id = IdentifierUtils.createInvoiceIdRandom("DMY");

        //invoice content
        File draft = new File("xml\\draft-invoice.xml"); // draft invoice
        File createdXML = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary b64array = new Base64Binary();
        b64array.setValue(Files.readAllBytes(createdXML.toPath()));

        inv.setCONTENT(b64array);

        request.getINVOICE().add(inv);

        SendInvoiceResponse resp = client.einvoiceWS().sendInvoice(request);

        createdXML.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendEinvoiceUUID = uuid.toString();
    }
    
    private void getInvoice_givenInvoiceSearchKey_then_returnsInvoiceList() throws Exception { // getInvoice
        GetInvoiceRequest request = new GetInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetInvoiceRequest.INVOICESEARCHKEY key = new GetInvoiceRequest.INVOICESEARCHKEY();
        key.setDIRECTION("OUT");
        request.setINVOICESEARCHKEY(key);

        /* okunmuş faturaları alma */
        key.setREADINCLUDED(true);

        // Tarihe göre alma
        key.setSTARTDATE(DateUtils.minusDays(30));
        key.setENDDATE(DateUtils.now());

        // ID ye göre alma
        /*
        key.setUUID("EXAMPLE");
        */

        GetInvoiceResponse response = client.einvoiceWS().getInvoice(request);

        invoices = response.getINVOICE();

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICE().get(0).getHEADER().getSUPPLIER());
    }
    
    private void getInvoiceWithType_givenInvoiceSearchKey_andGivenType_then_returnsInvoiceList() throws Exception{ // getInvoiceWithType
        GetInvoiceWithTypeRequest request = new GetInvoiceWithTypeRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetInvoiceWithTypeRequest.INVOICESEARCHKEY key = new GetInvoiceWithTypeRequest.INVOICESEARCHKEY();

        key.setTYPE("XML");
        key.setDIRECTION("OUT");
        // Tarihe göre alma
        key.setSTARTDATE(DateUtils.minusDays(30));
        key.setENDDATE(DateUtils.now());

        /* Okunmuş faturaların alınması */
        key.setREADINCLUDED(true);

        request.setINVOICESEARCHKEY(key);

        request.setHEADERONLY("N");

        GetInvoiceWithTypeResponse response = client.einvoiceWS().getInvoiceWithType(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICE().get(0).getHEADER().getSUPPLIER());
    }

    private void loadInvoice_givenValidDraftInvoice_then_loadInvoiceSucceeds() throws IOException { // loadInvoice
        LoadInvoiceRequest request = new LoadInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        INVOICE inv = new INVOICE();

        // invoice header
        INVOICE.HEADER invoiceHeader = new INVOICE.HEADER();
        invoiceHeader.setDIRECTION("IN");
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

        LoadInvoiceResponse response = client.einvoiceWS().loadInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        loadEinvoiceUUID = uuid.toString();

        created.delete();

        System.out.println(response.getREQUESTRETURN().getRETURNCODE());
    }

    private void markInvoice_givenValidInvoice_andGivenMarkValue_then_marksInvoice() { // markInvoice
        MarkInvoiceRequest request = new MarkInvoiceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        MarkInvoiceRequest.MARK mark = new MarkInvoiceRequest.MARK();
        mark.setValue("UNREAD");

        mark.getINVOICE().addAll(invoices);
        request.setMARK(mark);

        request.setREQUESTHEADER(header);

        MarkInvoiceResponse response = client.einvoiceWS().markInvoice(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getREQUESTRETURN().getINTLTXNID()); // transaction id
    }

    private void getInvoiceStatus_givenValidInvoice_then_returnsStatus() { // getInvoiceStatus
        GetInvoiceStatusRequest request = new GetInvoiceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setINVOICE(invoices.get(0));

        GetInvoiceStatusResponse response = client.einvoiceWS().getInvoiceStatus(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICESTATUS().getSTATUSDESCRIPTION());
    }

    private void getInvoiceStatusAll_givenInvoiceList_then_returnsStatusList() { // getInvoiceStatusAll
        GetInvoiceStatusAllRequest request = new GetInvoiceStatusAllRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);

        request.setREQUESTHEADER(header);

        // getting status for all invoices that returns from getInvoice
        request.getUUID().addAll(invoices.stream().map(inv -> inv.getUUID()).collect(Collectors.toList()));

        GetInvoiceStatusAllResponse response = client.einvoiceWS().getInvoiceStatusAll(request);

        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getINVOICESTATUS().get(0).getHEADER().getSTATUS());
    }

    private void sendInvoiceResponseWithServerSign_givenStatus_then_sendsResponse() { // sendInvoiceResponseWithServerSign
        SendInvoiceResponseWithServerSignRequest request = new SendInvoiceResponseWithServerSignRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setSTATUS("KABUL");

        request.getINVOICE().addAll(invoices);

        SendInvoiceResponseWithServerSignResponse response = client.einvoiceWS().sendInvoiceResponseWithServerSign(request);

        // Belirtilen ID ye sahip bir fatura bulunamadı
        Assertions.assertNull(response.getERRORTYPE());

        System.out.println(response.getERRORTYPE());
    }

}
