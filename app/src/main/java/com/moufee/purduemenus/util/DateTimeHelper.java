package com.moufee.purduemenus.util;

import android.content.Context;
import android.text.format.DateFormat;

import com.moufee.purduemenus.R;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Static helper methods related to date and time
 */

public class DateTimeHelper {
    public static int getCurrentMealIndex(){
        DateTime now = new DateTime();
        if (now.getHourOfDay() <= 9)
            return 0;
        else if (now.getHourOfDay() <= 13)
            return 1;
        else if (now.getHourOfDay() <= 16)
            return 2;
        else if (now.getHourOfDay() <= 21 )
            return 3;
        else
            return 0;
    }

    public static String getFriendlyDateFormat(LocalDate dateTime, Locale locale, Context context) {
        String pattern;
        final int HINT_START_HOUR = 22; // the hour at which day hints will be shown (e.g. after 10pm)
        final int HINT_END_HOUR = 4; //the hour in the morning after which hints will not be displayed
        pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEE MMMMM dd");
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern).withLocale(Locale.getDefault());
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern(" (E)").withLocale(Locale.getDefault());
        DateTime now = new DateTime();
        boolean showDayHints = now.getHourOfDay() >= HINT_START_HOUR || now.getHourOfDay() <= HINT_END_HOUR;
        String dayString = showDayHints ? dayFormat.print(now) : "";
        LocalDate today = LocalDate.now();
        if (today.equals(dateTime))
            return context.getString(R.string.today) + dayString;
        LocalDate tomorrow = today.plusDays(1);
        if (tomorrow.equals(dateTime)) {
            dayString = showDayHints ? dayFormat.print(now.plusDays(1)) : "";
            return context.getString(R.string.tomorrow) + dayString;
        }
        LocalDate yesterday = today.minusDays(1);
        if (yesterday.equals(dateTime)) {
            dayString = showDayHints ? dayFormat.print(now.plusDays(-1)) : "";
            return context.getString(R.string.yesterday) + dayString;
        }
        return format.print(dateTime);
    }
}
