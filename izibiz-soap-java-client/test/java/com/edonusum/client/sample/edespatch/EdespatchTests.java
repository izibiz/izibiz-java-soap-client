package com.edonusum.client.sample.edespatch;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.adapter.EdespatchAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.edespatch.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
public class EdespatchTests {
    private AuthAdapter authAdapter = new AuthAdapter();
    private EdespatchAdapter edespatchAdapter = new EdespatchAdapter();

    private String getSessionId() {
        String sessionId = authAdapter.login(AuthTests.prepareLoginRequest()).getSESSIONID();
        return sessionId;
    }

    @Test
    public void getDespatchAdviceStatus_givenEdespatchUUID_then_returnsStatus() { // GetDespatchAdviceStatus
        GetDespatchAdviceStatusRequest request = new GetDespatchAdviceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        String exampleUUID = "552f87b8-aa28-42bc-a326-7da282976cda"; // example id

        request.getUUID().add(exampleUUID);

        GetDespatchAdviceStatusResponse resp = edespatchAdapter.getDespatchAdviseStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getDESPATCHADVICESTATUS().get(0).getSTATUS());
    }

    @Test
    public void getDespatchAdvice_givenSearchKey_returnsDespatchList() throws IOException, DatatypeConfigurationException { // GetDespatchAdvice
        GetDespatchAdviceRequest request = new GetDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GetDespatchAdviceRequest.SEARCHKEY key = new GetDespatchAdviceRequest.SEARCHKEY();

        key.setDIRECTION("OUT");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        XMLGregorianCalendar end = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        XMLGregorianCalendar start = (XMLGregorianCalendar) end.clone();
        start.setMonth(start.getMonth()-1); // take last 30 days

        key.setSTARTDATE(start);
        key.setENDDATE(end);

        /* Query with ID */
        // key.setUUID("552f87b8-aa28-42bc-a326-7da282976cda");

        request.setSEARCHKEY(key);

        GetDespatchAdviceResponse resp = edespatchAdapter.getDespatchAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getDESPATCHADVICE().get(0).getID());
    }

    @Test
    public void loadDespatchAdvice_givenDespatchAdviceContent_then_canLoadDraftDespatchAdvice() throws IOException { // LoadDespatchAdvice
        LoadDespatchAdviceRequest request = new LoadDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        DESPATCHADVICE despatch = new DESPATCHADVICE();
        Base64Binary b64 = new Base64Binary();

        // ID
        DecimalFormat formatter = new DecimalFormat("#000000000");
        Random random = new Random();
        long id = random.nextInt(999999999); // 9 haneli
        String despatchId = "EIR" + LocalDate.now().getYear() + formatter.format(id); // seri + yıl + 9 haneli id
        UUID uuid = UUID.randomUUID();

        File draft = new File("xml\\draft-edespatch.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, uuid, despatchId);

        b64.setValue(Files.readAllBytes(createdXml.toPath()));
        despatch.setCONTENT(b64);

        DESPATCHADVICEHEADER despatchHeader = new DESPATCHADVICEHEADER();
        despatch.setDESPATCHADVICEHEADER(despatchHeader);

        request.getDESPATCHADVICE().add(despatch);

        LoadDespatchAdviceResponse resp = edespatchAdapter.loadDespatchAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void sendDespatchAdvice_givenValidDestpachAdvice_then_canSendDespatchAdvice() throws IOException { // SendDespatchAdvice
        SendDespatchAdviceRequest request = new SendDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        SendDespatchAdviceRequest.RECEIVER receiver = new SendDespatchAdviceRequest.RECEIVER();
        receiver.setVkn("4840847211");
        receiver.setAlias("urn:mail:defaultgb@izibiz.com.tr");

        request.setRECEIVER(receiver);

        DESPATCHADVICE despatch = new DESPATCHADVICE();
        Base64Binary b64 = new Base64Binary();

        File draft = new File("xml\\draft-edespatch.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, UUID.randomUUID(), IdentifierUtils.createInvoiceIdRandom("EIR"));

        b64.setValue(Files.readAllBytes(createdXml.toPath()));
        despatch.setCONTENT(b64);

        DESPATCHADVICEHEADER despatchHeader = new DESPATCHADVICEHEADER();
        despatch.setDESPATCHADVICEHEADER(despatchHeader);

        request.getDESPATCHADVICE().add(despatch);

        SendDespatchAdviceResponse resp = edespatchAdapter.sendDespatchAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void markDespatchAdvice_givenDespatchUUID_andGivenAction_marksDespatchAdvice() { // markDespatchAdvice
        MarkDespatchAdviceRequest request = new MarkDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        MarkDespatchAdviceRequest.MARK mark = new MarkDespatchAdviceRequest.MARK();
        mark.setValue("READ");

        DESPATCHADVICEINFO info = new DESPATCHADVICEINFO();
        info.setUUID("552f87b8-aa28-42bc-a326-7da282976cda"); // Query with ID

        mark.getDESPATCHADVICEINFO().add(info);

        request.setMARK(mark);

        MarkDespatchAdviceResponse resp = edespatchAdapter.markDespatchAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    // referans e-irsaliye belgesinin yüklenmesinin üzerinden 7 gün geçtiği için hata döndürülmektedir
    // güncel bir taslak.xml ile çalıştırılmalıdır.
    @Test
    public void sendReceiptAdvice_givenValidContent_then_canSendReceiptAdvice() throws IOException { // sendReceiptAdvice
        SendReceiptAdviceRequest request = new SendReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        header.setCOMPRESSED("N");
        request.setREQUESTHEADER(header);

        File draft = new File("xml\\draft-receiptAdvice.xml");
        File createdXml = XMLUtils.createXmlFromDraftInvoice(draft, UUID.randomUUID(), IdentifierUtils.createInvoiceIdRandom("EIR"));

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(createdXml.toPath()));

        RECEIPTADVICE receipt = new RECEIPTADVICE();
        receipt.setUUID("a9a5363b-8fa3-4b09-9ebd-365fb9eeaba5");

        RECEIPTADVICEHEADER receiptHeader = new RECEIPTADVICEHEADER();

        receipt.setRECEIPTADVICEHEADER(receiptHeader);

        request.getRECEIPTADVICE().add(receipt);

        SendReceiptAdviceResponse resp = edespatchAdapter.sendReceiptAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getRECEIPTID());
    }

    @Test
    public void loadReceiptAdvice_givenValidContent_thenSendsDespatchAdviceAsDraft() throws IOException { // loadReceiptAdvice
        LoadReceiptAdviceRequest request = new LoadReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        header.setCOMPRESSED("N"); // "Y" if sending a zipped file
        request.setREQUESTHEADER(header);

        File draft = new File("xml\\draft-receiptAdvice.xml");
        File created = XMLUtils.createXmlFromDraftInvoice(draft, UUID.randomUUID(), IdentifierUtils.createInvoiceIdRandom("DMY"));

        Base64Binary b64 = new Base64Binary();
        b64.setValue(Files.readAllBytes(created.toPath()));

        RECEIPTADVICE receiptadvice = new RECEIPTADVICE();
        receiptadvice.setCONTENT(b64);

        request.getRECEIPTADVICE().add(receiptadvice);

        LoadReceiptAdviceResponse resp = edespatchAdapter.loadReceiptAdvice(request);

        created.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    public void getReceiptAdvice_canGetReceiptList_withGivenParameters() throws DatatypeConfigurationException { // getReceiptAdvice
        GetReceiptAdviceRequest request = new GetReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GetReceiptAdviceRequest.SEARCHKEY key = new GetReceiptAdviceRequest.SEARCHKEY();

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        XMLGregorianCalendar end = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        XMLGregorianCalendar start = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        start.setMonth(start.getMonth()-1); // take last 30 days

        key.setSTARTDATE(start);
        key.setENDDATE(end);

        key.setDIRECTION("OUT");

        key.setCONTENTTYPE(CONTENTTYPE.XML);

        request.setSEARCHKEY(key);

        GetReceiptAdviceResponse resp = edespatchAdapter.getReceiptAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getRECEIPTADVICE().get(0).getID());
    }

    @Test
    public void getReceiptAdviceStatus_givenUUID_returnsReceiptStatus() { // getReceiptAdviceStatus
        GetReceiptAdviceStatusRequest request = new GetReceiptAdviceStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        request.getUUID().add("a9a5363b-8fa3-4b09-9ebd-365fb9eeaba5");

        GetReceiptAdviceStatusResponse resp = edespatchAdapter.getReceiptAdviceStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getRECEIPTADVICESTATUS().get(0).getID());
    }

    @Test
    public void markReceiptAdvice_marksReceipts_withGivenParameters() {
        MarkReceiptAdviceRequest request = new MarkReceiptAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        RECEIPTADVICEINFO info = new RECEIPTADVICE();
        info.setUUID("a9a5363b-8fa3-4b09-9ebd-365fb9eeaba5"); // Query with ID

        MarkReceiptAdviceRequest.MARK mark = new MarkReceiptAdviceRequest.MARK();
        mark.setValue("READ");
        mark.getRECEIPTADVICEINFO().add(info);

        request.setMARK(mark);

        MarkReceiptAdviceResponse resp = edespatchAdapter.markReceiptAdvice(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }
}
