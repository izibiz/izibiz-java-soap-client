package com.edonusum.client;

import com.edonusum.client.adapter.AuthAdapter;
import com.edonusum.client.adapter.EiarchiveAdapter;
import com.edonusum.client.adapter.EinvoiceAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoapJavaClientApplication {
    private AuthAdapter authAdapter;
    private EinvoiceAdapter einvoiceAdapter;
    private EiarchiveAdapter eiarchiveAdapter;

    public SoapJavaClientApplication() {
        this.authAdapter = new AuthAdapter();
        this.eiarchiveAdapter = new EiarchiveAdapter();
        this.einvoiceAdapter = new EinvoiceAdapter();

        //TODO: declarations
    }

    public AuthAdapter auth() {
        return this.authAdapter;
    }

    public EinvoiceAdapter einvoiceWS() {
        return this.einvoiceAdapter;
    }

    public EiarchiveAdapter eiarchiveWS() {
        return this.eiarchiveAdapter;
    }
}
