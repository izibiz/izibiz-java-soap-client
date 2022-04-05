package com.edonusum.client;

import com.edonusum.client.adapter.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.edonusum.client"})
public class SoapJavaClientApplication {
    private final AuthAdapter authAdapter;
    private final EinvoiceAdapter einvoiceAdapter;
    private final EiarchiveAdapter eiarchiveAdapter;
    private final EdespatchAdapter edespatchAdapter;
    private final CreditNoteAdapter creditNoteAdapter;
    private final SmmAdapter smmAdapter;
    private final ReconciliationAdapter reconciliationAdapter;

    public SoapJavaClientApplication() {
        this.authAdapter = new AuthAdapter();
        this.eiarchiveAdapter = new EiarchiveAdapter();
        this.einvoiceAdapter = new EinvoiceAdapter();
        this.edespatchAdapter = new EdespatchAdapter();
        this.creditNoteAdapter = new CreditNoteAdapter();
        this.smmAdapter = new SmmAdapter();
        this.reconciliationAdapter = new ReconciliationAdapter();

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

    public ReconciliationAdapter reconciliationWS() {
        return this.reconciliationAdapter;
    }

}
