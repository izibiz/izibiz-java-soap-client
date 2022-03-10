package com.edonusum.client.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {
    private ZipUtils(){}

    public static void UnZipAllFiles(ZipFile zf, String pathToExtract) throws IOException {
        Enumeration<? extends ZipEntry> entries = zf.entries();
        InputStream is;
        FileOutputStream fis;
        ZipEntry entry;
        while(entries.hasMoreElements()) {
            entry = entries.nextElement();
            is = zf.getInputStream(entry);
            fis = new FileOutputStream(pathToExtract+entry.getName());

            is.transferTo(fis);
        }
    }

    public static String base64ToZip(byte[] base64, String path) {
        try {
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path+"user.zip"), base64.length);
            bos.write(base64);
            ZipFile zf = new ZipFile(path+"user.zip");

            UnZipAllFiles(zf, path);

            return "Created files: " + zf.getName();

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
