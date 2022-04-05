package com.edonusum.client.sample.smm;

import com.edonusum.client.adapter.SmmAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import com.edonusum.client.util.XMLUtils;
import com.edonusum.client.wsdl.smm.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@DisplayName("E-SMM Servisi")
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ESmmTests {
    
    @Autowired
    private SmmAdapter adapter;

    private static String loadSmmUUID = "";
    private static String sendSmmUUID = "";

    private static String SESSION_ID = "";

    private static List<SMM> smmList;
    private static List<REPORT> smmReports;


    @Test
    @Order(1)
    @DisplayName("Giriş yapma")
    public void login() {
        SESSION_ID = AuthTests.login();
    }

    @Test
    @Order(2)
    @DisplayName("Serbest meslek makbuz listesi çekme")
    public void getSmm_canGetSmmList_withGivenParameters() throws Exception{ // getSmm
        GetSmmRequest request = new GetSmmRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetSmmRequest.SMMSEARCHKEY searchKey = new GetSmmRequest.SMMSEARCHKEY();
        // tarihe göre sorgu
        searchKey.setENDDATE(DateUtils.now());
        searchKey.setSTARTDATE(DateUtils.minusDays(30));

        /* UUID ile sorgu */
        // searchKey.setUUID("example");

        /* okunmuş belgeleri alma */
        searchKey.setREADINCLUDED(FLAGVALUE.Y);

        request.setSMMSEARCHKEY(searchKey);
        request.setCONTENTTYPE(CONTENTTYPE.XML);
        request.setHEADERONLY(FLAGVALUE.N);

        GetSmmResponse resp = adapter.getSmm(request);

        Assertions.assertNull(resp.getERRORTYPE());

        smmList = resp.getSMM();

        System.out.println(resp.getSMM().get(0).getID());
    }

    @Test
    @Order(3)
    @DisplayName("Taslak e-serbest meslek makbuzu yükleme")
    public void loadSmm_givenValidDraftSmm_then_loadsSmmAsDraft() throws Exception{ // loadSmm
        LoadSmmRequest request = new LoadSmmRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");
        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        // props
        SMMPROPERTIES smmProperties = new SMMPROPERTIES();
        smmProperties.setSENDINGTYPE(SENDINGTYPE.KAGIT);
        smmProperties.setSMSFLAG(FLAGVALUE.N);
        smmProperties.setEMAILFLAG(FLAGVALUE.N);

        request.setSMMPROPERTIES(smmProperties);

        // id
        String id = IdentifierUtils.createInvoiceIdRandom("X01");
        UUID uuid = UUID.randomUUID();

        // preparing the content
        File draft = new File("xml\\draft-ESmm.xml");
        File created = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(created.toPath()));

        SMM smmToLoad = new SMM();
        smmToLoad.setCONTENT(base64Binary);

        request.getSMM().add(smmToLoad);

        LoadSmmResponse resp = adapter.loadSmm(request);

        Assertions.assertNull(resp.getERRORTYPE());
        Assertions.assertNotNull(resp.getREQUESTRETURN());

        loadSmmUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    // Gönderen VKN ile session alan kullanıcı aynı değil
    @Test
    @Order(4)
    @DisplayName("Serbest meslek makbuzu gönderme")
    public void sendSmm() throws Exception{ // sendSmm
        SendSmmRequest request = new SendSmmRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setCOMPRESSED("N");
        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        // props
        SMMPROPERTIES smmProperties = new SMMPROPERTIES();
        smmProperties.setSENDINGTYPE(SENDINGTYPE.KAGIT);
        smmProperties.setSMSFLAG(FLAGVALUE.N);
        smmProperties.setEMAILFLAG(FLAGVALUE.N);

        request.setSMMPROPERTIES(smmProperties);

        // id
        String id = IdentifierUtils.createInvoiceIdRandom("X01");
        UUID uuid = UUID.randomUUID();

        // preparing the content
        File draft = new File("xml\\draft-ESmm.xml");
        File created = XMLUtils.createXmlFromDraftInvoice(draft, uuid, id);

        Base64Binary base64Binary = new Base64Binary();
        base64Binary.setValue(Files.readAllBytes(created.toPath()));

        SMM smmToSend = new SMM();
        smmToSend.setCONTENT(base64Binary);

        request.getSMM().add(smmToSend);

        SendSmmResponse resp = adapter.sendSmm(request);

        Assertions.assertNull(resp.getERRORTYPE());
        Assertions.assertNotNull(resp.getREQUESTRETURN());

        sendSmmUUID = uuid.toString();

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(5)
    @DisplayName("Serbest meslek makbuzu durum sorgulama")
    public void getSmmStatus_givenValidUUID_then_returnsSmmStatus() { // getSmmStatus
        GetSmmStatusRequest request = new GetSmmStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().add(loadSmmUUID);

        GetSmmStatusResponse resp = adapter.getSmmStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getSMMSTATUS().get(0).getHEADER().getSTATUS());
    }

    @Test
    @Order(6)
    @DisplayName("Serbest meslek makbuzu rapor listesi çekme")
    public void getSmmReport_givenTimePeriod_then_returnsReportList() throws Exception{ // getSmmReport
        GetSmmReportRequest request = new GetSmmReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setHEADERONLY(FLAGVALUE.N);

        request.setSTARTDATE(DateUtils.minusDays(30));
        request.setENDDATE(DateUtils.now());

        GetSmmReportResponse resp = adapter.getSmmReport(request);

        Assertions.assertNull(resp.getERRORTYPE());

        smmReports = resp.getSMMREPORT();

        System.out.println(resp.getSMMREPORT().get(0).getHEADER().getSTATUS());
    }

    @Test
    @Order(7)
    @DisplayName("Serbest meslek makbuzu iptali")
    public void cancelSmm_givenUUID_canCancelSmm() { // cancelSMm
        CancelSmmRequest request = new CancelSmmRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().add(loadSmmUUID);

        CancelSmmResponse resp = adapter.cancelSmm(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Test
    @Order(8)
    @DisplayName("Çıkış yapma")
    public void logout() {
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }

}
