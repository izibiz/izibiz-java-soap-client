package com.edonusum.client.ubl;

import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;

import java.math.BigDecimal;
import java.util.UUID;

public class CreditNoteUBL extends UBL{
    private final CreditNoteType creditNote;

    public CreditNoteUBL() throws Exception {
        creditNote = new CreditNoteType();

        createHeader();
        createSignature();
        createSupplierParty();
        createCustomerParty();
        createDelivery();
        taxTotal();
        legalMonetaryTotal();
        addCreditNoteLine();
        addGibTemplate(); // Xslt şablonu
    }

    public CreditNoteType getCreditNote() {
        return this.creditNote;
    }

    private void createHeader() throws Exception {
        creditNote.setUBLVersionID(ublVersion());
        creditNote.setCustomizationID(customizationID("TR1.2"));
        creditNote.setProfileID(profileId("EARSIVBELGE"));

        creditNote.setID(id(IdentifierUtils.createInvoiceIdRandomPrefix()));
        creditNote.setUUID(uuid(UUID.randomUUID().toString()));

        creditNote.setCopyIndicator(new CopyIndicatorType());
        creditNote.getCopyIndicator().setValue(false);

        creditNote.setIssueDate(issueDate());
        creditNote.setIssueTime(issueTime());

        creditNote.setCreditNoteTypeCode(new CreditNoteTypeCodeType());
        creditNote.getCreditNoteTypeCode().setValue("MUSTAHSILMAKBUZ");

        creditNote.getNote().add(note("Yalnız yirmiDört TL Elli kuruş."));
        creditNote.getNote().add(note("Bilgi: e-Müstahsil izni kapsamında oluşturulmuştur."));

        creditNote.setDocumentCurrencyCode(new DocumentCurrencyCodeType());
        creditNote.getDocumentCurrencyCode().setValue("TRY");

        creditNote.setLineCountNumeric(new LineCountNumericType());
        creditNote.getLineCountNumeric().setValue(BigDecimal.ONE);
    }

    private void createSignature() {
        creditNote.getSignature().add(signature(creditNote.getID().getValue()));
    }

    private void createSupplierParty() {
        SupplierPartyType supplierPartyType = new SupplierPartyType();
        supplierPartyType.setParty(party("İZİBİZ BİLİŞİM TEKNOLOJİLERİ AŞ", "İstanbul", "Esenler", "Esenler", "34065", "Türkiye", "DAVUTPAŞA", "11111111111","11111111111", "email@example.com", "4840847211"));

        creditNote.setAccountingSupplierParty(supplierPartyType);
    }

    private void createCustomerParty() {
        CustomerPartyType customer = new CustomerPartyType();
        customer.setParty(party("İZİBİZ BİLİŞİM TEKNOLOJİLERİ AŞ", "İstanbul", "Esenler", "Esenler", "34065", "Türkiye", "DAVUTPAŞA", "11111111111","11111111111", "email@example.com", "4840847211"));;

        creditNote.setAccountingCustomerParty(customer);
    }

    private void createDelivery() throws Exception{
        DeliveryType delivery = new DeliveryType();
        delivery.setActualDeliveryDate(new ActualDeliveryDateType());
        delivery.getActualDeliveryDate().setValue(DateUtils.now());

        creditNote.getDelivery().add(delivery);
    }

    private TaxTotalType taxTotal() {
        TaxTotalType taxTotal = new TaxTotalType();

        taxTotal.setTaxAmount(new TaxAmountType());
        taxTotal.getTaxAmount().setValue(BigDecimal.valueOf(0.5));
        taxTotal.getTaxAmount().setCurrencyID("TRY");

        TaxSubtotalType subTotal = new TaxSubtotalType();

        subTotal.setTaxableAmount(new TaxableAmountType());
        subTotal.getTaxableAmount().setValue(BigDecimal.valueOf(25));
        subTotal.getTaxableAmount().setCurrencyID("TRY");

        subTotal.setTaxAmount(new TaxAmountType());
        subTotal.getTaxAmount().setCurrencyID("TRY");
        subTotal.getTaxAmount().setValue(BigDecimal.valueOf(0.5));

        subTotal.setCalculationSequenceNumeric(new CalculationSequenceNumericType());
        subTotal.getCalculationSequenceNumeric().setValue(BigDecimal.ONE);

        subTotal.setPercent(new PercentType());
        subTotal.getPercent().setValue(BigDecimal.valueOf(2));
        subTotal.getPercent().setFormat("%");

        subTotal.setTaxCategory(new TaxCategoryType());
        subTotal.getTaxCategory().setTaxScheme(new TaxSchemeType());
        subTotal.getTaxCategory().getTaxScheme().setName(name("Bağkur(SGK_PRIM)"));
        subTotal.getTaxCategory().getTaxScheme().getName().setLanguageLocaleID("bağkur");
        subTotal.getTaxCategory().getTaxScheme().setTaxTypeCode(new TaxTypeCodeType());
        subTotal.getTaxCategory().getTaxScheme().getTaxTypeCode().setValue("SGK_PRIM");

        taxTotal.getTaxSubtotal().add(subTotal);

        creditNote.getTaxTotal().add(taxTotal);

        return taxTotal;
    }

    private void legalMonetaryTotal() {
        MonetaryTotalType monetaryTotal = legalMonetaryTotal(25, 25, 24.5, 24.5,0);

        creditNote.setLegalMonetaryTotal(monetaryTotal);
    }

    private void addCreditNoteLine() {
        CreditNoteLineType line = new CreditNoteLineType();

        line.setID(id("1"));
        line.getNote().add(note(""));

        line.setCreditedQuantity(new CreditedQuantityType());
        line.getCreditedQuantity().setValue(BigDecimal.valueOf(1));
        line.getCreditedQuantity().setUnitCode("C62");

        line.setLineExtensionAmount(new LineExtensionAmountType());
        line.getLineExtensionAmount().setValue(BigDecimal.valueOf(25));
        line.getLineExtensionAmount().setCurrencyID("TRY");

        OrderLineReferenceType orderLine = new OrderLineReferenceType();
        orderLine.setLineID(new LineIDType());
        orderLine.getLineID().setValue("1");

        line.getOrderLineReference().add(orderLine);

        line.getTaxTotal().add(taxTotal());

        ItemType item = new ItemType();
        item.setName(name("Müstahsil"));
        item.setSellersItemIdentification(new ItemIdentificationType());
        item.getSellersItemIdentification().setID(id("011"));

        line.setItem(item);

        PriceType price = new PriceType();
        price.setPriceAmount(new PriceAmountType());
        price.getPriceAmount().setValue(BigDecimal.valueOf(25));
        price.getPriceAmount().setCurrencyID("TRY");

        line.setPrice(price);

        creditNote.getCreditNoteLine().add(line);
    }

    private void addGibTemplate() throws Exception{
        DocumentReferenceType ref = new DocumentReferenceType();
        ref.setID(creditNote.getID());
        ref.setIssueDate(creditNote.getIssueDate());
        ref.setDocumentType(documentType("XSLT"));
        ref.setAttachment(xsltTemplate(creditNote.getID().getValue(), Xslt.readCreditNote()));

        creditNote.getAdditionalDocumentReference().add(ref);
    }
}
