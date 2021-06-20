// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import java.util.Enumeration;

import Bula.Internal;
import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;

/**
 * Helper class for manipulations with strings.
 */
public class Strings extends Meta {
    /**
     * Provide empty array.
     * @return String[] Empty array of strings.
     */
    public static String[] emptyArray() {
        return new String[0];
    }

    /**
     * Convert first char of a string to upper case.
     * @param $input Input string.
     * @return String Resulting string.
     */
    public static String firstCharToUpper(String $input) {
        return concat($input.substring(0, 1).toUpperCase(), $input.substring(1));
    }

    /**
     * Join an array of strings using divider,
     * @param $divider Divider (yes, may be empty).
     * @param[] $strings Array of strings.
     * @return String Resulting string.
     */
    public static String join(String $divider, String[] $strings) {
        String $output = new String();
        int $count = 0;
        for (String $string1 : $strings) {
            if ($count > 0)
                $output += $divider;
            $output += $string1;
            $count++;
        }
        return $output;
    }

    /**
     * Remove all HTML tags from string.
     * @param $input Input string.
     * @return String Resulting string.
     */
    public static String removeTags(String $input) {
        return removeTags($input, null);
    }

    /**
     * Remove HTML tags from string except allowed ones.
     * @param $input Input string.
     * @param $except List of allowed tags (do not remove).
     * @return String Resulting string.
     */
    public static String removeTags(String $input, String $except/* = null */) {
        return Internal.removeTags($input, $except);
    }

    /**
     * Add slashes to the string.
     * @param $input Input string.
     * @return String Resulting string.
     */
    public static String addSlashes(String $input) {
        return $input.replace("'", "\\'"); //TODO!!!
    }

    /**
     * remove slashes from the string.
     * @param $input Input string.
     * @return String Resulting string.
     */
    public static String stripSlashes(String $input) {
        return $input.replace("\\'", "'"); //TODO!!!
    }

    /**
     * Count substrings in the string.
     * @param $input Input string.
     * @param $chunk String to count.
     * @return int Number of substrings.
     */
    public static int countSubstrings(String $input, String $chunk) {
        if ($input.length() == 0)
            return 0;
        String $replaced = $input.replace($chunk, "");
        return $input.length() - $replaced.length();
    }

    /**
     * Concatenate a number of strings to a single one.
     * @param[] $args Array of strings.
     * @return String Resulting string.
     */
    public static String concat(Object... $args) {
        String $output = new String();
        if (SIZE($args) != 0) {
            for (Object $arg: $args) {
                if ($arg == null)
                    continue;
                $output += (String)$arg;
            }
        }
        return $output;
    }

    /**
     * Split a string using divider/separator.
     * @param $divider Divider/separator.
     * @param $input Input string.
     * @return String[] Array of resulting strings.
     */
    public static String[] split(String $divider, String $input) {
        String[] $chunks =
            Regex.split($input, Regex.escape($divider));
        TArrayList $result = new TArrayList();
        for (int $n = 0; $n < SIZE($chunks); $n++)
            $result.add($chunks[$n]);
        return (String[])$result.toArray(new String[] {});
    }

    /**
     * Replace all substring(s) from a string.
     * @param $from Substring to replace.
     * @param $to Replacement string.
     * @param $input Input string.
     * @return String Resulting string.
     */
    public static String replace(String $from, String $to, String $input) {
        return replace($from, $to, $input, 0);
    }

    /**
     * Replace a number of substring(s) from a string.
     * @param $from Substring to replace.
     * @param $to Replacement string.
     * @param $input Input string.
     * @param $limit Max number of replacements [optional].
     * @return String Resulting string.
     */
    public static String replace(String $from, String $to, String $input, int $limit/* = 0*/) {
        return $limit == 1 ? $input.replaceFirst($from, $to) : $input.replace($from, $to);
    }

    /**
     * Replace all substrings using regular expressions.
     * @param $regex Regular expression to match substring(s).
     * @param $to Replacement string.
     * @param $input Input string.
     * @return String Resulting string.
     */
    public static String replaceAll(String $regex, String $to, String $input) {
        return replace($regex, $to, $input);
    }

    /**
     * Replace first substring using regular expressions.
     * @param $regex Regular expression to match substring.
     * @param $to Replacement string.
     * @param $input Input string.
     * @return String Resulting string.
     */
    public static String replaceFirst(String $regex , String $to, String $input) {
        return replace($regex, $to, $input, 1);
    }

    /**
     * Replace "keys by values" in a string.
     * @param $template Input template.
     * @param $hash Set of key/value pairs.
     * @return String Resulting string.
     */
    public static String replaceInTemplate(String $template, THashtable $hash) {
        TEnumerator $keys = new TEnumerator($hash.keys());
        while ($keys.moveNext()) {
            String $key = STR($keys.getCurrent());
            $template = Strings.replace($key, STR($hash.get($key)), $template);
        }
        return $template;
    }

    public static String trim(String input) {
        return trim(input, null);
    }

    /**
     * Trim this string.
     * @param $chars Which chars to trim [optional].
     * @return String Resulting string.
     */
    public static String trim(String $input, String $chars/* = null*/) {
        if ($chars == null)
            $chars = " \\n\\r\\t\\v\\0";
        $input = Regex.replace($input, CAT("^", "[", $chars, "]*"), "");
        $input = Regex.replace($input, CAT("[", $chars, "]*$"), "");
        return $input;
    }

}
