
package Bula.Objects;
import Bula.Meta;

import java.util.*;
import java.text.*;

/**
 * Helper class to manipulate with Date and Times.
 */
public class DateTimes extends Meta {
    /** Date/time format for processing GMT date/times */
    public static final String GMT_DTS = "dd-MMM-yyyy HH:mm 'GMT'"; //TODO -- append GMT
    /** Date/time format for RSS operations */
    public static final String XML_DTS = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    /** Date/time format for DB operations */
    public static final String SQL_DTS = "yyyy-MM-dd HH:mm:ss";
    /** Format of log-file name. */
    public static final String LOG_DTS = "yyyy-MM-dd_HH-mm-ss";
    /** Format of date/time in RSS-feeds. */
    public static final String RSS_DTS = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Get current time as Unix timestamp.
     * @return int Resulting time (Unix timestamp).
     */
    public static long getTime() {
        return (long)(new Date()).getTime();
    }

    /**
     * Get time as Unix timestamp.
     * @param $timeString Input string.
     * @return int Resulting time (Unix timestamp).
     */
    public static long getTime(String $timeString/* = null*/) {
        try { return (new SimpleDateFormat(SQL_DTS)).parse($timeString).getTime(); } catch (Exception ex) { return 0; }
    }

    /**
     * Get Unix timestamp from date/time extracted from RSS-feed.
     * @param $timeString Input string.
     * @return int Resulting timestamp.
     */
    public static long fromRss(String $timeString) {
        try { return (new SimpleDateFormat(RSS_DTS)).parse($timeString).getTime(); } catch (Exception ex) { return 0; }
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
