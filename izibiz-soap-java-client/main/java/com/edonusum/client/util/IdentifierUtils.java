package com.edonusum.client.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Random;

public class IdentifierUtils {
    private IdentifierUtils() {}

    private static DecimalFormat formatter = new DecimalFormat("#000000000");
    private static Random random = new Random();


    public static String createInvoiceIdRandom(String prefix) {
        long id = random.nextInt(999999999); // 9 hane
        String result = prefix + LocalDate.now().getYear() + formatter.format(id); // seri + yıl + 9 haneli id

        return result;
    }
}
