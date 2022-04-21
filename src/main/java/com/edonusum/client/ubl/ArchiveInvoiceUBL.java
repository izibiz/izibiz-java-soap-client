package com.edonusum.client.ubl;

import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ArchiveInvoiceUBL extends UBL {
    private final InvoiceType invoice;

    public ArchiveInvoiceUBL() throws Exception {
        this.invoice = new InvoiceType();

        createHeader();
        createSignature();
        createAccountSupplierParty();
        createAccountCustomerParty();
        createDelivery();
        createTaxTotal();
        createLegalMonetaryTotal();
        addInvoiceLine();
        addAdditionalDocumentReferences();
        addGibTemplate(); // Xslt şablonu
    }

    public InvoiceType getInvoice() {
        return this.invoice;
    }

    private void createHeader() throws Exception {
        invoice.setUBLVersionID(ublVersion());
        invoice.setCustomizationID(customizationID("TR1.2"));
        invoice.setProfileID(profileId("TEMELFATURA"));

        invoice.setID(id(IdentifierUtils.createInvoiceIdRandomPrefix()));
        invoice.setUUID(uuid(UUID.randomUUID().toString()));

        invoice.setCopyIndicator(new CopyIndicatorType());
        invoice.getCopyIndicator().setValue(false);

        invoice.setIssueDate(issueDate());
        invoice.setIssueTime(issueTime());

        invoice.setInvoiceTypeCode(new InvoiceTypeCodeType());
        invoice.getInvoiceTypeCode().setValue("SATIS");

        invoice.setDocumentCurrencyCode(new DocumentCurrencyCodeType());
        invoice.getDocumentCurrencyCode().setValue("TRY");

        invoice.setLineCountNumeric(new LineCountNumericType());
        invoice.getLineCountNumeric().setValue(BigDecimal.valueOf(1));
    }

    private void addAdditionalDocumentReferences() {
        DocumentReferenceType ref = new DocumentReferenceType();

        ref.setID(id(UUID.randomUUID().toString()));
        ref.setIssueDate(invoice.getIssueDate());
        ref.setDocumentType(documentType("XSLT"));

        invoice.getAdditionalDocumentReference().add(ref);

        DocumentReferenceType ref2 = new DocumentReferenceType();
        ref2.setID(id("1"));
        ref2.setIssueDate(invoice.getIssueDate());
        ref2.setDocumentTypeCode(documentTypeCode("SendingType"));
        ref2.setDocumentType(documentType("ELEKTRONIK"));

        invoice.getAdditionalDocumentReference().add(ref2);
    }

    private void createSignature() {
        invoice.getSignature().add(signature(invoice.getID().getValue()));
    }

    private void createAccountSupplierParty() {
        SupplierPartyType supplier = new SupplierPartyType();

        supplier.setParty(defaultParty());

        invoice.setAccountingSupplierParty(supplier);
    }

    private void createAccountCustomerParty() {
        CustomerPartyType customer = new CustomerPartyType();

        customer.setParty(party("İzibiz", "ISTANBUL", "Street caddesi", "Bahçelievler", "44444", "Güney Sudan", "DAVUTPAŞA","11111111111", "11111111112", "email@example.com", "4444444444"));

        invoice.setAccountingCustomerParty(customer);
    }

    private void createDelivery() throws Exception {
        DeliveryType delivery = new DeliveryType();

        delivery.setDeliveryParty(defaultParty());

        delivery.setDespatch(new DespatchType());
        delivery.getDespatch().setActualDespatchDate(new ActualDespatchDateType());
        delivery.getDespatch().getActualDespatchDate().setValue(DateUtils.now());

        delivery.setActualDeliveryTime(new ActualDeliveryTimeType());
        delivery.getActualDeliveryTime().setValue(DateUtils.now());

        invoice.getDelivery().add(delivery);
    }

    private void addInvoiceLine() {
        InvoiceLineType line = new InvoiceLineType();

        line.setID(id("1"));
        line.getNote().add(note("Deneme"));

        line.setInvoicedQuantity(new InvoicedQuantityType());
        line.getInvoicedQuantity().setValue(BigDecimal.valueOf(1));
        line.getInvoicedQuantity().setUnitCode("EA");

        line.setLineExtensionAmount(new LineExtensionAmountType());
        line.getLineExtensionAmount().setCurrencyID("TRY");
        line.getLineExtensionAmount().setValue(BigDecimal.valueOf(1));

        TaxSubtotalType subTotal = subTotal(17.8, 3.2, 1, 18, "KDV","0015");
        TaxTotalType taxTotal = taxTotal(3.2, List.of(subTotal));

        line.setTaxTotal(taxTotal);

        ItemType item = new ItemType();
        item.setName(name("PROSER.1028"));
        item.setDescription(new DescriptionType());
        item.getDescription().setValue("Test description");

        line.setItem(item);

        line.setPrice(new PriceType());
        line.getPrice().setPriceAmount(new PriceAmountType());
        line.getPrice().getPriceAmount().setValue(BigDecimal.valueOf(17.8));
        line.getPrice().getPriceAmount().setCurrencyID("TRY");

        invoice.getInvoiceLine().add(line);
    }

    private void createTaxTotal() {
        TaxSubtotalType subTotal = subTotal(17.8, 32, 1, 18, "KDV", "0015");
        TaxTotalType total = taxTotal(3.2, List.of(subTotal));

        invoice.getTaxTotal().add(total);
    }

    private void createLegalMonetaryTotal() {
        MonetaryTotalType monetaryTotal = legalMonetaryTotal(0, 0, 0, 21, 0);

        invoice.setLegalMonetaryTotal(monetaryTotal);
    }

    private void addGibTemplate() throws Exception {
        DocumentReferenceType ref = new DocumentReferenceType();

        ref.setID(id(UUID.randomUUID().toString()));

        ref.setIssueDate(invoice.getIssueDate());
        ref.setDocumentType(documentType("XSLT"));

        ref.setAttachment(xsltTemplate(invoice.getID().getValue(), Xslt.readArchive()));

        invoice.getAdditionalDocumentReference().add(ref);
    }
}
