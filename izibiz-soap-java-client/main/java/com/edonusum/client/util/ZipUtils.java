package com.edonusum.client.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;

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

    public static byte[] zipToBase64(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);

        byte[] b64content = fis.readAllBytes();

        return b64content;
    }

    public static byte[] unzipWithCommons(InputStream is) {
        ArchiveInputStream stream = null;
        byte[] bytes = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            stream = factory.createArchiveInputStream(ArchiveStreamFactory.ZIP, is);
            ZipArchiveEntry entry = (ZipArchiveEntry) stream.getNextEntry();

            bytes = IOUtils.toByteArray(stream);
            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e2) {
                    System.out.println("error");
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (Exception e2) {
                    System.out.println("error");
                }
            }
        }
    }
}
