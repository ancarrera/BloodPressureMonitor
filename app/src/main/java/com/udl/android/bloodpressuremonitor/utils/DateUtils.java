package com.udl.android.bloodpressuremonitor.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Adrian on 06/03/2015.
 */
public class DateUtils {

    public static String DATEFORMAT = "dd-MM-yyyy";

    public static Date stringToDate(String date, String format) throws ParseException {
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(date);
    }
    public static String dateToString(Date date, String format){
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String getCurrentDate(){
        return dateToString(new Date(),DATEFORMAT);
    }
}
