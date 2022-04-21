package com.edonusum.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClientTests {

    @Test
    void contextLoads() {
    }

    @Test
    void canInitializeClient() {
        SoapJavaClientApplication client = new SoapJavaClientApplication();

        Assertions.assertNotNull(client.authWS());
        Assertions.assertNotNull(client.einvoiceWS());
        Assertions.assertNotNull(client.eiarchiveWS());
        Assertions.assertNotNull(client.smmWS());
        Assertions.assertNotNull(client.creditNoteWS());
        Assertions.assertNotNull(client.despatchAdviceWS());
        Assertions.assertNotNull(client.reconciliationWS());

    }

}
