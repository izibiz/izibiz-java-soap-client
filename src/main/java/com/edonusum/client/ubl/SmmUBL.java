package com.edonusum.client.ubl;

import com.edonusum.client.util.IdentifierUtils;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SmmUBL extends UBL {
    private final InvoiceType invoice;

    public SmmUBL() throws Exception{
        invoice = new InvoiceType();

        createHeader();
        createSignature();
        createSupplierParty();
        createCustomerParty();
        createTaxTotal();
        createWithHoldingTaxTotal();
        createLegalMonetaryTotal();
        addInvoiceLine();
        addDocumentReferences();
        addGibTemplate(); // Xslt şablonu
    }

    public InvoiceType getInvoice() {
        return this.invoice;
    }

    private void createHeader() throws Exception{
        invoice.setUBLVersionID(ublVersion());
        invoice.setCustomizationID(customizationID("TR1.2"));

        invoice.setProfileID(profileId("EARSIVBELGE"));

        invoice.setID(id(IdentifierUtils.createInvoiceIdRandomPrefix()));
        invoice.setUUID(uuid(UUID.randomUUID().toString()));

        invoice.setCopyIndicator(new CopyIndicatorType());
        invoice.getCopyIndicator().setValue(false);

        invoice.setIssueDate(issueDate());
        invoice.setIssueTime(issueTime());

        invoice.setInvoiceTypeCode(new InvoiceTypeCodeType());
        invoice.getInvoiceTypeCode().setValue("SERBESTMESLEKMAKBUZU");

        invoice.setDocumentCurrencyCode(new DocumentCurrencyCodeType());
        invoice.getDocumentCurrencyCode().setValue("TRY");

        invoice.setLineCountNumeric(new LineCountNumericType());
        invoice.getLineCountNumeric().setValue(BigDecimal.valueOf(1));
    }

    private void addDocumentReferences() {
        DocumentReferenceType ref1 = new DocumentReferenceType();
        ref1.setID(id(UUID.randomUUID().toString()));
        ref1.setDocumentType(documentType("KAGIT"));
        ref1.setDocumentTypeCode(documentTypeCode("SendingType"));

        invoice.getAdditionalDocumentReference().add(ref1);

        DocumentReferenceType ref2 = new DocumentReferenceType();
        ref2.setID(id(UUID.randomUUID().toString()));
        ref2.setDocumentType(documentType("CalculationType"));
        ref2.setDocumentTypeCode(documentTypeCode("BRUT"));

        invoice.getAdditionalDocumentReference().add(ref2);
    }

    private void createSignature() {
        invoice.getSignature().add(signature(invoice.getID().getValue()));
    }

    private void createSupplierParty() {
        SupplierPartyType supplier = new SupplierPartyType();
        supplier.setParty(defaultParty());

        invoice.setAccountingSupplierParty(supplier);
    }

    private void createCustomerParty() {
        CustomerPartyType customer = new CustomerPartyType();
        customer.setParty(defaultParty());

        invoice.setAccountingCustomerParty(customer);
    }

    private void createPayments() {
        PaymentMeansType payment = new PaymentMeansType();

        payment.setPaymentMeansCode(new PaymentMeansCodeType());
        payment.getPaymentMeansCode().setValue("46");

        payment.setPayeeFinancialAccount(new FinancialAccountType());
        payment.getPayeeFinancialAccount().setID(id("TR111111122222222222222222"));

        payment.getPayeeFinancialAccount().setCurrencyCode(new CurrencyCodeType());
        payment.getPayeeFinancialAccount().getCurrencyCode().setValue("TRY");

        payment.getPayeeFinancialAccount().setFinancialInstitutionBranch(new BranchType());
        payment.getPayeeFinancialAccount().getFinancialInstitutionBranch().setName(name("Alibeyköy Şubesi"));

        payment.getPayeeFinancialAccount().getFinancialInstitutionBranch().setFinancialInstitution(new FinancialInstitutionType());
        payment.getPayeeFinancialAccount().getFinancialInstitutionBranch().getFinancialInstitution().setName(name("EYÜP"));

        invoice.getPaymentMeans().add(payment);
    }

    private void addInvoiceLine() {
        InvoiceLineType line = new InvoiceLineType();
         line.setID(id("1"));

         line.setInvoicedQuantity(new InvoicedQuantityType());
         line.getInvoicedQuantity().setValue(BigDecimal.valueOf(1));
         line.getInvoicedQuantity().setUnitCode("C62");

         line.setLineExtensionAmount(new LineExtensionAmountType());
         line.getLineExtensionAmount().setCurrencyID("TRY");
         line.getLineExtensionAmount().setValue(BigDecimal.valueOf(17.8));

         TaxSubtotalType subTotal = subTotal(17.8, 3.2, 1, 18, "KDV", "0015");
         TaxSubtotalType subTotal2 = subTotal(17.8, 3.56, 1, 20, "GV.SPOTAJI", "0003");

         TaxTotalType total = taxTotal(6.76, List.of(subTotal, subTotal2));

         line.setTaxTotal(total);

         TaxSubtotalType withHoldingSubTotal = subTotal(3.2, 0.64, 1, 20, "KDV Tevkifat", "0003");

         TaxTotalType withHoldingTaxTotal = taxTotal(0.64, List.of(withHoldingSubTotal));

         line.getWithholdingTaxTotal().add(withHoldingTaxTotal);

         ItemType item = new ItemType();
         item.setName(name("test"));

         line.setItem(item);

         PriceType price = new PriceType();
         price.setPriceAmount(new PriceAmountType());
         price.getPriceAmount().setValue(BigDecimal.valueOf(17.8));
         price.getPriceAmount().setCurrencyID("TRY");

         line.setPrice(price);

         invoice.getInvoiceLine().add(line);
    }

    private void createWithHoldingTaxTotal() {
        TaxSubtotalType subTotal = subTotal(3.2, 0.64, 1, 20, "KDV Tevfikat", "0003");
        TaxTotalType total = taxTotal(0.64,  List.of(subTotal));

        invoice.getWithholdingTaxTotal().add(total);
    }

    private void createTaxTotal() {
        TaxSubtotalType subTotal = subTotal(17.8, 3.56, 1, 20, "GV.STOPAJI", "0003");
        TaxSubtotalType subTotal2 = subTotal(17.8, 3.2, 1, 18, "KDV", "0015");

        TaxTotalType total = taxTotal(6.76, List.of(subTotal, subTotal2));

        invoice.getTaxTotal().add(total);
    }

    private void createLegalMonetaryTotal() {
        MonetaryTotalType monetaryTotal = legalMonetaryTotal(17.8, 17.8, 21.8, 16.8, 0);

        invoice.setLegalMonetaryTotal(monetaryTotal);
    }

    private void addGibTemplate() throws Exception {
        DocumentReferenceType ref = new DocumentReferenceType();
        ref.setID(id(UUID.randomUUID().toString()));
        ref.setIssueDate(invoice.getIssueDate());
        ref.setDocumentType(documentType("XSLT"));
        ref.setAttachment(xsltTemplate(invoice.getID().getValue(), Xslt.readSmm()));

        invoice.getAdditionalDocumentReference().add(ref);
    }
}
