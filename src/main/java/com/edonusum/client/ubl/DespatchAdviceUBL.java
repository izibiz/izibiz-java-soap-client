package com.edonusum.client.ubl;

import com.edonusum.client.util.DateUtils;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;

import java.math.BigDecimal;
import java.util.UUID;

public class DespatchAdviceUBL extends UBL{
    private final DespatchAdviceType despatchAdvice;

    public DespatchAdviceUBL(String id, String uuid) throws Exception {
        despatchAdvice = new DespatchAdviceType();

        createHeader(id, uuid);
        createSignature();
        despatchSupplierParty();
        delivery();
        shipment();
        addDespatchLine();
        addGibTemplate(); // Xslt şablonu
    }

    public DespatchAdviceType getDespatchAdvice() {
        return this.despatchAdvice;
    }

    private void createHeader(String id, String uuid) throws Exception {
        despatchAdvice.setUBLVersionID(ublVersion());
        despatchAdvice.setCustomizationID(customizationID("TR1.2.1"));
        despatchAdvice.setProfileID(profileId("TEMELIRSALIYE"));
        despatchAdvice.setID(id(id));
        despatchAdvice.setUUID(uuid(uuid));

        despatchAdvice.setCopyIndicator(new CopyIndicatorType());
        despatchAdvice.getCopyIndicator().setValue(false);

        despatchAdvice.setIssueDate(issueDate());
        despatchAdvice.setIssueTime(issueTime());

        despatchAdvice.setDespatchAdviceTypeCode(new DespatchAdviceTypeCodeType());
        despatchAdvice.getDespatchAdviceTypeCode().setValue("SEVK");

        despatchAdvice.getNote().add(note("Denemedir"));

        despatchAdvice.setLineCountNumeric(new LineCountNumericType());
        despatchAdvice.getLineCountNumeric().setValue(BigDecimal.ONE);
    }

    private void despatchSupplierParty() {
        SupplierPartyType supplier = new SupplierPartyType();

        supplier.setParty(defaultParty());

        supplier.setDespatchContact(new ContactType());
        supplier.getDespatchContact().setName(name(""));

        despatchAdvice.setDespatchSupplierParty(supplier);
    }

    private void delivery() {
        CustomerPartyType customer = new CustomerPartyType();

        customer.setParty(defaultParty());

        despatchAdvice.setDeliveryCustomerParty(customer);
    }

    private void createSignature() {
        despatchAdvice.getSignature().add(signature(despatchAdvice.getID().getValue()));
    }

    private void shipment() throws Exception {
        ShipmentType shipment = new ShipmentType();

        // id
        shipment.setID(new IDType());

        // weight
        NetWeightMeasureType weight = new NetWeightMeasureType();
        weight.setUnitCode("C62");
        weight.setValue(BigDecimal.valueOf(1.0));

        shipment.setNetWeightMeasure(weight);

        // total goods quantity
        TotalGoodsItemQuantityType count = new TotalGoodsItemQuantityType();
        count.setValue(BigDecimal.ONE);

        shipment.setTotalGoodsItemQuantity(count);

        // items
        GoodsItemType item = new GoodsItemType();
        item.setValueAmount(new ValueAmountType());
        item.getValueAmount().setValue(BigDecimal.valueOf(800.0));
        item.getValueAmount().setCurrencyID("TRY");

        shipment.getGoodsItem().add(item);

        // shipment stages
        ShipmentStageType stage = new ShipmentStageType();

        stage.setID(id("1"));

        stage.setTransportModeCode(new TransportModeCodeType());

        stage.setTransportMeans(new TransportMeansType());
        stage.getTransportMeans().setRoadTransport(new RoadTransportType());
        stage.getTransportMeans().getRoadTransport().setLicensePlateID(new LicensePlateIDType());
        stage.getTransportMeans().getRoadTransport().getLicensePlateID().setValue("44FB444");
        stage.getTransportMeans().getRoadTransport().getLicensePlateID().setSchemeID("PLAKA");

        PersonType driver = new PersonType();
        driver.setFirstName(new FirstNameType());
        driver.getFirstName().setValue("AD");
        driver.setFamilyName(new FamilyNameType());
        driver.getFamilyName().setValue("SOYAD");
        driver.setNationalityID(new NationalityIDType());
        driver.getNationalityID().setValue("11111111111");

        stage.getDriverPerson().add(driver);

        shipment.getShipmentStage().add(stage);

        // delivery type
        DeliveryType delivery = new DeliveryType();
        delivery.setDeliveryAddress(new AddressType());
        delivery.getDeliveryAddress().setCountry(country("TURKIYE"));
        delivery.getDeliveryAddress().setCityName(city("ISTANBUL"));
        delivery.getDeliveryAddress().setPostalZone(postalZone("34065"));
        delivery.getDeliveryAddress().setCitySubdivisionName(citySub("info@bicycleworld.com"));

        delivery.setCarrierParty(defaultParty());

        delivery.setDespatch(new DespatchType());
        delivery.getDespatch().setActualDespatchTime(new ActualDespatchTimeType());
        delivery.getDespatch().setActualDespatchDate(new ActualDespatchDateType());
        delivery.getDespatch().getActualDespatchDate().setValue(DateUtils.nowWithoutTime());
        delivery.getDespatch().getActualDespatchTime().setValue(DateUtils.now());

        shipment.setDelivery(delivery);

        despatchAdvice.setShipment(shipment);
    }

    private void addDespatchLine() {
        DespatchLineType line = new DespatchLineType();

        line.setID(id("1"));

        line.getNote().add(note(""));

        line.setDeliveredQuantity(new DeliveredQuantityType());
        line.getDeliveredQuantity().setValue(BigDecimal.ONE);
        line.getDeliveredQuantity().setUnitCode("C62");

        line.setOrderLineReference(new OrderLineReferenceType());
        line.getOrderLineReference().setLineID(new LineIDType());

        ItemType item = new ItemType();
        item.setName(name("DEFTER"));
        item.setSellersItemIdentification(new ItemIdentificationType());
        item.getSellersItemIdentification().setID(id("BP001"));

        line.setItem(item);

        ShipmentType shipment = new ShipmentType();
        shipment.setID(id(""));

        GoodsItemType goodsItem = new GoodsItemType();
        InvoiceLineType invLine = new InvoiceLineType();
        invLine.setID(new IDType());

        invLine.setInvoicedQuantity(new InvoicedQuantityType());
        invLine.getInvoicedQuantity().setValue(BigDecimal.ZERO);
        invLine.getInvoicedQuantity().setUnitCode("C62");

        invLine.setLineExtensionAmount(new LineExtensionAmountType());
        invLine.getLineExtensionAmount().setCurrencyID("TRY");
        invLine.getLineExtensionAmount().setValue(BigDecimal.valueOf(800));

        ItemType itemType = new ItemType();
        itemType.setName(name("BP0001"));
        invLine.setItem(itemType);

        PriceType price = new PriceType();
        price.setPriceAmount(new PriceAmountType());
        price.getPriceAmount().setValue(BigDecimal.valueOf(800));
        price.getPriceAmount().setCurrencyID("TRY");

        invLine.setPrice(price);

        goodsItem.getInvoiceLine().add(invLine);
        shipment.getGoodsItem().add(goodsItem);

        line.getShipment().add(shipment);

        despatchAdvice.getDespatchLine().add(line);
    }

    private void addGibTemplate() throws Exception {
        DocumentReferenceType ref = new DocumentReferenceType();

        ref.setID(id(UUID.randomUUID().toString()));

        ref.setIssueDate(issueDate());
        ref.setDocumentType(documentType("XSLT"));

        ref.setAttachment(xsltTemplate(despatchAdvice.getID().getValue(), Xslt.readDespatch()));

        despatchAdvice.getAdditionalDocumentReference().add(ref);
    }
}
