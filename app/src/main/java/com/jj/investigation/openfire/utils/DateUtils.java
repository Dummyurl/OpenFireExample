package com.jj.investigation.openfire.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转换
 * Created by ${R.js} on 2017/12/19.
 */
public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");

    public static String newDate(){
        return dateFormat.format(new Date());
    }

}
