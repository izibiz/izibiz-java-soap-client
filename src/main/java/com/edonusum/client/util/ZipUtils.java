package com.edonusum.client.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public final class ZipUtils {
    private ZipUtils(){}

    private static final String DEFAULT_EXTENSION = ".xml";

    /**
     * Default ayarlar ile tek bir dosyayı unzip yapmak için kullanılır.
     * @param f Unzip edilmek istenen dosya
     * @return Verilen zip'ten çıkartılan dosyalar
     * @throws Exception
     */
    public static List<File> unzipDefault(File f) throws Exception{
        if(f.getName().endsWith(".zip")) {
            List<File> extractedFiles = new ArrayList<>();
            extractedFiles.addAll(unzipWithCommons(f));

            return extractedFiles;
        }
        return List.of();
    }

    /**
     * Default ayarlar ile birden fazla dosyayı unzip yapmak için kullanılır.
     * @param files Unzip edilmek istenen zip dosyaları
     * @return Unzip edilen tüm dosyaların tutulduğu liste
     * @throws Exception
     */
    public static List<File> unzipMultiple(List<File> files) throws Exception{
        List<File> extractedFiles = new ArrayList<>();

        for(File f : files) {
            if(f.getName().endsWith(".zip")) {
                extractedFiles.addAll(unzipDefault(f));
            }
        }

        return extractedFiles;
    }

    private static List<File> unzipWithCommons(final File zip) throws Exception {
        try(ZipArchiveInputStream input = new ZipArchiveInputStream(new FileInputStream(zip))) {
            ZipArchiveEntry entry;
            String defaultUnzipPath;
            List<File> unzipped = new ArrayList<>();
            while((entry = input.getNextZipEntry()) != null) {
                defaultUnzipPath = zip.getParent() + "\\" + entry.getName();
                IOUtils.copy(input, new FileOutputStream(defaultUnzipPath));
                unzipped.add(new File(defaultUnzipPath));
            }

            return unzipped;
        }
    }

    public static File zipWithCommons(final List<File> filesToZip, final File zip) throws Exception {
        try(ZipArchiveOutputStream output = new ZipArchiveOutputStream(zip)) {
            ZipArchiveEntry entry;
            for(File file : filesToZip) {
                try(FileInputStream inputStream = new FileInputStream(file)) {
                    entry = new ZipArchiveEntry(file.getName().substring(file.getName().lastIndexOf("/")));

                    output.putArchiveEntry(entry);

                    IOUtils.copy(inputStream, output);

                    output.closeArchiveEntry();
                    output.finish();
                }
            }

            return zip;
        }
    }

    @Deprecated
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

}
