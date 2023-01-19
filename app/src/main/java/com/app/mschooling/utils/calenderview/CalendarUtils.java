package com.app.mschooling.utils.calenderview;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by Nilanchala Panigrahy on 8/24/16.
 */

public class CalendarUtils {
    public Context context;

    public CalendarUtils(Context context) {
        this.context = context;
    }

    //public static AppUser appUser;

    public static boolean isSameMonth(Calendar c1, Calendar c2) {
        if (c1 == null || c2 == null)
            return false;
        return (c1.get(Calendar.ERA) == c2.get(Calendar.ERA)
                && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
    }

    /**
     * <p>Checks if a calendar is today.</p>
     *
     * @param calendar the calendar, not altered, not null.
     * @return true if the calendar is today.
     * @throws IllegalArgumentException if the calendar is <code>null</code>
     */
    public static boolean isToday(Calendar calendar) {
        return isSameDay(calendar, Calendar.getInstance());
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null)
            throw new IllegalArgumentException("The dates must not be null");
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static int getTotalWeeks(Calendar calendar) {
        if (null == calendar) return 0;
        int maxWeeks = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        return maxWeeks;

    }

    public static int getTotalWeeks(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getTotalWeeks(cal);
    }

    public boolean isPastDay(Date date) {
        int a = 0;
       /* if (!Preferences.getInstance(context).getBlock_before_days().equals("")) {
             a = Integer.parseInt(Preferences.getInstance(context).getBlock_before_days());
        }
        else{
            a=0;
        }*/
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, a);
//        Timber.i("BBBBBBBBBBBBBBBBBB" + date);
//        Timber.i("BBBBBBBBBBBBBBBBBB" + date.before(calendar.getTime()));

        return (date.before(calendar.getTime())) ? true : false;
    }

}
