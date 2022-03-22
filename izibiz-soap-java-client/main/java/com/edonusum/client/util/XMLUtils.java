package com.edonusum.client.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class XMLUtils {
    private XMLUtils(){}

    public static File createXmlFromDraft(File draft, UUID uuid, String invoiceId) throws IOException {
        String entireFile = Files.readString(draft.toPath());

        entireFile = entireFile
                .replaceAll("%UUID%", uuid.toString())
                .replaceAll("%INVOICEID%", invoiceId)
                .replaceAll("%FILENAME%",invoiceId+".xslt");

        File newFile = new File("xml\\temp.xml");
        Files.writeString(newFile.toPath(), entireFile, StandardOpenOption.CREATE);

        return newFile;
    }
}
