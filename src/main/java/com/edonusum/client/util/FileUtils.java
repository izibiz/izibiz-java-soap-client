package com.edonusum.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {

    private FileUtils() {}

    public static List<File> writeToFile(List<byte[]> contents, String basePath, String folderPrefix, String extension) throws Exception{
        List<File> files = new ArrayList<>();

        Path currentFile;
        int index = 1;
        String tempDir;
        for (byte[] content : contents) {
            if(contents.size() == 1) tempDir = basePath + "\\" + folderPrefix;
            else tempDir = basePath + "\\" + folderPrefix+"_"+index;
            currentFile = createDirectoryAndFile(tempDir, folderPrefix + "." + extension.toLowerCase()).toPath();

            try(FileOutputStream fileOutputStream = new FileOutputStream(currentFile.toFile())) {
                fileOutputStream.write(content);
                files.add(currentFile.toFile());
            }
            index++;
        }
        return files;
    }

    public static File createDirectoryAndFile(String dir, String fileName) throws Exception{
        Path path = Path.of(dir);
        if(Files.notExists(Path.of(dir))) {
            path = Files.createDirectories(Paths.get(dir));
        }

        File file = Path.of(path + "\\" + fileName).toFile();

        if(Files.notExists(file.toPath())) {
            Files.createFile(file.toPath());
        }

        return file;
    }
}
