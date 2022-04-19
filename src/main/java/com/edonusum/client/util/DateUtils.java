package com.edonusum.client.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    private DateUtils() {}

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

    public static XMLGregorianCalendar now() throws  Exception {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    public static XMLGregorianCalendar nowWithoutTime() throws Exception {
        GregorianCalendar cal = new GregorianCalendar();

        LocalDate date = LocalDate.now();

        cal.setTime(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), DatatypeConstants.FIELD_UNDEFINED);
    }

    public static XMLGregorianCalendar parse(String date) throws DatatypeConfigurationException {
        LocalDate localDate = LocalDate.parse(date);

        Date d = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }
}
