package io.github.mynametsthad.helpfulutilsbotline.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Utils {

    /**
     * @param firstTimeStamp The first timestamp to compare. (unix millis)
     * @param secondTimeStamp The second timestamp to compare. (unix millis)
     * @param levels Return Levels; example: 1 returns just seconds, 2 returns seconds and minutes, 3 returns seconds, minutes and hours, etc...
     * @return Returns A list of numbers denoting the difference between the two timestamps.
     */
    public static List<Long> getFormattedTimeDiffrence(long firstTimeStamp, long secondTimeStamp, int levels){
        long timeDifference = Math.abs(firstTimeStamp - secondTimeStamp); //time in milliseconds
        List<Long> returnList = new ArrayList<>();
        if (levels >= 7){
            long yearsAgo = (long) Math.floor(timeDifference / 31540000000D);
            long monthsAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D)) / 2628000000D);
            long weeksAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D)) / 604800000D);
            long daysAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D)) / 86400000D);
            long hoursAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D)) / 3600000D);
            long minutesAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D) - (hoursAgo * 3600000D)) / 60000D);
            long secondsAgo = (long) Math.floor((timeDifference - (yearsAgo * 31540000000D) - (monthsAgo * 2628000000D) - (hoursAgo * 3600000D) - (minutesAgo * 60000D)) / 1000D);
            Collections.addAll(returnList, yearsAgo, monthsAgo, weeksAgo, daysAgo, hoursAgo, minutesAgo, secondsAgo);
        }else if (levels == 6){
            long monthsAgo = (long) Math.floor(timeDifference / 2628000000D);
            long weeksAgo = (long) Math.floor((timeDifference - (monthsAgo * 2628000000D)) / 604800000D);
            long daysAgo = (long) Math.floor((timeDifference - (monthsAgo * 2628000000D)) / 86400000D);
            long hoursAgo = (long) Math.floor((timeDifference - (monthsAgo * 2628000000D)) / 3600000D);
            long minutesAgo = (long) Math.floor((timeDifference - (monthsAgo * 2628000000D) - (hoursAgo * 3600000D)) / 60000D);
            long secondsAgo = (long) Math.floor((timeDifference - (monthsAgo * 2628000000D) - (hoursAgo * 3600000D) - (minutesAgo * 60000D)) / 1000D);
            Collections.addAll(returnList, monthsAgo, weeksAgo, daysAgo, hoursAgo, minutesAgo, secondsAgo);
        }else if (levels == 5){
            long weeksAgo = (long) Math.floor((timeDifference) / 604800000D);
            long daysAgo = (long) Math.floor((timeDifference) / 86400000D);
            long hoursAgo = (long) Math.floor((timeDifference) / 3600000D);
            long minutesAgo = (long) Math.floor((timeDifference - (hoursAgo * 3600000D)) / 60000D);
            long secondsAgo = (long) Math.floor((timeDifference - (hoursAgo * 3600000D) - (minutesAgo * 60000D)) / 1000D);
            Collections.addAll(returnList, weeksAgo, daysAgo, hoursAgo, minutesAgo, secondsAgo);
        }else if (levels == 4){
            long daysAgo = (long) Math.floor((timeDifference) / 86400000D);
            long hoursAgo = (long) Math.floor((timeDifference) / 3600000D);
            long minutesAgo = (long) Math.floor((timeDifference - (hoursAgo * 3600000D)) / 60000D);
            long secondsAgo = (long) Math.floor((timeDifference - (hoursAgo * 3600000D) - (minutesAgo * 60000D)) / 1000D);
            Collections.addAll(returnList, daysAgo, hoursAgo, minutesAgo, secondsAgo);
        }else if (levels == 3){
            long hoursAgo = (long) Math.floor((timeDifference) / 3600000D);
            long minutesAgo = (long) Math.floor((timeDifference - (hoursAgo * 3600000D)) / 60000D);
            long secondsAgo = (long) Math.floor((timeDifference - (hoursAgo * 3600000D) - (minutesAgo * 60000D)) / 1000D);
            Collections.addAll(returnList, hoursAgo, minutesAgo, secondsAgo);
        }else if (levels == 2){
            long minutesAgo = (long) Math.floor((timeDifference) / 60000D);
            long secondsAgo = (long) Math.floor((timeDifference - (minutesAgo * 60000D)) / 1000D);
            Collections.addAll(returnList, minutesAgo, secondsAgo);
        }else if (levels == 1){
            long secondsAgo = (long) Math.floor((timeDifference) / 1000D);
            Collections.addAll(returnList, secondsAgo);
        }

        return returnList;
    }

    /**
     * @param timeStamp The timestamp to compare with the current timestamp. (unix millis)
     * @param levels Return Levels; example: 1 returns just seconds, 2 returns seconds and minutes, 3 returns seconds, minutes and hours, etc...
     * @return Returns A list of numbers denoting the difference between the two timestamps.
     */
    public static List<Long> getFormattedTimeDiffrence(long timeStamp, int levels){
        return getFormattedTimeDiffrence(timeStamp, new Date().getTime(), levels);
    }

    public static long timeToMillis(int seconds){
        return seconds * 1000L;
    }
    public static long timeToMillis(int minutes, int seconds){
        return ((minutes * 60L) + seconds) * 1000L;
    }
    public static long timeToMillis(int hours, int minutes, int seconds){
        return ((((hours * 60L) + minutes) * 60L) + seconds) * 1000L;
    }
    public static long timeToMillis(int days, int hours, int minutes, int seconds){
        return ((((((days * 24L) + hours) * 60L) + minutes) * 60L) + seconds) * 1000L;
    }
    public static long timeToMillis(int weeks, int days, int hours, int minutes, int seconds){
        return ((((((((weeks * 7L) + days) * 24L) + hours) * 60L) + minutes) * 60L) + seconds) * 1000L;
    }
    public static long timeToMillis(int months, int weeks, int days, int hours, int minutes, int seconds){
        return ((((((((((months * 4L) + weeks) * 7L) + days) * 24L) + hours) * 60L) + minutes) * 60L) + seconds) * 1000L;
    }
    public static long timeToMillis(int years, int months, int weeks, int days, int hours, int minutes, int seconds){
        return ((((((((((((years * 12L) + months) * 4L) + weeks) * 7L) + days) * 24L) + hours) * 60L) + minutes) * 60L) + seconds) * 1000L;
    }
}
