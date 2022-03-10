package com.edonusum.client.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
}
