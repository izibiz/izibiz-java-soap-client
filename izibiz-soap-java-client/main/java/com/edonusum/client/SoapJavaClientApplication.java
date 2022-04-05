package com.edonusum.client;

import com.edonusum.client.adapter.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.edonusum.client"})
public class SoapJavaClientApplication {
    private AuthAdapter authAdapter;
    private EinvoiceAdapter einvoiceAdapter;
    private EiarchiveAdapter eiarchiveAdapter;
    private EdespatchAdapter edespatchAdapter;
    private CreditNoteAdapter creditNoteAdapter;
    private SmmAdapter smmAdapter;

    public SoapJavaClientApplication() {
        this.authAdapter = new AuthAdapter();
        this.eiarchiveAdapter = new EiarchiveAdapter();
        this.einvoiceAdapter = new EinvoiceAdapter();
        this.edespatchAdapter = new EdespatchAdapter();
        this.creditNoteAdapter = new CreditNoteAdapter();
        this.smmAdapter = new SmmAdapter();

        //TODO: declarations
    }

    public static void main(String[] args) {
        SpringApplication.run(SoapJavaClientApplication.class,args);
    }


    public AuthAdapter authWS() {
        return this.authAdapter;
    }

    public EinvoiceAdapter einvoiceWS() {
        return this.einvoiceAdapter;
    }

    public EiarchiveAdapter eiarchiveWS() {
        return this.eiarchiveAdapter;
    }

    public EdespatchAdapter despatchAdviceWS() {
        return this.edespatchAdapter;
    }

    public CreditNoteAdapter creditNoteWS() {
        return this. creditNoteAdapter;
    }

    public SmmAdapter smmWS() {
        return this.smmAdapter;
    }
}
