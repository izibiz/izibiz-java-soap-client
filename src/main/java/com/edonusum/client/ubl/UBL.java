package com.edonusum.client.ubl;

import com.edonusum.client.util.DateUtils;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class UBL {
    
    public static UBLVersionIDType ublVersion() {
        UBLVersionIDType ublVersionIDType = new UBLVersionIDType();
        ublVersionIDType.setValue("2.1");
        
        return ublVersionIDType;
    }
    
    public static CustomizationIDType customizationID(String v) {
        CustomizationIDType customizationIDType = new CustomizationIDType();
        customizationIDType.setValue(v);
        
        return customizationIDType;
    }
    
    public static IssueTimeType issueTime() throws Exception{
        IssueTimeType issueTimeType = new IssueTimeType();
        issueTimeType.setValue(DateUtils.now());
        
        return issueTimeType;
    }

    public static IssueDateType issueDate() throws Exception{
        IssueDateType issueDateType = new IssueDateType();
        issueDateType.setValue(DateUtils.now());

        return issueDateType;
    }

    public static IDType id(String id) {
        IDType idType = new IDType();
        idType.setValue(id);

        return idType;
    }

    public static UUIDType uuid(String uuid) {
        UUIDType uuidType = new UUIDType();
        uuidType.setValue(uuid);

        return uuidType;
    }

    public static ProfileIDType profileId(String profile) {
        ProfileIDType profileIDType = new ProfileIDType();
        profileIDType.setValue(profile);

        return profileIDType;
    }

    public static NoteType note(String note) {
        NoteType noteType = new NoteType();
        noteType.setValue(note);

        return noteType;
    }
    
    public static NameType name(String name) {
        NameType nameType = new NameType();
        nameType.setValue(name);
        
        return nameType;
    }
    
    public static DocumentTypeType documentType(String type) {
        DocumentTypeType documentTypeType = new DocumentTypeType();
        documentTypeType.setValue(type);
        
        return documentTypeType;
    }

    public static DocumentTypeCodeType documentTypeCode(String type) {
        DocumentTypeCodeType DocumentTypeCodeType = new DocumentTypeCodeType();
        DocumentTypeCodeType.setValue(type);

        return DocumentTypeCodeType;
    }

    public static CityNameType city(String name) {
        CityNameType city = new CityNameType();
        city.setValue(name);

        return city;
    }

    public static CountryType country(String name) {
        CountryType country = new CountryType();
        country.setName(name(name));

        return country;
    }

    public static PostalZoneType postalZone(String value) {
        PostalZoneType postalZone = new PostalZoneType();
        postalZone.setValue(value);

        return postalZone;
    }

    public static CitySubdivisionNameType citySub(String name) {
        CitySubdivisionNameType sub = new CitySubdivisionNameType();
        sub.setValue(name);

        return sub;
    }

    public static PartyType party(String partyName, String city, String street, String citySub, String postalZone, String country, String taxScheme, String phone, String fax, String email, String vkn) {
        PartyType party = new PartyType();

        // website url
        WebsiteURIType websiteURIType = new WebsiteURIType();
        websiteURIType.setValue("https://www.izibiz.com.tr");

        party.setWebsiteURI(websiteURIType);

        // party name
        PartyNameType partyNameType = new PartyNameType();
        NameType nameType = new NameType();
        nameType.setValue(partyName);
        partyNameType.setName(nameType);

        party.setPartyName(partyNameType);

        // postal address
        AddressType addressType = new AddressType();
        StreetNameType streetNameType = new StreetNameType();
        streetNameType.setValue(street);
        addressType.setStreetName(streetNameType);

        CitySubdivisionNameType citySubdivisionNameType = new CitySubdivisionNameType();
        citySubdivisionNameType.setValue(citySub);
        addressType.setCitySubdivisionName(citySubdivisionNameType);

        CityNameType cityNameType = new CityNameType();
        cityNameType.setValue(city);
        addressType.setCityName(cityNameType);

        PostalZoneType postalZoneType = new PostalZoneType();
        postalZoneType.setValue(postalZone);
        addressType.setPostalZone(postalZoneType);

        CountryType countryType = new CountryType();
        NameType countryNameType = new NameType();
        countryNameType.setValue(country);
        countryType.setName(countryNameType);
        addressType.setCountry(countryType);

        party.setPostalAddress(addressType);

        // party identification
        PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
        IDType idType = new IDType();
        idType.setSchemeID("VKN");
        idType.setValue(vkn);
        partyIdentificationType.setID(idType);

        party.getPartyIdentification().add(partyIdentificationType);

        // party tax scheme
        PartyTaxSchemeType partyTaxSchemeType = new PartyTaxSchemeType();
        TaxSchemeType taxSchemeType = new TaxSchemeType();
        NameType taxSchemeNameType = new NameType();
        taxSchemeNameType.setValue(taxScheme);
        taxSchemeType.setName(taxSchemeNameType);
        partyTaxSchemeType.setTaxScheme(taxSchemeType);

        party.setPartyTaxScheme(partyTaxSchemeType);

        // party contact
        party.setContact(new ContactType());
        TelephoneType tel = new TelephoneType();
        tel.setValue(phone);
        party.getContact().setTelephone(tel);

        TelefaxType telefaxType = new TelefaxType();
        telefaxType.setValue(fax);
        party.getContact().setTelefax(telefaxType);

        ElectronicMailType electronicMailType = new ElectronicMailType();
        electronicMailType.setValue(email);
        party.getContact().setElectronicMail(electronicMailType);

        return party;
    }

    public static PartyType defaultParty() {
        return party("İZİBİZ BİLİŞİM TEKNOLOJİLERİ AŞ", "İSTANBUL", "Yıldız Teknik üniversitesi Teknopark B Blok Kat:2 No:412 Davutpaşa -Esenler /İstanbu", "ATABEY", "34521", "Turkey", "DAVUTPAŞA", "2122121212", "21211111111", "defaultgb@izibiz.com.tr", "4840847211");
    }

    public static SignatureType signature(String id) {
        SignatureType signatureType = new SignatureType();

        // id
        IDType idType = new IDType();
        idType.setSchemeID("VKN_TCKN");
        idType.setValue("4840847211");
        signatureType.setID(idType);

        // signature party
        PartyType party = party("akbil teknolojileri","ANKARA","DENEME ADRES BİLGİLERİ","KAHRAMANKAZAN","","TÜRKİYE","KURUMLAR","(800) 811-1199","","yazilim@izibiz.com.tr","4840847211");
        signatureType.setSignatoryParty(party);

        // attachment
        AttachmentType attachmentType = new AttachmentType();
        ExternalReferenceType externalReferenceType = new ExternalReferenceType();
        URIType uriType = new URIType();

        uriType.setValue("#Signature_"+id);
        externalReferenceType.setURI(uriType);
        attachmentType.setExternalReference(externalReferenceType);

        signatureType.setDigitalSignatureAttachment(attachmentType);

        return signatureType;
    }
    
    public static AttachmentType xsltTemplate(String id, String embedded) {
        AttachmentType attachmentType = new AttachmentType();

        System.out.println(embedded.length());

        attachmentType.setEmbeddedDocumentBinaryObject(new EmbeddedDocumentBinaryObjectType());
        attachmentType.getEmbeddedDocumentBinaryObject().setCharacterSetCode("UTF-8");
        attachmentType.getEmbeddedDocumentBinaryObject().setEncodingCode("base64");
        attachmentType.getEmbeddedDocumentBinaryObject().setFilename(id + ".xslt");
        attachmentType.getEmbeddedDocumentBinaryObject().setMimeCode("application/xml");

        attachmentType.getEmbeddedDocumentBinaryObject().setValue(embedded.getBytes(StandardCharsets.UTF_8));

        return attachmentType;
    }

    public static TaxTotalType taxTotal(double taxAmount, List<TaxSubtotalType> subTotals) {
        TaxTotalType taxTotal = new TaxTotalType();

        TaxAmountType amount = new TaxAmountType();
        amount.setValue(BigDecimal.valueOf(taxAmount));
        amount.setCurrencyID("TRY");

        taxTotal.setTaxAmount(amount);

        taxTotal.getTaxSubtotal().addAll(subTotals);

        return taxTotal;
    }

    public static TaxSubtotalType subTotal(double taxableAmount, double taxAmount, int calculationSequenceNumeric, double percent, String taxScheme, String taxTypeCode) {
        TaxSubtotalType subTotal = new TaxSubtotalType();

        TaxableAmountType taxable = new TaxableAmountType();
        taxable.setCurrencyID("TRY");
        taxable.setValue(BigDecimal.valueOf(taxableAmount));

        subTotal.setTaxableAmount(taxable);

        TaxAmountType amount = new TaxAmountType();
        amount.setValue(BigDecimal.valueOf(taxAmount));
        amount.setCurrencyID("TRY");

        subTotal.setTaxAmount(amount);

        CalculationSequenceNumericType calculationSequence = new CalculationSequenceNumericType();
        calculationSequence.setValue(BigDecimal.valueOf(calculationSequenceNumeric));

        subTotal.setCalculationSequenceNumeric(calculationSequence);

        PercentType percentType = new PercentType();
        percentType.setFormat("%");
        percentType.setValue(BigDecimal.valueOf(percent));

        subTotal.setPercent(percentType);

        TaxCategoryType category = new TaxCategoryType();
        TaxSchemeType scheme = new TaxSchemeType();
        TaxTypeCodeType code = new TaxTypeCodeType();

        code.setValue(taxTypeCode);

        scheme.setName(name(taxScheme));
        scheme.setTaxTypeCode(code);

        category.setTaxScheme(scheme);

        subTotal.setTaxCategory(category);

        return subTotal;
    }

    public static MonetaryTotalType legalMonetaryTotal(double lineExtensionAmount, double taxExclusiveAmount, double taxInclusiveAmount, double payableAmount, double allowanceTotal) {
        MonetaryTotalType monetaryTotal = new MonetaryTotalType();

        monetaryTotal.setLineExtensionAmount(new LineExtensionAmountType());
        monetaryTotal.setTaxExclusiveAmount(new TaxExclusiveAmountType());
        monetaryTotal.setTaxInclusiveAmount(new TaxInclusiveAmountType());
        monetaryTotal.setPayableAmount(new PayableAmountType());
        monetaryTotal.setAllowanceTotalAmount(new AllowanceTotalAmountType());

        monetaryTotal.getLineExtensionAmount().setCurrencyID("TRY");
        monetaryTotal.getLineExtensionAmount().setValue(BigDecimal.valueOf(lineExtensionAmount));

        monetaryTotal.getTaxExclusiveAmount().setValue(BigDecimal.valueOf(taxExclusiveAmount));
        monetaryTotal.getTaxExclusiveAmount().setCurrencyID("TRY");

        monetaryTotal.getTaxInclusiveAmount().setValue(BigDecimal.valueOf(taxInclusiveAmount));
        monetaryTotal.getTaxInclusiveAmount().setCurrencyID("TRY");

        monetaryTotal.getPayableAmount().setValue(BigDecimal.valueOf(payableAmount));
        monetaryTotal.getPayableAmount().setCurrencyID("TRY");

        monetaryTotal.getAllowanceTotalAmount().setValue(BigDecimal.valueOf(allowanceTotal));
        monetaryTotal.getAllowanceTotalAmount().setCurrencyID("TRY");

        return monetaryTotal;
    }
}
