package com.edonusum.client.sample.edespatch;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.adapter.EdespatchAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.edespatch.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
public class EdespatchTests {
    private AuthAdapter authAdapter = new AuthAdapter();
    private EdespatchAdapter edespatchAdapter = new EdespatchAdapter();

    private static String loadDespatchAdviceUUID = "";
    private static String loadReceiptAdviceUUID = "";
    private static String sendDespatchAdviceUUID = "";
    private static String sendReceiptAdviceUUID = "";

    private static List<DESPATCHADVICE> despatchadvices;
    private static List<RECEIPTADVICE> receiptadvices;
    
    private static String SESSION_ID;

    @Test
    public void runAllTests() throws Exception{
        // login
        login();

        // getDespatchAdvice
        getDespatchAdvice_givenSearchKey_returnsDespatchList();

        // getReceiptAdvice
        getReceiptAdvice_canGetReceiptList_withGivenParameters();

        // loadDespatchAdvice
        loadDespatchAdvice_givenDespatchAdviceContent_then_canLoadDraftDespatchAdvice();

        // sendReceiptAdvice
        loadReceiptAdvice_givenValidContent_thenSendsDespatchAdviceAsDraft();

        // sendDespatchAdvice
        sendDespatchAdvice_givenValidDestpachAdvice_then_canSendDespatchAdvice();

        // sendReceiptAdvice
        //sendReceiptAdvice_givenValidContent_then_canSendReceiptAdvice();

        // getDespatchAdviceStatus
        getDespatchAdviceStatus_givenEdespatchUUID_then_returnsStatus();

        // getReceiptAdviceStatus
        getReceiptAdviceStatus_givenUUID_returnsReceiptStatus();

        // markDespatchAdvice
        markDespatchAdvice_givenDespatchUUID_andGivenAction_marksDespatchAdvice();

        // markReceiptAdvice
        markReceiptAdvice_marksReceipts_withGivenParameters();

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

    private void getDespatchAdviceStatus_givenEdespatchUUID_then_returnsStatus() { // GetDespatchAdviceStatus
        GetDespatchAdviceStatusRequest request = new GetDespatchAdviceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(despatchadvices.stream().map(d -> d.getUUID()).collect(Collectors.toList())); // toplu status sorgulama

        GetDespatchAdviceStatusResponse resp = edespatchAdapter.getDespatchAdviseStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getDESPATCHADVICESTATUS().get(0).getSTATUS());
    }

    private void getDespatchAdvice_givenSearchKey_returnsDespatchList() throws IOException, DatatypeConfigurationException { // GetDespatchAdvice
        GetDespatchAdviceRequest request = new GetDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetDespatchAdviceRequest.SEARCHKEY key = new GetDespatchAdviceRequest.SEARCHKEY();

        key.setDIRECTION("OUT");
        key.setSTARTDATE(DateUtils.minusDays(30));
        key.setENDDATE(DateUtils.now());

        /*
        key.setLIMIT(20);
        key.setCONTENTTYPE(CONTENTTYPE.XML);
        key.setDATETYPE(DATETYPE.CREATE);
        */

        /* Okunmuş belgeler isteniyorsa */
        key.setREADINCLUDED(true);

        /* Query with ID */
        // key.setUUID("552f87b8-aa28-42bc-a326-7da282976cda");

        request.setSEARCHKEY(key);

        GetDespatchAdviceResponse resp = edespatchAdapter.getDespatchAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        despatchadvices = resp.getDESPATCHADVICE();

        System.out.println(resp.getDESPATCHADVICE().get(0).getID());
    }

    private void loadDespatchAdvice_givenDespatchAdviceContent_then_canLoadDraftDespatchAdvice() throws IOException { // LoadDespatchAdvice
        LoadDespatchAdviceRequest request = new LoadDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        DESPATCHADVICE despatch = new DESPATCHADVICE();
        Base64Binary b64 = new Base64Binary();

        // ID
        String id = IdentifierUtils.createInvoiceIdRandom("EIR");
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-edespatch.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        b64.setValue(Files.readAllBytes(createdXml.toPath()));
        despatch.setCONTENT(b64);

        DESPATCHADVICEHEADER despatchHeader = new DESPATCHADVICEHEADER();
        despatch.setDESPATCHADVICEHEADER(despatchHeader);

        request.getDESPATCHADVICE().add(despatch);

        LoadDespatchAdviceResponse resp = edespatchAdapter.loadDespatchAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        loadDespatchAdviceUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    private void sendDespatchAdvice_givenValidDestpachAdvice_then_canSendDespatchAdvice() throws IOException { // SendDespatchAdvice
        SendDespatchAdviceRequest request = new SendDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        SendDespatchAdviceRequest.RECEIVER receiver = new SendDespatchAdviceRequest.RECEIVER();
        receiver.setVkn("4840847211");
        receiver.setAlias("urn:mail:defaultgb@izibiz.com.tr");

        request.setRECEIVER(receiver);

        DESPATCHADVICE despatch = new DESPATCHADVICE();
        Base64Binary b64 = new Base64Binary();

        //ID
        String id = IdentifierUtils.createInvoiceIdRandom("EIR");
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-edespatch.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        b64.setValue(Files.readAllBytes(createdXml.toPath()));
        despatch.setCONTENT(b64);

        DESPATCHADVICEHEADER despatchHeader = new DESPATCHADVICEHEADER();
        despatch.setDESPATCHADVICEHEADER(despatchHeader);

        request.getDESPATCHADVICE().add(despatch);

        SendDespatchAdviceResponse resp = edespatchAdapter.sendDespatchAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendDespatchAdviceUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    private void markDespatchAdvice_givenDespatchUUID_andGivenAction_marksDespatchAdvice() { // markDespatchAdvice
        MarkDespatchAdviceRequest request = new MarkDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        MarkDespatchAdviceRequest.MARK mark = new MarkDespatchAdviceRequest.MARK();
        mark.setValue("UNREAD");

        DESPATCHADVICEINFO info = new DESPATCHADVICEINFO();
        info.setUUID(sendDespatchAdviceUUID); // UUID ile istek gönderme

        mark.getDESPATCHADVICEINFO().add(info);

        request.setMARK(mark);

        MarkDespatchAdviceResponse resp = edespatchAdapter.markDespatchAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    // referans e-irsaliye belgesinin yüklenmesinin üzerinden 7 gün geçtiği için hata döndürülmektedir
    // güncel bir taslak.xml ile çalıştırılmalıdır.
    private void sendReceiptAdvice_givenValidContent_then_canSendReceiptAdvice() throws IOException { // sendReceiptAdvice
        SendReceiptAdviceRequest request = new SendReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("N");
        request.setREQUESTHEADER(header);

        //ID
        String id = IdentifierUtils.createInvoiceIdRandom("EIR");
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-receiptAdvice.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(createdXml.toPath()));

        RECEIPTADVICE receipt = new RECEIPTADVICE();
        receipt.setUUID(UUID.randomUUID().toString());


        RECEIPTADVICEHEADER receiptHeader = new RECEIPTADVICEHEADER();

        receipt.setRECEIPTADVICEHEADER(receiptHeader);

        request.getRECEIPTADVICE().add(receipt);

        SendReceiptAdviceResponse resp = edespatchAdapter.sendReceiptAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendReceiptAdviceUUID = uuid.toString();

        System.out.println(resp.getRECEIPTID());
    }

    private void loadReceiptAdvice_givenValidContent_thenSendsDespatchAdviceAsDraft() throws IOException { // loadReceiptAdvice
        LoadReceiptAdviceRequest request = new LoadReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("N"); // "Y" if sending a zipped file
        request.setREQUESTHEADER(header);

        //ID
        String id = IdentifierUtils.createInvoiceIdRandom("DMY");
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-receiptAdvice.xml");
        File created = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(created.toPath()));

        RECEIPTADVICE receiptadvice = new RECEIPTADVICE();
        receiptadvice.setCONTENT(b64);

        request.getRECEIPTADVICE().add(receiptadvice);

        LoadReceiptAdviceResponse resp = edespatchAdapter.loadReceiptAdvice(request);

        created.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        loadReceiptAdviceUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    private void getReceiptAdvice_canGetReceiptList_withGivenParameters() throws DatatypeConfigurationException { // getReceiptAdvice
        GetReceiptAdviceRequest request = new GetReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetReceiptAdviceRequest.SEARCHKEY key = new GetReceiptAdviceRequest.SEARCHKEY();

        key.setSTARTDATE(DateUtils.minusDays(30));
        key.setENDDATE(DateUtils.now());

        key.setDIRECTION("OUT");
        key.setREADINCLUDED(true);
        key.setCONTENTTYPE(CONTENTTYPE.XML);

        request.setSEARCHKEY(key);

        GetReceiptAdviceResponse resp = edespatchAdapter.getReceiptAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        receiptadvices = resp.getRECEIPTADVICE();

        System.out.println(resp.getRECEIPTADVICE().get(0).getID());
    }

    private void getReceiptAdviceStatus_givenUUID_returnsReceiptStatus() { // getReceiptAdviceStatus
        GetReceiptAdviceStatusRequest request = new GetReceiptAdviceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(receiptadvices.stream().map(r -> r.getUUID()).collect(Collectors.toList()));

        GetReceiptAdviceStatusResponse resp = edespatchAdapter.getReceiptAdviceStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getRECEIPTADVICESTATUS().get(0).getID());
    }

    private void markReceiptAdvice_marksReceipts_withGivenParameters() { // markReceiptAdvice
        MarkReceiptAdviceRequest request = new MarkReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        RECEIPTADVICEINFO info = new RECEIPTADVICE();
        info.setUUID(receiptadvices.get(0).getUUID()); // Query with ID

        MarkReceiptAdviceRequest.MARK mark = new MarkReceiptAdviceRequest.MARK();
        mark.setValue("READ");
        mark.getRECEIPTADVICEINFO().add(info);

        request.setMARK(mark);

        MarkReceiptAdviceResponse resp = edespatchAdapter.markReceiptAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

}
