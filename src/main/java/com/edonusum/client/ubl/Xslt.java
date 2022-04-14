package com.edonusum.client.ubl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Xslt {
    private Xslt() {}

    public static String readInvoice() throws Exception{
        return Files.readString(Paths.get("txt\\invoice.txt"));
    }

    public static String readArchive() throws Exception {
        return Files.readString(Paths.get("txt\\archive.txt"));
    }

    public static String readCreditNote() throws Exception {
        return Files.readString(Paths.get("txt\\creditNote.txt"));
    }

    public static String readSmm() throws Exception {
        return Files.readString(Paths.get("txt\\smm.txt"));
    }

    public static String readDespatch() throws Exception {
        return Files.readString(Paths.get("txt\\despatch.txt"));
    }

    public static String readReceipt() throws Exception {
        return Files.readString(Paths.get("txt\\receipt.txt"));
    }

}

