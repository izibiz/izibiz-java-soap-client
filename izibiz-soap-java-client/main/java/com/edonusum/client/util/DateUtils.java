package com.edonusum.client.util;

import javax.xml.datatype.DatatypeConfigurationException;
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

        XMLGregorianCalendar result = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        return result;
    }

    public static XMLGregorianCalendar now() throws  DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        XMLGregorianCalendar result = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        return result;
    }
}
