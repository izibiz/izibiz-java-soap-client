package com.edonusum.client.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DateUtils {
    private DateUtils() {}

    /**
     * Parametre olarak verilen gün sayısı kadar önceki tarihi XMLGregorianCalendar tipinde döndürür
     * @param days Şu anki tarihden eksiltilmek istenen gün sayısı
     * @return {@link XMLGregorianCalendar} tipindeki tarih objesi
     * @throws DatatypeConfigurationException
     */
    public static XMLGregorianCalendar minusDays(int days) throws DatatypeConfigurationException {
        LocalDate date = LocalDate.now();
        date = date.minusDays(days);

        Date d = Date.from(date.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    /**
     * Şu anki tarihi XMLGregorianCalendar tipindeki bir objeye dönüştürmektedir.
     * @return {@link XMLGregorianCalendar} tipindeki tarih objesi
     * @throws Exception
     */
    public static XMLGregorianCalendar now() throws  Exception {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    /**
     * Şu anki tarihi saat olmaksızın yalnızca tarih kısmını XMLGregorianCalendar tipinde döndürmektedir.
     * @return {@link XMLGregorianCalendar} tipindeki tarih objesi
     * @throws Exception
     */
    public static XMLGregorianCalendar nowWithoutTime() throws Exception {
        GregorianCalendar cal = new GregorianCalendar();

        LocalDate date = LocalDate.now();

        cal.setTime(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), DatatypeConstants.FIELD_UNDEFINED);
    }

    /**
     * Verilen string'ten XMLGregorianCalendar tipinde bir tarih objesi üretmektedir.
     * @param date Parse edilmek istenen String tipindeki tarih
     * @return {@link XMLGregorianCalendar} tipindeki tarih objesi
     * @throws DatatypeConfigurationException
     */
    public static XMLGregorianCalendar parse(String date) throws DatatypeConfigurationException {
        LocalDate localDate = LocalDate.parse(date);

        Date d = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }
}
