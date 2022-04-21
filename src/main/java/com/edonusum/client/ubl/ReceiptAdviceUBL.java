package com.edonusum.client.ubl;

import com.edonusum.client.util.DateUtils;
import com.edonusum.client.util.IdentifierUtils;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.receiptadvice_2.ReceiptAdviceType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReceiptAdviceUBL extends UBL{

    private ReceiptAdviceType receiptAdvice;
    private List<DocumentReferenceType> refList = new ArrayList<>();

    public ReceiptAdviceUBL(String id, String uuid, String date) throws Exception{
        receiptAdvice = new ReceiptAdviceType();

        createHeader();
        createSignature();
        createDespatchDocumentReference(uuid, date);
        createAdditionalDocumentReference(id);
        createDelivery();
        createDespatchSupplierParty();
        createShipment();
        addReceiptLine();
        addGibTemplate(); // Xslt şablonu
    }

    private void createHeader() throws Exception{
        receiptAdvice.setUBLVersionID(ublVersion());
        receiptAdvice.setCustomizationID(customizationID("TR1.2.1"));

        receiptAdvice.setProfileID(profileId("TEMELIRSALIYE"));

        receiptAdvice.setID(id(IdentifierUtils.createInvoiceIdRandomPrefix()));
        receiptAdvice.setUUID(uuid(UUID.randomUUID().toString()));

        // Copy indicator
        CopyIndicatorType copyIndicatorType = new CopyIndicatorType();
        copyIndicatorType.setValue(false);
        receiptAdvice.setCopyIndicator(copyIndicatorType);

        receiptAdvice.setIssueDate(issueDate());
        receiptAdvice.setIssueTime(issueTime());

        // Receipt advice type code
        ReceiptAdviceTypeCodeType receiptAdviceTypeCodeType = new ReceiptAdviceTypeCodeType();
        receiptAdviceTypeCodeType.setValue("SEVK");
        receiptAdvice.setReceiptAdviceTypeCode(receiptAdviceTypeCodeType);

        receiptAdvice.getNote().add(note("Test"));

        // Line count numeric
        LineCountNumericType lineCountNumericType = new LineCountNumericType();
        lineCountNumericType.setValue(BigDecimal.ONE);
        receiptAdvice.setLineCountNumeric(lineCountNumericType);
    }

    private void createDespatchDocumentReference(String uuid, String date) throws Exception{
        // yanıt verilen irsaliye uuid
        DocumentReferenceType documentReferenceType = new DocumentReferenceType();
        documentReferenceType.setID(id(uuid));
        documentReferenceType.setIssueDate(issueDate());

        receiptAdvice.setDespatchDocumentReference(documentReferenceType);
    }

    private void createSignature() {
        receiptAdvice.getSignature().add(signature(receiptAdvice.getID().getValue()));
    }

    private void createAdditionalDocumentReference(String id) {
        // yanıt verilen irsaliye id
        DocumentReferenceType documentReferenceType = new DocumentReferenceType();
        documentReferenceType.setID(id(id));

        // issue date
        documentReferenceType.setIssueDate(receiptAdvice.getIssueDate());

        // document type
        documentReferenceType.setDocumentType(documentType("DespatchAdviceID"));
        documentReferenceType.setDocumentTypeCode(documentTypeCode("DespatchAdviceID"));

        receiptAdvice.getAdditionalDocumentReference().add(documentReferenceType);

        refList.add(documentReferenceType);
    }

    private void createDespatchSupplierParty() {
        SupplierPartyType supplierPartyType = new SupplierPartyType();

        // party
        supplierPartyType.setParty(defaultParty());

        // contact
        ContactType contactType = new ContactType();
        contactType.setName(new NameType());
        supplierPartyType.setDespatchContact(contactType);

        receiptAdvice.setDespatchSupplierParty(supplierPartyType);
    }

    private void createDelivery() {
        CustomerPartyType customerPartyType = new CustomerPartyType();

        // party
        PartyType party = party("akbil teknolojileri","ANKARA","DENEME ADRES BİLGİLERİ","KAHRAMANKAZAN","","TÜRKİYE","KURUMLAR","(800) 811-1199","","yazilim@izibiz.com.tr","4840847211");

        customerPartyType.setParty(party);

        receiptAdvice.setDeliveryCustomerParty(customerPartyType);
    }

    private void createShipment() throws Exception{
        ShipmentType shipmentType = new ShipmentType();

        // id
        shipmentType.setID(new IDType());

        // delivery
        DeliveryType deliveryType = new DeliveryType();
        ActualDeliveryDateType date = new ActualDeliveryDateType();
        ActualDeliveryTimeType time = new ActualDeliveryTimeType();
        date.setValue(DateUtils.now());
        time.setValue(DateUtils.now());

        deliveryType.setActualDeliveryDate(date);
        deliveryType.setActualDeliveryTime(time);

        shipmentType.setDelivery(deliveryType);

        receiptAdvice.setShipment(shipmentType);
    }

    private ReceiptLineType createLine(String id, String orderLine, String lineReference, String name) {
        ReceiptLineType receiptLine = new ReceiptLineType();

        // id
        receiptLine.setID(id(id));

        // received quantity
        ReceivedQuantityType receivedQuantityType = new ReceivedQuantityType();
        receivedQuantityType.setValue(BigDecimal.ONE);
        receivedQuantityType.setUnitCode("C62");

        receiptLine.setReceivedQuantity(receivedQuantityType);

        // order line
        OrderLineReferenceType orderLineReferenceType = new OrderLineReferenceType();
        LineIDType lineIDType = new LineIDType();
        lineIDType.setValue(orderLine);
        orderLineReferenceType.setLineID(lineIDType);

        receiptLine.setOrderLineReference(orderLineReferenceType);

        // line reference
        LineReferenceType lineReferenceType = new LineReferenceType();
        LineIDType lineIDType1 = new LineIDType();
        lineIDType1.setValue(lineReference);
        lineReferenceType.setLineID(lineIDType1);

        receiptLine.setDespatchLineReference(lineReferenceType);

        // item
        ItemType itemType = new ItemType();
        itemType.setName(name(name));

        ItemIdentificationType itemIdentificationType = new ItemIdentificationType();
        itemIdentificationType.setID(new IDType());
        itemType.setSellersItemIdentification(itemIdentificationType);

        receiptLine.setItem(itemType);

        // shipment
        ShipmentType shipmentType = new ShipmentType();
        shipmentType.setID(new IDType());
        TotalGoodsItemQuantityType totalGoodsItemQuantityType = new TotalGoodsItemQuantityType();
        totalGoodsItemQuantityType.setValue(BigDecimal.ONE);
        shipmentType.setTotalGoodsItemQuantity(totalGoodsItemQuantityType);

        receiptLine.getShipment().add(shipmentType);

        return receiptLine;
    }

    private void addReceiptLine() {
        ReceiptLineType line = createLine("BP001", "1", "", "DEFTER");
        receiptAdvice.getReceiptLine().add(line);
    }

    private void addGibTemplate() throws Exception{
        DocumentReferenceType documentReferenceType = new DocumentReferenceType();

        // id
        documentReferenceType.setID(id(UUID.randomUUID().toString()));

        // issue date
        documentReferenceType.setIssueDate(issueDate());

        // document type
        documentReferenceType.setDocumentType(documentType("XSLT"));

        // attachment
        documentReferenceType.setAttachment(xsltTemplate(receiptAdvice.getID().getValue(), Xslt.readReceipt()));

        refList.add(documentReferenceType);

        receiptAdvice.getAdditionalDocumentReference().add(documentReferenceType);
    }

    public ReceiptAdviceType getReceiptAdvice() {
        return this.receiptAdvice;
    }

}
