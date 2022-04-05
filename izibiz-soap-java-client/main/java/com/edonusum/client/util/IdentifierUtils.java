package com.edonusum.client.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;

public class IdentifierUtils {
    private IdentifierUtils() {}

    private static DecimalFormat formatter = new DecimalFormat("#000000000");
    private static Random random = new Random();
    private static final char[] alphabet = "abcdefghijklmnopqrstuvwxyz0123456789".toUpperCase(Locale.ENGLISH).toCharArray();
    private static final int ID_UPPER_BOUND = 999999999;


    public static String createInvoiceIdRandom(String prefix) {
        long id = random.nextInt(ID_UPPER_BOUND); // 9 hane
        String result = prefix + LocalDate.now().getYear() + formatter.format(id); // seri + yıl + 9 haneli id

        return result;
    }

    public static String createInvoiceIdRandomPrefix() {
        int firstIndex = random.nextInt(alphabet.length-1);
        int secondIndex = random.nextInt(alphabet.length-1);
        int thirdIndex = random.nextInt(alphabet.length-1);

        char[] prefix = {alphabet[firstIndex], alphabet[secondIndex], alphabet[thirdIndex]};

        long id = 000000001; // her yeni seri 1'den başlar

        String result = String.copyValueOf(prefix) + LocalDate.now().getYear() + formatter.format(id);

        return result;
    }
}
