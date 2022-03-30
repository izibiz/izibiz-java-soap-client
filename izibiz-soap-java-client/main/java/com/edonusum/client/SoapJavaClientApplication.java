package com.edonusum.client;

import com.edonusum.client.adapter.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoapJavaClientApplication {
    private AuthAdapter authAdapter;
    private EinvoiceAdapter einvoiceAdapter;
    private EiarchiveAdapter eiarchiveAdapter;
    private EdespatchAdapter edespatchAdapter;
    private CreditNoteAdapter creditNoteAdapter;

    public SoapJavaClientApplication() {
        this.authAdapter = new AuthAdapter();
        this.eiarchiveAdapter = new EiarchiveAdapter();
        this.einvoiceAdapter = new EinvoiceAdapter();
        this.edespatchAdapter = new EdespatchAdapter();
        this.creditNoteAdapter = new CreditNoteAdapter();

        //TODO: declarations
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
}
