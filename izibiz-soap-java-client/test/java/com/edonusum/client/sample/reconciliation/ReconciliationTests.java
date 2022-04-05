package com.edonusum.client.sample.reconciliation;

import com.edonusum.client.adapter.ReconciliationAdapter;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.wsdl.reconciliation.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@DisplayName("E-Mutabakat servisi")
@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReconciliationTests {

    private static String SESSION_ID;
    private static String sendReconciliatonUUID;

    @Autowired
    private ReconciliationAdapter reconciliationAdapter;

    @Order(1)
    @DisplayName("Giriş yapma")
    @Test
    public void login() {
        SESSION_ID = AuthTests.login();
    }

    @Order(2)
    @DisplayName("E-Mutabakat gönderme")
    @Test
    public void canSendReconciliation() {
        SendReconciliationRequest request = new SendReconciliationRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        SendReconciliationRequest.RECONCILIATION reconciliation = new SendReconciliationRequest.RECONCILIATION();
        reconciliation.setTYPE(RECONCILIATIONTYPE.CM); // Cari Mutabakat: CM , BA/BAS Mutabakat: EM
        reconciliation.setCUSTOMERIDENTIFIER("4840847211");
        reconciliation.setCOMMERCIALNAME("İzibiz Bilişim Teknolojileri A.Ş.");
        reconciliation.setCUSTOMERADDRESS("test");
        reconciliation.setCURRENCYCODE(CURRENCYCODE.USD);
        reconciliation.setTAXOFFICE("DAVUTPAŞA");
        reconciliation.setUUID(UUID.randomUUID().toString());

        // Cari Mutabakat için zorunlu alanlar
        reconciliation.setCMAMOUNT(BigDecimal.valueOf(5001));
        reconciliation.setCMAMOUNTTYPE(CMTYPE.B);
        reconciliation.setCMDATE(LocalDate.now().toString());

        reconciliation.setEMAIL("rtdonuk@gmail.com");

        request.getRECONCILIATION().add(reconciliation);

        SendReconciliationResponse resp = reconciliationAdapter.sendReconciliation(request);

        sendReconciliatonUUID = reconciliation.getUUID();

        Assertions.assertNull(resp.getERRORTYPE());
        Assertions.assertEquals(0, resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Order(3)
    @DisplayName("E-Mutabakat durum sorgulama")
    @Test
    public void canGetReconciliationStatus() {
        GetReconciliationStatusRequest request = new GetReconciliationStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetReconciliationStatusRequest.RECONCILIATIONSEARCHING key = new GetReconciliationStatusRequest.RECONCILIATIONSEARCHING();
        key.getUUID().add(sendReconciliatonUUID);

        request.setRECONCILIATIONSEARCHING(key);

        GetReconciliationStatusResponse resp = reconciliationAdapter.getReconciliationStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());
        Assertions.assertEquals(0, resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Order(4)
    @DisplayName("E-Mutabakat mail gönderme")
    @Test
    public void canSendMailReconciliation() {
        SendMailReconciliationRequest request = new SendMailReconciliationRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        SendMailReconciliationRequest.MAILSEARCHING key = new SendMailReconciliationRequest.MAILSEARCHING();
        key.getUUID().add(sendReconciliatonUUID);

        request.setMAILSEARCHING(key);

        SendMailReconciliationResponse resp = reconciliationAdapter.sendReconciliationMail(request);

        Assertions.assertNull(resp.getERRORTYPE());
        Assertions.assertEquals(0, resp.getREQUESTRETURN().getRETURNCODE());
    }

    @Order(5)
    @DisplayName("Çıkış yapma")
    @Test
    public void logout() {
        AuthTests.logout(SESSION_ID);

        SESSION_ID = "";
    }
}
