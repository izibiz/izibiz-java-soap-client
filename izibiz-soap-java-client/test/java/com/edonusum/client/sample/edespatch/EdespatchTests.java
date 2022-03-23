package com.edonusum.client.sample.edespatch;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.adapter.EdespatchAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.edespatch.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
    public void getDespatchAdvice_givenSearchKey_returnsDespatchList() throws IOException { // GetDespatchAdvice
        GetDespatchAdviceRequest request = new GetDespatchAdviceRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(getSessionId());
        request.setREQUESTHEADER(header);

        GetDespatchAdviceRequest.SEARCHKEY key = new GetDespatchAdviceRequest.SEARCHKEY();

        key.setDIRECTION("OUT");
        key.setUUID("552f87b8-aa28-42bc-a326-7da282976cda"); // example id

        // key.setUUID("UUID");
        // key.setDIRECTION("IN");

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
    public void sendDespatchAdvice_givenValidDestpachAdvice_then_canSendDespatchAdvice() throws IOException { // LoadDespatchAdvice
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

        SendDespatchAdviceResponse resp = edespatchAdapter.sendDespatchAdvice(request);

        createdXml.delete();

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }
}
