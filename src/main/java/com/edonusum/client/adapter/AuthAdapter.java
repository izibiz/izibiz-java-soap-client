package com.edonusum.client.adapter;

import com.edonusum.client.dto.GibUserDTO;
import com.edonusum.client.dto.GibUsers;
import com.edonusum.client.util.FileUtils;
import com.edonusum.client.util.ZipUtils;
import com.edonusum.client.wsdl.auth.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.util.List;

@Component
public class AuthAdapter extends Adapter {

    private static final String URL_ENDPOINT = URL + "/AuthenticationWS";
    private static final String CONTEXT_PATH = "com.edonusum.client.wsdl.auth";
    private static final String DOCUMENTS_DIR = PATH_TO_DOCUMENTS + "\\auth";

    private final ObjectFactory of;

    public AuthAdapter() {
        of = new ObjectFactory();
        setContextPath(CONTEXT_PATH);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        JAXBElement<LoginRequest> finalRequest = of.createLoginRequest(loginRequest);
        JAXBElement<LoginResponse> resp = (JAXBElement<LoginResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, finalRequest);

        return resp.getValue();
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) {
        JAXBElement<LogoutResponse> respObj = (JAXBElement<LogoutResponse>) getWebServiceTemplate().marshalSendAndReceive(URL_ENDPOINT, of.createLogoutRequest(logoutRequest));

        return respObj.getValue();
    }

    public GetGibUserListResponse getGibUserList(GetGibUserListRequest request) throws Exception {
        JAXBElement<GetGibUserListResponse> response = (JAXBElement<GetGibUserListResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(URL_ENDPOINT, of.createGetGibUserListRequest(request));

        String pathToFile = DOCUMENTS_DIR + "\\getGibUserList";

        String extension = "xml";
        if("Y".equals(request.getREQUESTHEADER().getCOMPRESSED()) || null == request.getREQUESTHEADER().getCOMPRESSED()) {
            extension = "zip";
        }

        List<File> files = FileUtils.writeToFile(List.of(response.getValue().getCONTENT().getValue()),pathToFile,"users", extension);
        ZipUtils.unzipMultiple(files);

        GibUsers users = JAXB.unmarshal(files.get(0).getParent()+"\\users", GibUsers.class); // single xml file
        GibUserDTO [] userList = users.getUsers();
        System.out.println(userList.length);

        // TODO: do business with user list

        return response.getValue();
    }

    public CheckUserResponse checkUser(CheckUserRequest request) {
        JAXBElement<CheckUserResponse> response = (JAXBElement<CheckUserResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(URL_ENDPOINT,of.createCheckUserRequest(request));

        return response.getValue();
    }
}
