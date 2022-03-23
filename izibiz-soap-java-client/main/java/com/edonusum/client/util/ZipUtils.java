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
            fis = new FileOutputStream(pathToExtract+"\\"+entry.getName());

            is.transferTo(fis);
        }
    }

    public static ZipFile base64ToZip(byte[] base64, String path, String fileName) {
        try {
            File file = new File(path+"\\");
            file.mkdirs();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file.getPath()+"\\"+fileName), base64.length);
            bos.write(base64);
            ZipFile zf = new ZipFile(path+"\\"+fileName);

            return zf;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
