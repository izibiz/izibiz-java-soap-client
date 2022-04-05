package com.edonusum.client.sample.edespatch;

import com.edonusum.client.adapter.EdespatchAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.edespatch.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
@DisplayName("E-İrsaliye servisi")
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EdespatchTests {

    @Autowired
    private EdespatchAdapter adapter;

    private static String loadDespatchAdviceUUID = "";
    private static String loadReceiptAdviceUUID = "";
    private static String sendDespatchAdviceUUID = "";
    private static String sendReceiptAdviceUUID = "";

    private static List<DESPATCHADVICE> despatchAdvices;
    private static List<RECEIPTADVICE> receiptAdvices;
    
    private static String SESSION_ID;

    @Test
    @Order(1)
    @DisplayName("Giriş yapma")
    public void login() {
        SESSION_ID = AuthTests.login();
    }

    @Test
    @Order(2)
    @DisplayName("E-İrsaliye listesi çekme")
    public void getDespatchAdvice_givenSearchKey_returnsDespatchList() throws Exception { // GetDespatchAdvice
        GetDespatchAdviceRequest request = new GetDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetDespatchAdviceRequest.SEARCHKEY searchKey = new GetDespatchAdviceRequest.SEARCHKEY();

        searchKey.setDIRECTION("OUT");
        searchKey.setSTARTDATE(DateUtils.minusDays(30));
        searchKey.setENDDATE(DateUtils.now());
        searchKey.setCONTENTTYPE(CONTENTTYPE.XML);

        /* bkz: dev.izibiz
        searchKey.setLIMIT(20);
        searchKey.setCONTENTTYPE(CONTENTTYPE.XML);
        searchKey.setDATETYPE(DATETYPE.CREATE);
        */

        /* Okunmuş belgeler isteniyorsa */
        searchKey.setREADINCLUDED(true);

        /* Query with ID */
        // searchKey.setUUID("552f87b8-aa28-42bc-a326-7da282976cda");

        request.setSEARCHKEY(searchKey);

        GetDespatchAdviceResponse resp = adapter.getDespatchAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        despatchAdvices = resp.getDESPATCHADVICE();

        System.out.println(resp.getDESPATCHADVICE().get(0).getID());
    }

    @Test
    @Order(3)
    @DisplayName("E-İrsaliye yanıt listesi çekme")
    public void getReceiptAdvice_canGetReceiptList_withGivenParameters() throws Exception { // getReceiptAdvice
        GetReceiptAdviceRequest request = new GetReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetReceiptAdviceRequest.SEARCHKEY searchKey = new GetReceiptAdviceRequest.SEARCHKEY();

        searchKey.setSTARTDATE(DateUtils.minusDays(30));
        searchKey.setENDDATE(DateUtils.now());

        searchKey.setDIRECTION("OUT");
        searchKey.setREADINCLUDED(true);
        searchKey.setCONTENTTYPE(CONTENTTYPE.XML);
        // searchKey.setLIMIT(10);

        request.setSEARCHKEY(searchKey);

        header.setSESSIONID(AuthTests.login());
        GetReceiptAdviceResponse resp = adapter.getReceiptAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        receiptAdvices = resp.getRECEIPTADVICE();

        System.out.println(resp.getRECEIPTADVICE().get(0).getID());
    }

    @Test
    @Order(4)
    @DisplayName("Taslak E-İrsaliye yükleme")
    public void loadDespatchAdvice_givenDespatchAdviceContent_then_canLoadDraftDespatchAdvice() throws IOException { // LoadDespatchAdvice
        LoadDespatchAdviceRequest request = new LoadDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        DESPATCHADVICE despatch = new DESPATCHADVICE();
        Base64Binary base64Binary = new Base64Binary();

        // ID
        String id = IdentifierUtils.createInvoiceIdRandom("EIR");
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-edespatch.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        base64Binary.setValue(Files.readAllBytes(createdXml.toPath()));
        despatch.setCONTENT(base64Binary);

        DESPATCHADVICEHEADER despatchHeader = new DESPATCHADVICEHEADER();
        despatch.setDESPATCHADVICEHEADER(despatchHeader);

        request.getDESPATCHADVICE().add(despatch);

        LoadDespatchAdviceResponse resp = adapter.loadDespatchAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        loadDespatchAdviceUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(5)
    @DisplayName("Taslak E-İrsaliye yanıtı yükleme")
    public void loadReceiptAdvice_givenValidContent_thenSendsDespatchAdviceAsDraft() throws IOException { // loadReceiptAdvice
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

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(created.toPath()));

        RECEIPTADVICE receiptadvice = new RECEIPTADVICE();
        receiptadvice.setCONTENT(base64Binary);

        request.getRECEIPTADVICE().add(receiptadvice);

        LoadReceiptAdviceResponse resp = adapter.loadReceiptAdvice(request);

        created.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        loadReceiptAdviceUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(6)
    @DisplayName("E-İrsaliye gönderme")
    public void sendDespatchAdvice_givenValidDestpachAdvice_then_canSendDespatchAdvice() throws IOException { // SendDespatchAdvice
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
        Base64Binary base64Binary = new Base64Binary();

        //ID
        String id = IdentifierUtils.createInvoiceIdRandomPrefix();
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-edespatch.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        base64Binary.setValue(Files.readAllBytes(createdXml.toPath()));
        despatch.setCONTENT(base64Binary);

        DESPATCHADVICEHEADER despatchHeader = new DESPATCHADVICEHEADER();
        despatch.setDESPATCHADVICEHEADER(despatchHeader);

        request.getDESPATCHADVICE().add(despatch);

        SendDespatchAdviceResponse resp = adapter.sendDespatchAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendDespatchAdviceUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    // referans e-irsaliye belgesinin yüklenmesinin üzerinden 7 gün geçtiği için hata döndürülmektedir
    // güncel bir taslak.xml ile çalıştırılmalıdır.
    @Test
    @Order(7)
    @DisplayName("E-irsaliye yanıtı gönderme")
    public void sendReceiptAdvice_givenValidContent_then_canSendReceiptAdvice() throws IOException { // sendReceiptAdvice
        SendReceiptAdviceRequest request = new SendReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("Y");
        request.setREQUESTHEADER(header);

        //ID
        String id = IdentifierUtils.createInvoiceIdRandomPrefix();
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-receiptAdvice.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(createdXml.toPath()));

        RECEIPTADVICE receipt = new RECEIPTADVICE();
        receipt.setUUID(UUID.randomUUID().toString());


        RECEIPTADVICEHEADER receiptHeader = new RECEIPTADVICEHEADER();

        receipt.setRECEIPTADVICEHEADER(receiptHeader);

        request.getRECEIPTADVICE().add(receipt);

        SendReceiptAdviceResponse resp = adapter.sendReceiptAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        sendReceiptAdviceUUID = uuid.toString();

        System.out.println(resp.getRECEIPTID());
    }

    @Test
    @Order(8)
    @DisplayName("E-İrsaliye durum sorgulama")
    public void getDespatchAdviceStatus_givenEdespatchUUID_then_returnsStatus() { // GetDespatchAdviceStatus
        GetDespatchAdviceStatusRequest request = new GetDespatchAdviceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(despatchAdvices.stream().map(d -> d.getUUID()).collect(Collectors.toList())); // toplu status sorgulama

        GetDespatchAdviceStatusResponse resp = adapter.getDespatchAdviseStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getDESPATCHADVICESTATUS().get(0).getSTATUS());
    }

    @Test
    @Order(9)
    @DisplayName("E-İrsaliye yanıtı durumu sorgulama")
    public void getReceiptAdviceStatus_givenUUID_returnsReceiptStatus() { // getReceiptAdviceStatus
        GetReceiptAdviceStatusRequest request = new GetReceiptAdviceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(receiptAdvices.stream().map(r -> r.getUUID()).collect(Collectors.toList()));

        GetReceiptAdviceStatusResponse resp = adapter.getReceiptAdviceStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getRECEIPTADVICESTATUS().get(0).getID());
    }

    @Test
    @Order(10)
    @DisplayName("E-İrsaliye işaretleme")
    public void markDespatchAdvice_givenDespatchUUID_andGivenAction_marksDespatchAdvice() { // markDespatchAdvice
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

        MarkDespatchAdviceResponse resp = adapter.markDespatchAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(11)
    @DisplayName("E-İrsaliye yanıtı işaretleme")
    public void markReceiptAdvice_marksReceipts_withGivenParameters() { // markReceiptAdvice
        MarkReceiptAdviceRequest request = new MarkReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        RECEIPTADVICEINFO info = new RECEIPTADVICE();
        info.setUUID(receiptAdvices.get(0).getUUID()); // Query with ID

        MarkReceiptAdviceRequest.MARK mark = new MarkReceiptAdviceRequest.MARK();
        mark.setValue("READ");
        mark.getRECEIPTADVICEINFO().add(info);

        request.setMARK(mark);

        MarkReceiptAdviceResponse resp = adapter.markReceiptAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(12)
    @DisplayName("Giriş yapma")
    private void logout() {
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

}
