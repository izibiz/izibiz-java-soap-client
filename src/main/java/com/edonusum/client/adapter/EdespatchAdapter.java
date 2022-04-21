package com.edonusum.client.adapter;

import com.edonusum.client.util.FileUtils;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.edespatch.*;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.receiptadvice_2.ReceiptAdviceType;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EdespatchAdapter extends Adapter{
    private static final String URL_ENDPOINT = URL + "/EIrsaliye";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.edespatch";
    private static final String DOCUMENTS_DIR = PATH_TO_DOCUMENTS + "\\edespatch";
    private final ObjectFactory of;

    public EdespatchAdapter() {
        of = new ObjectFactory();
        setContextPath(CONTEXT_PATH);
    }

    private boolean isCompressed(REQUESTHEADERType header) {
        return ("Y".equals(header.getCOMPRESSED()) || null == header.getCOMPRESSED());
    }

    public GetDespatchAdviceStatusResponse getDespatchAdviseStatus(GetDespatchAdviceStatusRequest request) {
        JAXBElement<GetDespatchAdviceStatusResponse> respObj = (JAXBElement<GetDespatchAdviceStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetDespatchAdviceStatusRequest(request));

        return respObj.getValue();
    }

    public GetDespatchAdviceResponse getDespatchAdvice(GetDespatchAdviceRequest request) throws Exception {
        JAXBElement<GetDespatchAdviceResponse> respObj = (JAXBElement<GetDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetDespatchAdviceRequest(request));

        String path = DOCUMENTS_DIR + "\\getDespatchAdvice\\";

        if(null == request.getSEARCHKEY().getCONTENTTYPE()) request.getSEARCHKEY().setCONTENTTYPE(CONTENTTYPE.XML);

        String ext = isCompressed(request.getREQUESTHEADER()) ? "zip" : request.getSEARCHKEY().getCONTENTTYPE().value();

        List<byte[]> contents = respObj.getValue().getDESPATCHADVICE().stream().map(despatch -> despatch.getCONTENT().getValue()).collect(Collectors.toList());
        List<File> files = FileUtils.writeToFile(contents, path, "despatch_advice", ext);

        if(ext.equals("zip")) {
            files = ZipUtils.unzipMultiple(files); // extracted files
        }

        if(request.getSEARCHKEY().getCONTENTTYPE() == CONTENTTYPE.XML && ! files.isEmpty()) {
            List<DespatchAdviceType> despatchAdviceList = new ArrayList<>(); // irsaliye listesi
            for(File xml : files) {
                despatchAdviceList.add(JAXB.unmarshal(xml, DespatchAdviceType.class));
            }
        }

        // TODO: do business with despatch advice list

        return respObj.getValue();
    }

    public LoadDespatchAdviceResponse loadDespatchAdvice(LoadDespatchAdviceRequest request) {
        JAXBElement<LoadDespatchAdviceResponse> respObj = (JAXBElement<LoadDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createLoadDespatchAdviceRequest(request));

        return respObj.getValue();
    }

    public SendDespatchAdviceResponse sendDespatchAdvice(SendDespatchAdviceRequest request) {
        JAXBElement<SendDespatchAdviceResponse> respObj = (JAXBElement<SendDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createSendDespatchAdviceRequest(request));

        return respObj.getValue();
    }

    public MarkDespatchAdviceResponse markDespatchAdvice(MarkDespatchAdviceRequest request) {
        JAXBElement<MarkDespatchAdviceResponse> respObj = (JAXBElement<MarkDespatchAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createMarkDespatchAdviceRequest(request));

        return respObj.getValue();
    }

    public SendReceiptAdviceResponse sendReceiptAdvice(SendReceiptAdviceRequest request) {
        JAXBElement<SendReceiptAdviceResponse> respObj = (JAXBElement<SendReceiptAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createSendReceiptAdviceRequest(request));

        return respObj.getValue();
    }

    public LoadReceiptAdviceResponse loadReceiptAdvice(LoadReceiptAdviceRequest request) {
        JAXBElement<LoadReceiptAdviceResponse> respObj = (JAXBElement<LoadReceiptAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createLoadReceiptAdviceRequest(request));

        return respObj.getValue();
    }

    public GetReceiptAdviceResponse getReceiptAdvice(GetReceiptAdviceRequest request) throws Exception{
        JAXBElement<GetReceiptAdviceResponse> respObj = (JAXBElement<GetReceiptAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetReceiptAdviceRequest(request));

        String path = DOCUMENTS_DIR + "\\getReceiptAdvice\\";

        if(null == request.getSEARCHKEY().getCONTENTTYPE()) request.getSEARCHKEY().setCONTENTTYPE(CONTENTTYPE.XML);

        String ext = isCompressed(request.getREQUESTHEADER()) ? "zip" : request.getSEARCHKEY().getCONTENTTYPE().value();

        List<byte[]> contents = respObj.getValue().getRECEIPTADVICE().stream().map(despatch -> despatch.getCONTENT().getValue()).collect(Collectors.toList());
        List<File> files = FileUtils.writeToFile(contents, path, "receipt_advice", ext);

        if(ext.equals("zip")) {
            files = ZipUtils.unzipMultiple(files); // extracted files
        }

        if(request.getSEARCHKEY().getCONTENTTYPE() == CONTENTTYPE.XML && ! files.isEmpty()) {
            List<ReceiptAdviceType> receiptAdviceList = new ArrayList<>();
            for(File xml : files) {
                receiptAdviceList.add(JAXB.unmarshal(xml, ReceiptAdviceType.class));
            }
        }

        // TODO: do business with receipt advice list

        return respObj.getValue();
    }

    public GetReceiptAdviceStatusResponse getReceiptAdviceStatus(GetReceiptAdviceStatusRequest request) {
        JAXBElement<GetReceiptAdviceStatusResponse> respObj = (JAXBElement<GetReceiptAdviceStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetReceiptAdviceStatusRequest(request));

        return respObj.getValue();
    }

    public MarkReceiptAdviceResponse markReceiptAdvice(MarkReceiptAdviceRequest request) {
        JAXBElement<MarkReceiptAdviceResponse> respObj = (JAXBElement<MarkReceiptAdviceResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createMarkReceiptAdviceRequest(request));

        return respObj.getValue();
    }
}
