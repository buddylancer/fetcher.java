// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import java.util.regex.*;

/**
 * Helper class for manipulations using Regex.
 */
public class Regex extends Meta {
    
    public static Boolean isMatch(String $input, String $pattern) {
        return isMatch($input, $pattern, 0);
    }

    /**
     * Check whether input string matches a pattern.
     * @param $input Input string to check.
     * @param $pattern Pattern to check.
     * @param $options Matching options (0 - no options).
     * @return Boolean True - matches, False - not matches.
     */
    public static Boolean isMatch(String $input, String $pattern, int $options /* = 0 */) {
        //return Pattern.matches($pattern, $input); //TODO
        int $patternOptions = 
                ((INT($options) & RegexOptions.IgnoreCase) != 0) ? Pattern.CASE_INSENSITIVE : 0;
        return Pattern.compile($pattern, $patternOptions).matcher($input).find();
    }

    public static String replace(String $input, String $pattern, String $replacement) {
        return replace($input, $pattern, $replacement, 0);
    }

    /**
     * Replace pattern.
     * @param $input Input string to process.
     * @param $pattern Pattern to replace.
     * @param $replacement Replacing string.
     * @param $options Matching options (0 - no options).
     * @return String Resulting string.
     */
    public static String replace(String $input, String $pattern, String $replacement, int $options /* = 0*/) {
        int $patternOptions = 
                ((INT($options) & RegexOptions.IgnoreCase) != 0) ? Pattern.CASE_INSENSITIVE : 0;
        return Pattern.compile($pattern, $patternOptions).matcher($input).replaceAll($replacement);
    }

    public static String[] split(String $input, String $pattern) {
        return split($input, $pattern, 0);
    }

    /**
     * Split a string using pattern.
     * @param $input Input string to process.
     * @param $pattern Pattern to split by.
     * @param $options Matching options (0 - no options).
     * @return String[] Resulting array of strings.
     */
    public static String[] split(String $input, String $pattern, int $options /* = null */) {
        int $patternOptions = 
                ((INT($options) & RegexOptions.IgnoreCase) != 0) ? Pattern.CASE_INSENSITIVE : 0;
        String[] $chunks = Pattern.compile($pattern, $patternOptions).split($input/*, $splitOptions*/);
        TArrayList $outArray = new TArrayList();
        for (String $chunk : $chunks)
            $outArray.add($chunk);
        return (String[])$outArray.toArray(new String[] {});
    }

    public static String[] getMatches(String $input, String $pattern) {
        return getMatches($input, $pattern, 0);
    }

    /**
     * Get matching strings.
     * @param $input Input string to process.
     * @param $pattern Pattern to search for.
     * @param $options Matching options (0 - no options).
     * @return String[] Resulting array of strings (or null).
     */
    public static String[] getMatches(String $input, String $pattern, int $options /* = null */) {
        int $patternOptions = Pattern.UNICODE_CASE |
                (((INT($options) & RegexOptions.IgnoreCase) != 0) ? Pattern.CASE_INSENSITIVE : 0);
        Matcher $matcher = Pattern.compile($pattern, $patternOptions).matcher($input);
        TArrayList $result = new TArrayList();
        while ($matcher.find())
            $result.add($matcher.group());
        return (String[])$result.toArray(new String[] {});
    }

    /**
     * Get quoted string/pattern.
     * @param $input Input string/pattern.
     * @return String Resulting quoted string/pattern.
     */
    public static String escape(String $pattern) {
        return Pattern.quote($pattern);
    }

    /**
     * Get unquoted string.
     * @param $input Quoted string.
     * @return String Resulting unquoted string.
     */
    public static String unescape(String $input) {
        return $input; //TODO
    }
}
