package com.edonusum.client.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(namespace = "http://schemas.i2i.com/ei/wsdl", name = "GetUserListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GibUsers {

    @XmlElement(name = "USER", type = GibUserDTO.class)
    private List<GibUserDTO> users;

    public List<GibUserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<GibUserDTO> users) {
        this.users = users;
    }
}
