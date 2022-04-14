package com.edonusum.client.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/*
* Buradaki işlemler InputStream ve FileOutputStream kullanılarak byte array şeklindeki veriyi
* yazma ve okuma şeklinde bir algoritma ile yapılmıştır. Apachee ArchiveStream kütüphanesi
* kullanılarak daha iyi bir yoldan yapılabilir.
 */
public class ZipUtils {
    private static final String DEFAULT_EXTENSION = ".xml";
    private ZipUtils(){}

    public static List<File> unzipAllFiles(ZipFile zf, String pathToExtract) throws IOException {
        Enumeration<? extends ZipEntry> entries = zf.entries();

        String ext = "";
        List<File> extractedFiles = new ArrayList<>();
        ZipEntry entry;
        String entryFileName;
        while(entries.hasMoreElements()) {
             entry = entries.nextElement();

            if(! entry.getName().contains(".")) {
                ext = DEFAULT_EXTENSION;
            }

            entryFileName = pathToExtract + "\\" + entry.getName()+ext;

            try(InputStream inputStream = zf.getInputStream(entry);
                FileOutputStream fileOutputStream = new FileOutputStream(entryFileName)) {
                inputStream.transferTo(fileOutputStream);
                extractedFiles.add(new File(entryFileName));
            }
        }
        return extractedFiles;
    }

    public static List<File> unzipDefault(File f) throws Exception{
        if(f.getName().endsWith(".zip")) {
            ZipFile zf = new ZipFile(f);

            List<File> extractedFiles = new ArrayList<>();

            String path = f.getParent();

            extractedFiles.addAll(unzipAllFiles(zf, path));

            return extractedFiles;
        }
        return List.of();
    }

    public static List<File> unzipMultiple(List<File> files) throws Exception{
        List<File> extractedFiles = new ArrayList<>();

        for(File f : files) {
            if(f.getName().endsWith(".zip")) {
                extractedFiles.addAll(unzipDefault(f));
            }
        }

        return extractedFiles;
    }

    public static ZipFile base64ToZip(byte[] base64, String path, String fileName) {
        try {
            Path file = Files.createDirectories(Paths.get(path+"\\"));

            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file+"\\"+fileName), base64.length);
            outputStream.write(base64);
            ZipFile zipFile = new ZipFile(path+"\\"+fileName);

            outputStream.close();
            return zipFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
