package com.edonusum.client.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {
    private static final String DEFAULT_EXTENSION = ".xml";
    private ZipUtils(){}

    public static void unzipAllFiles(ZipFile zf, String pathToExtract) throws IOException {
        Enumeration<? extends ZipEntry> entries = zf.entries();

        String ext = "";
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if(! entry.getName().contains(".")) {
                ext = DEFAULT_EXTENSION;
            }

            try(InputStream inputStream = zf.getInputStream(entry);
                FileOutputStream fileOutputStream = new FileOutputStream(pathToExtract+"\\"+entry.getName()+ext)) {
                inputStream.transferTo(fileOutputStream);
            }
        }
    }

    public static void unzipDefault(File f) throws Exception{
        if(f.getName().endsWith(".zip")) {
            ZipFile zf = new ZipFile(f);

            String path = f.getParent();

            unzipAllFiles(zf, path);
        }
    }

    public static void unzipMultiple(List<File> files) throws Exception{
        for(File f : files) {
            if(f.getName().endsWith(".zip")) {
                ZipFile zf = new ZipFile(f);
                unzipDefault(f);
            }
        }
    }

    public static ZipFile base64ToZip(byte[] base64, String path, String fileName) {
        try {
            Path file = Files.createDirectories(Paths.get(path+"\\"));

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file+"\\"+fileName), base64.length);
            bos.write(base64);
            ZipFile zf = new ZipFile(path+"\\"+fileName);

            bos.close();
            return zf;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
