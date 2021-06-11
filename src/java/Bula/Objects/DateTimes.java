
package Bula.Objects;
import Bula.Meta;

import java.util.*;
import java.text.*;

/**
 * Helper class to manipulate with Date and Times.
 */
public class DateTimes extends Meta {
    /**
     * Format of date/time in RSS-feeds.
     */
    public static final String RSS_DTS = "ddd, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Get current time as Unix timestamp.
     * @return Integer Resulting time (Unix timestamp).
     */
    public static long getTime() {
        return (long)(new Date()).getTime();
    }

    /**
     * Get time as Unix timestamp.
     * @param $timeString Input string.
     * @return Integer Resulting time (Unix timestamp).
     */
    public static long getTime(String $timeString/* = null*/) {
        return (long)Date.parse($timeString);
    }

    /**
     * Get Unix timestamp from date/time extracted from RSS-feed.
     * @param $timeString Input string.
     * @return Integer Resulting timestamp.
     */
    public static long fromRss(String $timeString) {
        return getTime($timeString);
    }

    /**
     * Format to string presentation.
     * @param $formatString Format to apply.
     * @return String Resulting string.
     */
    public static String format(String $formatString) {
        return format($formatString, 0);
    }

    /**
     * Format time from Unix timestamp to string presentation.
     * @param $formatString Format to apply.
     * @param $timeValue Input time value (Unix timestamp).
     * @return String Resulting string.
     */
    public static String format(String $formatString, long $timeValue /*= 0*/) {
        return (new SimpleDateFormat($formatString)).format($timeValue == 0 ? new Date() : new Date($timeValue));
    }

    /**
     * Format current time to GMT string presentation.
     * @param $formatString Format to apply.
     * @return String Resulting string.
     */
    public static String gmtFormat(String $formatString) {
        return gmtFormat($formatString, 0);
    }

    /**
     * Format time from timestamp to GMT string presentation.
     * @param $formatString Format to apply.
     * @param $timeValue Input time value (Unix timestamp).
     * @return String Resulting string.
     */
    public static String gmtFormat(String $formatString, long $timeValue /*= 0*/) {
        SimpleDateFormat df = new SimpleDateFormat($formatString);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format($timeValue == 0 ? new Date() : new Date($timeValue));
    }
}
