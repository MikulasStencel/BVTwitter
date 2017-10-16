package com.epicqueststudios.bvtwitter.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateUtils {
    public static String formatDate(long milliseconds) {
       // DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
        //DateFormat sdf = new SimpleDateFormat("MMM' 'dd, yyyy");
        DateFormat sdf = new SimpleDateFormat("MMM' 'dd, yyyy' 'HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        return sdf.format(calendar.getTime());
    }
}
