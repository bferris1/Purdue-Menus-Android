package com.moufee.purduemenus;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void dateParsingTest(){
        DateTime tomorrow = DateTime.now().plusDays(1);
        LocalDate tomorrowDate = new LocalDate(tomorrow);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("kk:mm:ss");
        DateTimeFormatter timeFormatter = DateTimeFormat.shortTime().withLocale(Locale.US);
        DateTimeFormatter outputFormat = DateTimeFormat.fullDateTime().withLocale(Locale.US);
        DateTime dateTime = formatter.parseDateTime("17:30:00");
        LocalTime localTime = formatter.parseLocalTime("17:30:00");
        System.out.println(outputFormat.print(dateTime));
        DateTime tomorrowTime = dateTime.withDate(tomorrowDate);
        System.out.println(outputFormat.print(tomorrowTime));
        System.out.println(formatter.print(localTime));
        assertEquals(tomorrowTime, tomorrow.withTime(17,30,0,0));
    }
}