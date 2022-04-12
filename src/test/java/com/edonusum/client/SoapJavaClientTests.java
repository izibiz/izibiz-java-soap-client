package com.edonusum.client;

import com.edonusum.client.ubl.ReceiptAdviceUBL;
import com.edonusum.client.ubl.Xslt;
import com.edonusum.client.util.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@SpringBootTest
class SoapJavaClientTests {

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
