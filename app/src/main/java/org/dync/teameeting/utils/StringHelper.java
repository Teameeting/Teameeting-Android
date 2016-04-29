package org.dync.teameeting.utils;

import android.content.res.Resources;

import org.dync.teameeting.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by zhulang on 2016/1/6 0006.
 */
public class StringHelper {
    private static GregorianCalendar calendar = new GregorianCalendar();

    public static String formatDuration(long paramLong, Resources resources) {
        String mTimeFormat = " %d %s ";
        long l = System.currentTimeMillis() / 1000L - paramLong / 1000L;
        System.out.println("l" + l);
        if (l < 60L) {
            return resources.getString(R.string.chat_time_string_now);
        }
        if (l < 120L) {
            String str6 = mTimeFormat;
            Object[] arrayOfObject6 = new Object[2];
            arrayOfObject6[0] = Integer.valueOf(1);
            arrayOfObject6[1] = resources.getString(R.string.chat_time_string_minutes_age);
            return String.format(str6, arrayOfObject6);
        }
        if (l < 3600L) {
            String str5 = mTimeFormat;
            Object[] arrayOfObject5 = new Object[2];
            arrayOfObject5[0] = Long.valueOf(l / 60L);
            arrayOfObject5[1] = resources.getString(R.string.chat_time_string_minutes_age);
            return String.format(str5, arrayOfObject5);
        }
        if (l < 7200L) {
            String str4 = mTimeFormat;
            Object[] arrayOfObject4 = new Object[2];
            arrayOfObject4[0] = Integer.valueOf(1);
            arrayOfObject4[1] = resources.getString(R.string.chat_time_string_hour_age);
            return String.format(str4, arrayOfObject4);
        }
        if (l < 86400L) {
            String str3 = mTimeFormat;
            Object[] arrayOfObject3 = new Object[2];
            arrayOfObject3[0] = Long.valueOf(l / 60L / 60L);
            arrayOfObject3[1] = resources.getString(R.string.chat_time_string_hour_age);
            return String.format(str3, arrayOfObject3);
        }
        if (l < 172800L) {
            String str2 = mTimeFormat;
            Object[] arrayOfObject2 = new Object[2];
            arrayOfObject2[0] = Integer.valueOf(1);
            arrayOfObject2[1] = resources.getString(R.string.chat_time_string_hour_age);
            return String.format(str2, arrayOfObject2);
        }
        String str1 = mTimeFormat;
        Object[] arrayOfObject1 = new Object[2];
        arrayOfObject1[0] = Long.valueOf(l / 60L / 60L / 24L);
        arrayOfObject1[1] = resources.getString(R.string.chat_time_string_day_age);
        return String.format(str1, arrayOfObject1);
    }

    public static String format(long timeStr, Resources resources) {

        Date date = new Date(timeStr);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("   HH : mm");
        String YYMMDD = TimeHelper.getCustomStr(getYYMMDD(date), resources);
        return YYMMDD + simpleDateFormat.format(date);
    }

    private static String getYYMMDD(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
        String yueDay = time.format(date);
        return yueDay;
    }

    public static String unReadMessageStr(long noReadMessageSize, long timeStr, Resources resources) {
        return "<" + noReadMessageSize + "> " + resources.getString(R.string.chat_str_new_message) + format(timeStr, resources);
    }

    public static String uriToMeetingId(String uri) {
        int index = uri.lastIndexOf("//");
        return uri.substring(index + 2, index + 12);
    }


}
