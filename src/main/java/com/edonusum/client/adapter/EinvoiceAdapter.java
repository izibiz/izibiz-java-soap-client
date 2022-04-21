package com.edonusum.client.adapter;

import com.edonusum.client.util.FileUtils;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.einvoice.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EinvoiceAdapter extends Adapter{
    private static final String URL_ENDPOINT = URL + "/EInvoiceWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.einvoice";
    private static final String DOCUMENTS_DIR = PATH_TO_DOCUMENTS+"\\einvoice";
    private final ObjectFactory of;

    public EinvoiceAdapter() {
        setContextPath(CONTEXT_PATH);
        of = new ObjectFactory();
    }

    public SendInvoiceResponse sendInvoice(SendInvoiceRequest sendInvoiceRequest) {
        JAXBElement<SendInvoiceResponse> respObj = (JAXBElement<SendInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createSendInvoiceRequest(sendInvoiceRequest));

        return respObj.getValue();
    }

    public GetInvoiceResponse getInvoice(GetInvoiceRequest getInvoiceRequest) throws Exception {
        JAXBElement<GetInvoiceResponse> respObj = (JAXBElement<GetInvoiceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetInvoiceRequest(getInvoiceRequest));

        String dir = DOCUMENTS_DIR+"\\getInvoice\\";

        JAXB.marshal(respObj.getValue(), FileUtils.createDirectoryAndFile(dir, "invoices.xml")); // marshall invoice list to single xml

        String extension = ("Y".equals(getInvoiceRequest.getREQUESTHEADER().getCOMPRESSED()) || null == getInvoiceRequest.getREQUESTHEADER().getCOMPRESSED()) ? "zip" : "xml";

        List<byte[]> contentList = respObj.getValue().getINVOICE().stream().map(inv -> inv.getCONTENT().getValue()).collect(Collectors.toList());
        List<File> files = FileUtils.writeToFile(contentList, dir, "invoice", extension);

        if ("zip".equals(extension)) {
            files = ZipUtils.unzipMultiple(files); // assign extracted files to variable files
        }

        List<InvoiceType> invoices = new ArrayList<>();
        for(File xml : files) {
            invoices.add(JAXB.unmarshal(xml, InvoiceType.class));
        }

        // TODO: do business with invoices

        return respObj.getValue();
    }

    public GetInvoiceWithTypeResponse getInvoiceWithType(GetInvoiceWithTypeRequest request) throws Exception{
        JAXBElement<GetInvoiceWithTypeResponse> respObj = (JAXBElement<GetInvoiceWithTypeResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetInvoiceWithTypeRequest(request));

        String dir = DOCUMENTS_DIR+"\\getInvoiceWithType\\";

        JAXB.marshal(respObj.getValue(),FileUtils.createDirectoryAndFile(dir, "invoices_with_type.xml"));

        String extension;

        if("Y".equals(request.getREQUESTHEADER().getCOMPRESSED()) || null == request.getREQUESTHEADER().getCOMPRESSED()) {
            extension = "zip";
        } else {
            extension = request.getINVOICESEARCHKEY().getTYPE();
        }

        List<byte[]> contentList = respObj.getValue().getINVOICE().stream().map(inv -> inv.getCONTENT().getValue()).collect(Collectors.toList());
        List<File> files = FileUtils.writeToFile(contentList, dir, "invoice", extension);

        if ("zip".equals(extension)) {
            files = ZipUtils.unzipMultiple(files); // assign extracted files to variable files
        }

        // TODO: do business with invoices

        return respObj.getValue();
    }

    public LoadInvoiceResponse loadInvoice(LoadInvoiceRequest loadInvoiceRequest) {
        JAXBElement<LoadInvoiceResponse> respObj = (JAXBElement<LoadInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createLoadInvoiceRequest(loadInvoiceRequest));

        return respObj.getValue();
    }

    public MarkInvoiceResponse markInvoice(MarkInvoiceRequest markInvoiceRequest) {
        JAXBElement<MarkInvoiceResponse> respObj = (JAXBElement<MarkInvoiceResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createMarkInvoiceRequest(markInvoiceRequest));

        return respObj.getValue();
    }

    public GetInvoiceStatusAllResponse getInvoiceStatusAll(GetInvoiceStatusAllRequest getInvoiceRequest) {
        JAXBElement<GetInvoiceStatusAllResponse> respObj = (JAXBElement<GetInvoiceStatusAllResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetInvoiceStatusAllRequest(getInvoiceRequest));

        return respObj.getValue();
    }

    public SendInvoiceResponseWithServerSignResponse sendInvoiceResponseWithServerSign(SendInvoiceResponseWithServerSignRequest request) {
        JAXBElement<SendInvoiceResponseWithServerSignResponse> respObj = (JAXBElement<SendInvoiceResponseWithServerSignResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createSendInvoiceResponseWithServerSignRequest(request));

        return respObj.getValue();
    }

    public GetInvoiceStatusResponse getInvoiceStatus(GetInvoiceStatusRequest request) {
        JAXBElement<GetInvoiceStatusResponse> respObj = (JAXBElement<GetInvoiceStatusResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetInvoiceStatusRequest(request));

        return respObj.getValue();
    }

}
