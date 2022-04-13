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
        addAdditionalDocumentReference();
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

    private void addAdditionalDocumentReference() {
        DocumentReferenceType ref = new DocumentReferenceType();

        ref.setID(id(UUID.randomUUID().toString()));
        ref.setIssueDate(invoice.getIssueDate());
        ref.setDocumentType(documentType("XSLT"));

        invoice.getAdditionalDocumentReference().add(ref);
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

        customer.setParty(party("Taha anonim şirketi", "Malatya", "Street caddesi", "Yeşilyurt", "44444", "Güney Sudan", "DAVUTPAŞA","11111111111", "11111111112", "email@example.com", "44444444444"));

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
        MonetaryTotalType monetaryTotal = new MonetaryTotalType();

        monetaryTotal.setLineExtensionAmount(new LineExtensionAmountType());
        monetaryTotal.setTaxInclusiveAmount(new TaxInclusiveAmountType());
        monetaryTotal.setTaxExclusiveAmount(new TaxExclusiveAmountType());
        monetaryTotal.setAllowanceTotalAmount(new AllowanceTotalAmountType());
        monetaryTotal.setPayableAmount(new PayableAmountType());

        monetaryTotal.getLineExtensionAmount().setValue(BigDecimal.valueOf(0));
        monetaryTotal.getLineExtensionAmount().setCurrencyID("TRY");

        monetaryTotal.getTaxInclusiveAmount().setCurrencyID("TRY");
        monetaryTotal.getTaxInclusiveAmount().setValue(BigDecimal.ZERO);

        monetaryTotal.getTaxExclusiveAmount().setCurrencyID("TRY");
        monetaryTotal.getTaxExclusiveAmount().setValue(BigDecimal.ZERO);

        monetaryTotal.getPayableAmount().setCurrencyID("TRY");
        monetaryTotal.getPayableAmount().setValue(BigDecimal.valueOf(21));

        invoice.setLegalMonetaryTotal(monetaryTotal);
    }

    private void addGibTemplate() throws Exception {
        DocumentReferenceType ref = new DocumentReferenceType();

        ref.setID(id(UUID.randomUUID().toString()));
        ref.setIssueDate(invoice.getIssueDate());
        ref.setDocumentType(documentType("XSLT"));

        ref.setAttachment(defaultAttachment(invoice.getID().getValue(), Xslt.readArchive()));

        invoice.getAdditionalDocumentReference().add(ref);
    }
}
