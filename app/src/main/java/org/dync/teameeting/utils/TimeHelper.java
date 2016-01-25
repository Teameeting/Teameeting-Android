package org.dync.teameeting.utils;

import android.content.res.Resources;

import org.dync.teameeting.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Xiao_Bailong on 2016/1/22.
 */
public class TimeHelper {
    private static GregorianCalendar calendar = new GregorianCalendar();

    public TimeHelper() {
    }


    public static long getMillis(String dateString) {
        String[] date = dateString.split("-");
        return getMillis(date[0], date[1], date[2]);
    }


    public static long getMillis(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calendar.getTimeInMillis();

    }


    public static long getMillis(String yearString, String monthString,
                                 String dayString) {
        int year = Integer.parseInt(yearString);
        int month = Integer.parseInt(monthString) - 1;
        int day = Integer.parseInt(dayString);
        return getMillis(year, month, day);

    }


    public static long getNow() {
        GregorianCalendar now = new GregorianCalendar();
        return now.getTimeInMillis();

    }


    public static String getDate(long millis) {
        Date date = new Date(millis);
        return getYYMMDD(date);
    }

    private static String getYYMMDD(Date date) {

        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
        String yueDay = time.format(date);
        return yueDay;
    }


    public static int getWeek(long millis) {
        calendar.setTimeInMillis(millis);
        int week = 8;
        int cweek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (cweek) {
            case 1:
                week = 7;
                break;
            case 2:
                week = 1;
                break;
            case 3:
                week = 2;
                break;
            case 4:
                week = 3;
                break;
            case 5:
                week = 4;
                break;
            case 6:
                week = 5;
                break;
            case 7:
                week = 6;
                break;
        }

        return Integer.valueOf(week);

    }

    public static String getWeekStr(long millis) {
        calendar.setTimeInMillis(millis);
        String week = "";
        int cweek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (cweek) {
            case 1:
                week = "日";
                break;
            case 2:
                week = "一";
                break;
            case 3:
                week = "二";
                break;
            case 4:
                week = "三";
                break;
            case 5:
                week = "四";
                break;
            case 6:
                week = "五";
                break;
            case 7:
                week = "六";
                break;
        }
        return week;
    }

    public static String getTodayData() {
        return getDate(getNow());
    }

    public static String getYesData() {
        return getDate(getNow() - 86400000L);
    }

    public static String getBeforeYesData() {
        return getDate(getNow() - 86400000L - 86400000L);
    }

    /**
     * @param date 格式为 20xx-xx-xx
     * @return
     */
    public static String getCustomStr(String date, Resources resources) {

        long millis = getMillis(date);
        if (millis == getMillis(getBeforeYesData())) {
            return resources.getString(R.string.day3bef);
        } else if (millis == getMillis(getYesData())) {
            return resources.getString(R.string.yesterday);
        } else if (millis == getMillis(getTodayData())) {
            return resources.getString(R.string.newadays);
        } else if (millis > getMillis(getBeforeWorkData())) {
            if (getWeek(getMillis(getTodayData())) > 3 && getWeek(getMillis(getTodayData())) <= 7) {
                return resources.getString(R.string.work) + getWeekStr(millis);
            }
        }

        return date;
    }

    public static String getBeforeWorkData() {
        return getDate(getNow() - (86400000L * 7));
    }
}
