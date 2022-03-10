package com.edonusum.client.adapter;

import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.auth.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.zip.ZipFile;

@Component
public class AuthAdapter extends Adapter {

    private static final String URL = "https://efaturatest.izibiz.com.tr:443/AuthenticationWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.auth";
    private ObjectFactory of;

    public AuthAdapter() {
        of = new ObjectFactory();

        setContextPath(CONTEXT_PATH);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        JAXBElement<LoginRequest> finalRequest = of.createLoginRequest(loginRequest);
        JAXBElement<LoginResponse> resp = (JAXBElement<LoginResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, finalRequest);

        return resp.getValue();
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) {
        JAXBElement<LogoutResponse> respObj = (JAXBElement<LogoutResponse>) getWebServiceTemplate().marshalSendAndReceive(URL, of.createLogoutRequest(logoutRequest));

        return respObj.getValue();
    }

    public String getGibUserList(GetGibUserListRequest request) {
        JAXBElement<GetGibUserListResponse> response = (JAXBElement<GetGibUserListResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(URL, of.createGetGibUserListRequest(request));

        try {
            byte[] content = response.getValue().getCONTENT().getValue();
            String pathToDocuments = System.getProperty("user.home")+"\\documents\\izibiz\\";

            File file = new File(pathToDocuments);
            if(!file.exists()) {
                file.mkdirs();
            }

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pathToDocuments+"user.zip"), content.length);
            bos.write(content);
            ZipFile zf = new ZipFile(pathToDocuments+"user.zip");

            ZipUtils.UnZipAllFiles(zf, pathToDocuments);

            return "Created files: " + zf.getName();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.getValue().getERRORTYPE().getERRORCODE()+"";
    }

    public CheckUserResponse checkUser(CheckUserRequest request) {
        JAXBElement<CheckUserResponse> response = (JAXBElement<CheckUserResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(URL,of.createCheckUserRequest(request));

        return response.getValue();
    }
}
