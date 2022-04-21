package com.edonusum.client.ubl;

import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvoiceUBL extends UBL{
    private InvoiceType invoice;
    private List<InvoiceLineType> invLines = new ArrayList<>();
    private List<DocumentReferenceType> refList = new ArrayList<>();

    public InvoiceUBL() throws Exception{
        invoice = new InvoiceType();

        createHeader();
        createSignature();
        accountSupplierParty();
        accountCustomerParty();
        delivery();
        createTaxTotal();
        addInvoiceLine();
        legalMonetarTotal();
        addAdditionalDocumentReference();
        addGibTemplate(); // Xslt şablonu
    }

    public InvoiceType getInvoice() {
        return this.invoice;
    }

    private void createHeader() throws Exception{
        invoice.setUBLVersionID(ublVersion());
        invoice.setCustomizationID(customizationID("TR1.2"));

        invoice.setProfileID(profileId("TICARIFATURA"));

        invoice.setID(id(IdentifierUtils.createInvoiceIdRandomPrefix()));
        invoice.setUUID(uuid(UUID.randomUUID().toString()));

        CopyIndicatorType copyIndicatorType = new CopyIndicatorType();
        copyIndicatorType.setValue(false);
        invoice.setCopyIndicator(copyIndicatorType);

        invoice.setIssueDate(issueDate());
        invoice.setIssueTime(issueTime());

        invoice.setInvoiceTypeCode(new InvoiceTypeCodeType());
        invoice.getInvoiceTypeCode().setValue("SATIS");

        invoice.setDocumentCurrencyCode(new DocumentCurrencyCodeType());
        invoice.getDocumentCurrencyCode().setValue("TRY");

        invoice.setLineCountNumeric(new LineCountNumericType());
        invoice.getLineCountNumeric().setValue(BigDecimal.ONE);
    }

    private void addAdditionalDocumentReference() {
        DocumentReferenceType documentReferenceType = new DocumentReferenceType();

        documentReferenceType.setID(id("1"));
        documentReferenceType.setIssueDate(invoice.getIssueDate());
        documentReferenceType.setDocumentType(documentType("KAGIT"));
        documentReferenceType.setDocumentTypeCode(documentTypeCode("SendingType"));

        refList.add(documentReferenceType);
    }

    private void createSignature() {
        invoice.getSignature().add(signature(invoice.getID().getValue()));
    }

    private void accountSupplierParty() {
        SupplierPartyType supplierPartyType = new SupplierPartyType();

        supplierPartyType.setParty(defaultParty());

        invoice.setAccountingSupplierParty(supplierPartyType);
    }

    private void accountCustomerParty() {
        CustomerPartyType customerPartyType = new CustomerPartyType();

        customerPartyType.setParty(defaultParty());

        invoice.setAccountingCustomerParty(customerPartyType);
    }

    private void delivery() throws Exception{
        DeliveryType deliveryType = new DeliveryType();

        deliveryType.setDeliveryParty(defaultParty());

        DespatchType despatch = new DespatchType();
        despatch.setActualDespatchDate(new ActualDespatchDateType());
        despatch.getActualDespatchDate().setValue(DateUtils.now());

        despatch.setActualDespatchTime(new ActualDespatchTimeType());
        despatch.getActualDespatchTime().setValue(DateUtils.now());

        deliveryType.setDespatch(despatch);

        invoice.getDelivery().add(deliveryType);
    }

    private void addInvoiceLine() {
        InvoiceLineType line = new InvoiceLineType();

        line.setID(id("1"));
        line.getNote().add(note("Test"));

        line.setInvoicedQuantity(new InvoicedQuantityType());
        line.getInvoicedQuantity().setValue(BigDecimal.ONE);
        line.getInvoicedQuantity().setUnitCode("EA");

        line.setLineExtensionAmount(new LineExtensionAmountType());
        line.getLineExtensionAmount().setValue(BigDecimal.valueOf(17.8));
        line.getLineExtensionAmount().setCurrencyID("TRY");

        // tax total
        TaxTotalType taxTotalType = new TaxTotalType();

        taxTotalType.setTaxAmount(new TaxAmountType());
        taxTotalType.getTaxAmount().setCurrencyID("TRY");
        taxTotalType.getTaxAmount().setValue(BigDecimal.valueOf(3.2));

        // tax sub total
        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

        taxSubtotalType.setTaxableAmount(new TaxableAmountType());
        taxSubtotalType.getTaxableAmount().setCurrencyID("TRY");
        taxSubtotalType.getTaxableAmount().setValue(BigDecimal.valueOf(17.8));

        taxSubtotalType.setTaxAmount(new TaxAmountType());
        taxSubtotalType.getTaxAmount().setValue(BigDecimal.valueOf(3.2));
        taxSubtotalType.getTaxAmount().setCurrencyID("TRY");

        taxSubtotalType.setCalculationSequenceNumeric(new CalculationSequenceNumericType());
        taxSubtotalType.getCalculationSequenceNumeric().setValue(BigDecimal.ONE);

        taxSubtotalType.setPercent(new PercentType());
        taxSubtotalType.getPercent().setValue(BigDecimal.valueOf(18));

        taxSubtotalType.setTaxCategory(new TaxCategoryType());
        taxSubtotalType.getTaxCategory().setTaxScheme(new TaxSchemeType());
        taxSubtotalType.getTaxCategory().getTaxScheme().setName(name("KDV"));
        taxSubtotalType.getTaxCategory().getTaxScheme().setTaxTypeCode(new TaxTypeCodeType());
        taxSubtotalType.getTaxCategory().getTaxScheme().getTaxTypeCode().setValue("0015");

        taxTotalType.getTaxSubtotal().add(taxSubtotalType);

        // item
        ItemType item = new ItemType();
        item.setName(name("Defter"));
        item.setDescription(new DescriptionType());
        item.getDescription().setValue("Item description");

        PriceType price = new PriceType();
        price.setPriceAmount(new PriceAmountType());
        price.getPriceAmount().setValue(BigDecimal.valueOf(17.8));
        price.getPriceAmount().setCurrencyID("TRY");

        line.setItem(item);
        line.setPrice(price);
        line.setTaxTotal(taxTotalType);

        invoice.getInvoiceLine().add(line);
    }

    private void createTaxTotal() {
        TaxSubtotalType subTotal = subTotal(17.8, 3.2, 1, 18, "KDV", "0015");
        TaxTotalType total = taxTotal(3.2, List.of(subTotal));

        invoice.getTaxTotal().add(total);
    }

    private void legalMonetarTotal() {
        MonetaryTotalType monetaryTotalType = legalMonetaryTotal(0, 0, 0, 21, 0);

        invoice.setLegalMonetaryTotal(monetaryTotalType);
    }

    private void addGibTemplate() throws Exception{
        DocumentReferenceType reference = new DocumentReferenceType();

        reference.setID(id(UUID.randomUUID().toString()));
        reference.setIssueDate(issueDate());
        reference.setDocumentType(documentType("XSLT"));

        reference.setAttachment(xsltTemplate(invoice.getID().getValue(), Xslt.readInvoice()));

        invoice.getAdditionalDocumentReference().add(reference);
    }
}
