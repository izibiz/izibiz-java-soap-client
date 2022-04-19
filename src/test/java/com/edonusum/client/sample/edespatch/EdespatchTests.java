package com.edonusum.client.sample.edespatch;

import com.edonusum.client.adapter.EdespatchAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.ubl.DespatchAdviceUBL;
import com.edonusum.client.ubl.ReceiptAdviceUBL;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.edespatch.*;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.receiptadvice_2.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.receiptadvice_2.ReceiptAdviceType;
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
@DisplayName("E-İrsaliye servisi")
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EdespatchTests {

    @Autowired
    private EdespatchAdapter adapter;

    private static DespatchAdviceType lastSentDespatchAdvice;

    private static String sendReceiptAdviceUUID = "";

    private static List<DESPATCHADVICE> despatchAdvices;
    private static List<RECEIPTADVICE> receiptAdvices;
    
    private static String SESSION_ID;

    @Test
    @Order(1)
    @DisplayName("Giriş yapma")
    void login() {
        SESSION_ID = AuthTests.login();

        Assertions.assertNotEquals("", SESSION_ID);
    }

    @Test
    @Order(2)
    @DisplayName("E-İrsaliye listesi çekme")
    void canGetDespatchAdviceList() throws Exception { // GetDespatchAdvice
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

        /* bkz: dev.izibiz.com.tr
        searchKey.setLIMIT(20);
        searchKey.setDATETYPE(DATETYPE.CREATE);
        */

        /* Okunmuş belgeler isteniyorsa */
        searchKey.setREADINCLUDED(true);

        /* UUID değeri ile sorgulama */
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
    void canGetReceiptAdviceList() throws Exception { // getReceiptAdvice
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
        searchKey.setLIMIT(100);
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
    void canLoadDespatchAdvice() throws IOException { // LoadDespatchAdvice
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

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(5)
    @DisplayName("E-İrsaliye gönderme")
    void canSendDespatchAdvice() throws Exception { // SendDespatchAdvice
        SendDespatchAdviceRequest request = new SendDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        SendDespatchAdviceRequest.RECEIVER receiver = new SendDespatchAdviceRequest.RECEIVER();
        receiver.setVkn("4840847211");
        receiver.setAlias("urn:mail:defaultgb@izibiz.com.tr");

        request.setRECEIVER(receiver);

        //ID
        String id = IdentifierUtils.createInvoiceIdRandomPrefix();
        UUID uuid = UUID.randomUUID();

        oasis.names.specification.ubl.schema.xsd.despatchadvice_2.ObjectFactory factory = new oasis.names.specification.ubl.schema.xsd.despatchadvice_2.ObjectFactory();

        File file = new File(System.getProperty("user.home")+"\\Desktop\\x.xml");
        DespatchAdviceType despatchAdvice = new DespatchAdviceUBL(id, uuid.toString()).getDespatchAdvice(); // create from UBL
        JAXB.marshal(factory.createDespatchAdvice(despatchAdvice), file);

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(file.toPath()));

        file.delete();

        DESPATCHADVICE despatch = new DESPATCHADVICE();
        DESPATCHADVICEHEADER despatchHeader = new DESPATCHADVICEHEADER();

        despatch.setDESPATCHADVICEHEADER(despatchHeader);
        despatch.setCONTENT(base64Binary);

        request.getDESPATCHADVICE().add(despatch);

        SendDespatchAdviceResponse resp = adapter.sendDespatchAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        lastSentDespatchAdvice = despatchAdvice;

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(6)
    @DisplayName("Taslak E-İrsaliye yanıtı yükleme")
    void canLoadReceiptAdvice() throws IOException { // loadReceiptAdvice
        LoadReceiptAdviceRequest request = new LoadReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("N"); // zipli dosya gönderiliyorsa "Y" verilmeli
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

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    // referans e-irsaliye belgesinin yüklenmesinin üzerinden 7 gün geçtiği için hata döndürülmektedir
    // güncel bir taslak.xml ile çalıştırılmalıdır.
    @Test
    @Order(7)
    @DisplayName("E-irsaliye yanıtı gönderme")
    void canSendReceiptAdvice() throws Exception { // sendReceiptAdvice
        SendReceiptAdviceRequest request = new SendReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        header.setCOMPRESSED("N");
        request.setREQUESTHEADER(header);

        //ID
        String id = lastSentDespatchAdvice.getID().getValue();
        String uuid = lastSentDespatchAdvice.getUUID().getValue();

        ObjectFactory factory = new ObjectFactory();

        File file = new File(System.getProperty("user.home")+"\\Desktop\\x.xml");
        ReceiptAdviceType receipt = new ReceiptAdviceUBL(id, uuid,lastSentDespatchAdvice.getShipment().getDelivery().getDespatch().getActualDespatchDate().getValue().toString()).getReceiptAdvice(); // create from UBL
        JAXB.marshal(factory.createReceiptAdvice(receipt), file);

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(file.toPath()));

        RECEIPTADVICE receiptAdvice = new RECEIPTADVICE();
        receiptAdvice.setUUID(UUID.randomUUID().toString());
        receiptAdvice.setCONTENT(base64Binary);

        RECEIPTADVICEHEADER receiptHeader = new RECEIPTADVICEHEADER();

        receiptAdvice.setRECEIPTADVICEHEADER(receiptHeader);

        request.getRECEIPTADVICE().add(receiptAdvice);

        SendReceiptAdviceResponse resp = adapter.sendReceiptAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        sendReceiptAdviceUUID = uuid;

        System.out.println(resp.getRECEIPTID());
    }

    @Test
    @Order(8)
    @DisplayName("E-İrsaliye durum sorgulama")
    void canGetDespatchAdviceStatus() { // GetDespatchAdviceStatus
        GetDespatchAdviceStatusRequest request = new GetDespatchAdviceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(despatchAdvices.stream().map(DESPATCHADVICEINFO::getUUID).collect(Collectors.toList())); // toplu status sorgulama

        GetDespatchAdviceStatusResponse resp = adapter.getDespatchAdviseStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getDESPATCHADVICESTATUS().get(0).getSTATUS());
    }

    @Test
    @Order(9)
    @DisplayName("E-İrsaliye yanıtı durumu sorgulama")
    void canGetReceiptAdviceStatus() { // getReceiptAdviceStatus
        GetReceiptAdviceStatusRequest request = new GetReceiptAdviceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(receiptAdvices.stream().map(RECEIPTADVICEINFO::getUUID).collect(Collectors.toList()));

        GetReceiptAdviceStatusResponse resp = adapter.getReceiptAdviceStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getRECEIPTADVICESTATUS().get(0).getID());
    }

    @Test
    @Order(10)
    @DisplayName("E-İrsaliye işaretleme")
    void canMarkDespatchAdvice() { // markDespatchAdvice
        MarkDespatchAdviceRequest request = new MarkDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        MarkDespatchAdviceRequest.MARK mark = new MarkDespatchAdviceRequest.MARK();
        mark.setValue("UNREAD");

        DESPATCHADVICEINFO info = new DESPATCHADVICEINFO();
        info.setUUID(lastSentDespatchAdvice.getUUID().getValue()); // UUID ile istek gönderme

        mark.getDESPATCHADVICEINFO().add(info);

        request.setMARK(mark);

        MarkDespatchAdviceResponse resp = adapter.markDespatchAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(11)
    @DisplayName("E-İrsaliye yanıtı işaretleme")
    void canMarkReceiptAdvice() { // markReceiptAdvice
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
    @DisplayName("Çıkış yapma")
    private void logout() {
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

}
