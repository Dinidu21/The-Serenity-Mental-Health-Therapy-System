package com.dinidu.lk.pmt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return sdf.format(date);
    }
}