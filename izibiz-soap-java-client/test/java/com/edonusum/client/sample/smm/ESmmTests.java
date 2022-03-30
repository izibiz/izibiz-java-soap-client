package com.edonusum.client.sample.smm;

import com.edonusum.client.SoapJavaClientApplication;
import com.edonusum.client.sample.auth.AuthTests;
import com.edonusum.client.util.DateUtils;
import com.edonusum.client.wsdl.smm.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class ESmmTests {
    private SoapJavaClientApplication client = new SoapJavaClientApplication();

    private static String loadSmmUUID = "";
    private static String sendSmmUUID = "";

    private static String SESSION_ID = "";

    private List<SMM> smmList;
    private List<REPORT> smmReports;

    @Test
    public void runAllTestsWithOrder() throws Exception{
        // login
        login();

        // getSmm
        getSmm_canGetSmmList_withGivenParameters();

        // getSmmStatus
        getSmmStatus_givenValidUUID_then_returnsSmmStatus();

        // getSmmReport
        //getSmmReport_givenTimePeriod_then_returnsReportList();

        // cancelSmm
        cancelSmm_givenUUID_canCancelSmm();

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

    private void getSmm_canGetSmmList_withGivenParameters() throws Exception{ // getSmm
        GetSmmRequest request = new GetSmmRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        GetSmmRequest.SMMSEARCHKEY key = new GetSmmRequest.SMMSEARCHKEY();
        // tarihe göre sorgu
        key.setENDDATE(DateUtils.now());
        key.setSTARTDATE(DateUtils.minusDays(30));

        /* UUID ile sorgu */
        // key.setUUID("example");

        /* okunmuş belgeleri alma */
        key.setREADINCLUDED(FLAGVALUE.Y);

        request.setSMMSEARCHKEY(key);
        request.setCONTENTTYPE(CONTENTTYPE.XML);
        request.setHEADERONLY(FLAGVALUE.N);

        GetSmmResponse resp = client.smmWS().getSmm(request);

        Assertions.assertNull(resp.getERRORTYPE());

        smmList = resp.getSMM();

        System.out.println(resp.getSMM().get(0).getID());
    }

    private void getSmmStatus_givenValidUUID_then_returnsSmmStatus() { // getSmmStatus
        GetSmmStatusRequest request = new GetSmmStatusRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(smmList.stream().map(s -> s.getUUID()).collect(Collectors.toList()));

        GetSmmStatusResponse resp = client.smmWS().getSmmStatus(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getSMMSTATUS().get(0).getHEADER().getSTATUS());
    }

    private void getSmmReport_givenTimePeriod_then_returnsReportList() throws Exception{ // getSmmReport
        GetSmmReportRequest request = new GetSmmReportRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.setHEADERONLY(FLAGVALUE.N);

        request.setSTARTDATE(DateUtils.minusDays(30));
        request.setENDDATE(DateUtils.now());

        GetSmmReportResponse resp = client.smmWS().getSmmReport(request);

        Assertions.assertNull(resp.getERRORTYPE());

        smmReports = resp.getSMMREPORT();

        System.out.println(resp.getSMMREPORT().get(0).getHEADER().getSTATUS());
    }

    private void loadSmm() { // loadSmm

    }

    private void sendSmm() { // sendSmm

    }

    private void cancelSmm_givenUUID_canCancelSmm() { // cancelSMm
        CancelSmmRequest request = new CancelSmmRequest();
        REQUESTHEADERType header = new REQUESTHEADERType();

        header.setSESSIONID(SESSION_ID);
        request.setREQUESTHEADER(header);

        request.getUUID().addAll(smmList.stream().map(s -> s.getUUID()).collect(Collectors.toList()));

        CancelSmmResponse resp = client.smmWS().cancelSmm(request);

        Assertions.assertNull(resp.getERRORTYPE());

        System.out.println(resp.getREQUESTRETURN().getRETURNCODE());
    }
}
