package com.edonusum.client.ubl;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * XSLT formatındaki görüntüleme şablonlarının okunmasını sağlayan metodlar burada bulunmaktadır.
 * Buradaki şablonlar döküman tipine göre her bir döküman içerisinde embedded olarak bulunmalıdır. Aksi taktirde şablon hatası alınacaktır.
 */
public class Xslt {
    private Xslt() {}

    private static final String PATH_PREFIX = "xslt\\";

    public static String readInvoice() throws Exception{
        return Files.readString(Paths.get(PATH_PREFIX + "invoice.txt"));
    }

    public static String readArchive() throws Exception {
        return Files.readString(Paths.get(PATH_PREFIX + "archive.txt"));
    }

    public static String readCreditNote() throws Exception {
        return Files.readString(Paths.get(PATH_PREFIX + "creditNote.txt"));
    }

    public static String readSmm() throws Exception {
        return Files.readString(Paths.get(PATH_PREFIX + "smm.txt"));
    }

    public static String readDespatch() throws Exception {
        return Files.readString(Paths.get(PATH_PREFIX + "despatch.txt"));
    }

    public static String readReceipt() throws Exception {
        return Files.readString(Paths.get(PATH_PREFIX + "receipt.txt"));
    }

}

