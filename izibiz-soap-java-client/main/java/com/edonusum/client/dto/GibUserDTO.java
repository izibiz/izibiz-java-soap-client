package com.edonusum.client.dto;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "", name = "USER")
public class GibUserDTO {
    @XmlElement(name = "IDENTIFIER")
    protected String identifier;

    @XmlElement(name = "ALIAS")
    protected String alias;

    @XmlElement(name = "TITLE")
    protected String title;

    @XmlElement(name = "TYPE")
    protected String type;

    @XmlElement(name = "REGISTER_TIME")
    protected String registertime;

    @XmlElement(name = "UNIT")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String unit;

    @XmlElement(name = "ALIAS_CREATION_TIME")
    protected String aliascreationtime;

    @XmlElement(name = "ACCOUNT_TYPE")
    protected String accounttype;

    @XmlElement(name = "DELETED")
    protected String deleted;

    @XmlElement(name = "ALIAS_DELETION_TIME")
    protected String aliasdeletiontime;

    @XmlElement(name = "DOCUMENT_TYPE")
    protected String documenttype;

    public String getIDENTIFIER() {
        return this.identifier;
    }

    public void setIDENTIFIER(String value) {
        this.identifier = value;
    }

    public String getALIAS() {
        return this.alias;
    }

    public void setALIAS(String value) {
        this.alias = value;
    }

    public String getTITLE() {
        return this.title;
    }

    public void setTITLE(String value) {
        this.title = value;
    }

    public String getTYPE() {
        return this.type;
    }

    public void setTYPE(String value) {
        this.type = value;
    }

    public String getREGISTERTIME() {
        return this.registertime;
    }

    public void setREGISTERTIME(String value) {
        this.registertime = value;
    }

    public String getUNIT() {
        return this.unit;
    }

    public void setUNIT(String value) {
        this.unit = value;
    }

    public String getALIASCREATIONTIME() {
        return this.aliascreationtime;
    }

    public void setALIASCREATIONTIME(String value) {
        this.aliascreationtime = value;
    }

    public String getACCOUNTTYPE() {
        return this.accounttype;
    }

    public void setACCOUNTTYPE(String value) {
        this.accounttype = value;
    }

    public String getDELETED() {
        return this.deleted;
    }

    public void setDELETED(String value) {
        this.deleted = value;
    }

    public String getALIASDELETIONTIME() {
        return this.aliasdeletiontime;
    }

    public void setALIASDELETIONTIME(String value) {
        this.aliasdeletiontime = value;
    }

    public String getDOCUMENTTYPE() {
        return this.documenttype;
    }

    public void setDOCUMENTTYPE(String value) {
        this.documenttype = value;
    }
}
