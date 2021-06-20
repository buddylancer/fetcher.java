// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;

import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;

import Bula.Objects.TRequest;
import Bula.Objects.DateTimes;
import Bula.Objects.Helper;
import Bula.Objects.Strings;

/**
 * Various helper methods.
 */
public class Util extends Meta {
    /**
     * Output text safely.
     * @param $input Text to output.
     * @return String Converted text.
     */
    public static String safe(String $input) {
        String $output = Strings.stripSlashes($input);
        $output = $output.replace("<", "&lt;");
        $output = $output.replace(">", "&gt;");
        $output = $output.replace("&", "&amp;");
        $output = $output.replace("\"", "&quot;");
        return $output;
    }

    /**
     * Output text safely with line breaks.
     * @param $input Text to output.
     * @return String Converted text.
     */
    public static String show(String $input) {
        if ($input == null)
            return null;
        String $output = safe($input);
        $output = $output.replace(EOL, "<br/>");
        return $output;
    }

    /**
     * Format date/time to GMT presentation.
     * @param $input Input date/time.
     * @return String Resulting date/time.
     */
    public static String showTime(String $input) {
        return DateTimes.format(DateTimes.GMT_DTS, DateTimes.getTime($input));
    }

    /**
     * Format string.
     * @param $format Format (template).
     * @param[] $arr Parameters.
     * @return String Resulting string.
     */
    public static String formatString(String $format, Object[] $arr) {
        if (BLANK($format))
            return null;
        String $output = $format;
        int $arrSize = SIZE($arr);
        for (int $n = 0; $n < $arrSize; $n++) {
            String $match = CAT("{", $n, "}");
            int $ind = $format.indexOf($match);
            if ($ind == -1)
                continue;
            $output = $output.replace($match, (String)$arr[$n]);
        }
        return $output;
    }

    /**
     * Logic for getting/saving page from/into cache.
     * @param $engine Engine instance.
     * @param $cacheFolder Cache folder root.
     * @param $pageName Page to process.
     * @param $className Appropriate class name.
     * @return String Resulting content.
     */
    public static String showFromCache(Engine $engine, String $cacheFolder, String $pageName, String $className) {
        return showFromCache($engine, $cacheFolder, $pageName, $className, null);
    }

    /**
     * Main logic for getting/saving page from/into cache.
     * @param $engine Engine instance.
     * @param $cacheFolder Cache folder root.
     * @param $pageName Page to process.
     * @param $className Appropriate class name.
     * @param $query Query to process.
     * @return String Resulting content.
     */
    public static String showFromCache(Engine $engine, String $cacheFolder, String $pageName, String $className, String $query /*= null*/) {
        if (EQ($pageName, "bottom"))
            $query = $pageName;
        else {
            if ($query == null)
                $query = $engine.$context.$Request.getVar(TRequest.INPUT_SERVER, "QUERY_STRING");
            if (BLANK($query))
                $query = "p=home";
        }

        String $content = null;

        if (EQ($pageName, "view_item")) {
            int $titlePos = $query.indexOf("&title=");
            if ($titlePos != -1)
                $query = $query.substring(0, $titlePos);
        }

        String $hash = $query;
        //$hash = str_replace("?", "_Q_", $hash);
        $hash = Strings.replace("=", "_EQ_", $hash);
        $hash = Strings.replace("&", "_AND_", $hash);
        String $fileName = Strings.concat($cacheFolder, "/", $hash, ".cache");
        if (Helper.fileExists($fileName)) {
            $content = Helper.readAllText($fileName);
            //$content = CAT("*** Got from cache ", str_replace("/", " /", $fileName), "***<br/>", $content);
        }
        else {
            String $prefix = EQ($pageName, "bottom") ? null : "Pages/";
            $content = $engine.includeTemplate(CAT($prefix, $className));

            Helper.testFileFolder($fileName);
            Helper.writeText($fileName, $content);
            //$content = CAT("*** Cached to ", str_replace("/", " /", $fileName), "***<br/>", $content);
        }
        return $content;
    }

    /**
     * Max length to extract from string.
     */
    public static final int MAX_EXTRACT = 100;

    /**
     * Extract info from a string.
     * @param $source Input string.
     * @param $after Substring to extract info "After".
     * @return String Resulting string.
     */
    public static String extractInfo(String $source, String $after) {
        return extractInfo($source, $after, null);
    }

    /**
     * Extract info from a string.
     * @param $source Input string.
     * @param $after Substring to extract info "After".
     * @param $to Substring to extract info "To".
     * @return String Resulting string.
     */
    public static String extractInfo(String $source, String $after, String $to/* = null*/) {
        String $result = null;
        if (!NUL($source)) {
            int $index1 = 0;
            if (!NUL($after)) {
                $index1 = $source.indexOf($after);
                if ($index1 == -1)
                    return null;
                $index1 += LEN($after);
            }
            int $index2 = $source.length();
            if (!NUL($to)) {
                $index2 = $source.indexOf($to, $index1);
                if ($index2 == -1)
                    $index2 = $source.length();
            }
            int $length = $index2 - $index1;
            if ($length > MAX_EXTRACT)
                $length = MAX_EXTRACT;
            $result = $source.substring($index1, $length);
        }
        return $result;
    }

    /**
     * Remove some content from a string.
     * @param $source Input string.
     * @param $from Substring to remove "From".
     * @return String Resulting string.
     */
    public static String removeInfo(String $source, String $from) {
        return removeInfo($source, $from, null);
    }

    /**
     * Remove some content from a string.
     * @param $source Input string.
     * @param $from Substring to remove "From".
     * @param $to Substring to remove "To".
     * @return String Resulting string.
     */
    public static String removeInfo(String $source, String $from, String $to/* = null*/) {
        String $result = null;
        int $index1 = $from == null ? 0 : $source.indexOf($from);
        if ($index1 != -1) {
            if ($to == null)
                $result = $source.substring($index1);
            else {
                int $index2 = $source.indexOf($to, $index1);
                if ($index2 == -1)
                    $result = $source;
                else {
                    $index2 += $to.length();
                    $result = Strings.concat(
                        $source.substring(0, $index1),
                        $source.substring($index2));
                }
            }
        }
        return $result.trim();
    }

    private static String[] $ruChars =
    {
        "а","б","в","г","д","е","ё","ж","з","и","й","к","л","м","н","о","п",
        "р","с","т","у","ф","х","ц","ч","ш","щ","ъ","ы","ь","э","ю","я",
        "А","Б","В","Г","Д","Е","Ё","Ж","З","И","Й","К","Л","М","Н","О","П",
        "Р","С","Т","У","Ф","Х","Ц","Ч","Ш","Щ","Ъ","Ы","Ь","Э","Ю","Я",
        "á", "ą", "ä", "ę", "ó", "ś",
        "Á", "Ą", "Ä", "Ę", "Ó", "Ś"
    };

    private static String[] $enChars =
    {
        "a","b","v","g","d","e","io","zh","z","i","y","k","l","m","n","o","p",
        "r","s","t","u","f","h","ts","ch","sh","shch","\"","i","\"","e","yu","ya",
        "A","B","V","G","D","E","IO","ZH","Z","I","Y","K","L","M","N","O","P",
        "R","S","T","U","F","H","TS","CH","SH","SHCH","\"","I","\"","E","YU","YA",
        "a", "a", "ae", "e", "o", "s",
        "A", "a", "AE", "E", "O", "S"
    };

    /**
     * Transliterate Russian text.
     * @param $ruText Original Russian text.
     * @return String Transliterated text.
     */
    public static String transliterateRusToLat(String $ruText) {
        for (int $n = 0; $n < $ruChars.length; $n++)
            $ruText = Strings.replaceAll($ruChars[$n], $enChars[$n], $ruText);
        return $ruText;
    }
}
