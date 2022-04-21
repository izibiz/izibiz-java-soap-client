package com.edonusum.client.adapter;

import com.edonusum.client.util.FileUtils;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.smm.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SmmAdapter extends Adapter{
    private static final String URL_ENDPOINT = URL + "/SmmWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.smm";
    private static final String DOCUMENTS_DIR = PATH_TO_DOCUMENTS + "\\smm";
    private final ObjectFactory of;

    public SmmAdapter() {
        setContextPath(CONTEXT_PATH);
        of = new ObjectFactory();
    }

    private boolean isCompressed(REQUESTHEADERType header) {
        return (null == header.getCOMPRESSED() || "Y".equals(header.getCOMPRESSED()));
    }

    public GetSmmResponse getSmm(GetSmmRequest request) throws Exception{
        JAXBElement<GetSmmResponse> respObj = (JAXBElement<GetSmmResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetSmmRequest(request));

        String dir = DOCUMENTS_DIR + "\\getSmm";
        String ext = "xml";

        if(isCompressed(request.getREQUESTHEADER())) ext = "zip";

        List<byte[]> contents = respObj.getValue().getSMM().stream().map(smm -> smm.getCONTENT().getValue()).collect(Collectors.toList());
        List<File> files = FileUtils.writeToFile(contents, dir, "smm", ext);

        if("zip".equals(ext)) ZipUtils.unzipMultiple(files);

        return respObj.getValue();
    }

    public GetSmmStatusResponse getSmmStatus(GetSmmStatusRequest request) {
        JAXBElement<GetSmmStatusResponse> respObj = (JAXBElement<GetSmmStatusResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetSmmStatusRequest(request));

        return respObj.getValue();
    }

    public GetSmmReportResponse getSmmReport(GetSmmReportRequest request) {
        JAXBElement<GetSmmReportResponse> respObj = (JAXBElement<GetSmmReportResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createGetSmmReportRequest(request));

        return respObj.getValue();
    }

    public LoadSmmResponse loadSmm(LoadSmmRequest request) {
        JAXBElement<LoadSmmResponse> respObj = (JAXBElement<LoadSmmResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createLoadSmmRequest(request));

        return respObj.getValue();
    }

    public SendSmmResponse sendSmm(SendSmmRequest request) {
        JAXBElement<SendSmmResponse> respObj = (JAXBElement<SendSmmResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createSendSmmRequest(request));

        return respObj.getValue();
    }

    public CancelSmmResponse cancelSmm(CancelSmmRequest request) {
        JAXBElement<CancelSmmResponse> respObj = (JAXBElement<CancelSmmResponse>)
                getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createCancelSmmRequest(request));

        return respObj.getValue();
    }
}
