package com.edonusum.client.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://schemas.i2i.com/ei/wsdl", name = "GetUserListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GibUsers {

    @XmlElement(name = "USER")
    private GibUserDTO[] users;

    public GibUserDTO[] getUsers() {
        return users;
    }

    public void setUsers(GibUserDTO[] users) {
        this.users = users;
    }
}
