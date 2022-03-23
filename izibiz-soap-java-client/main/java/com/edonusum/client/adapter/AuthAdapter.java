package com.edonusum.client.adapter;

import com.edonusum.client.dto.GibUserDTO;
import com.edonusum.client.dto.GibUsers;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.auth.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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

    public GetGibUserListResponse getGibUserList(GetGibUserListRequest request) throws IOException, JAXBException {
        JAXBElement<GetGibUserListResponse> response = (JAXBElement<GetGibUserListResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(URL, of.createGetGibUserListRequest(request));

        String pathToFile = PATH_TO_DOCUMENTS + "\\auth\\getGibUserList\\";
        ZipFile file = ZipUtils.base64ToZip(response.getValue().getCONTENT().getValue(), pathToFile, "users.zip");
        ZipUtils.UnZipAllFiles(file,pathToFile);

        GibUsers users = (GibUsers) unmarshaller(GibUsers.class).unmarshal(new File(pathToFile+"\\users"));
        List<GibUserDTO> userList = users.getUsers();
        System.out.println(userList.get(0).getTITLE());

        return response.getValue();
    }

    public CheckUserResponse checkUser(CheckUserRequest request) {
        JAXBElement<CheckUserResponse> response = (JAXBElement<CheckUserResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(URL,of.createCheckUserRequest(request));

        return response.getValue();
    }
}
