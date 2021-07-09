// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import Bula.Objects.TEnumerator;
import Bula.Objects.TArrayList;
import Bula.Objects.Helper;
import Bula.Objects.Regex;

/**
 * Helper class for manipulation with text translations.
 */
public class Translator extends Meta {
    private static TArrayList $pairs = null;

    /**
     * Initialize translation table.
     * @param @fileName Filename to load translation table from.
     * @return int Number of actual pairs in translation table.
     */
    public static int initialize(String $fileName) {
        String[] $lines = Helper.readAllLines($fileName);
        if ($lines == null)
            return 0;
        $pairs = new TArrayList($lines);
        return $pairs.size();
    }

    /**
     * Translate content.
     * @param $input Input content to translate.
     * @return String Translated content.
     */
    public static String translate(String $input) {
        String $output = $input;
        for (int $n = 0; $n < $pairs.size(); $n++) {
            String $line = Strings.trim((String)$pairs.get($n), "\r\n");
            if (BLANK($line) || $line.indexOf("#") == 0)
                continue;
            if ($line.indexOf("|") == -1)
                continue;

            String[] $chunks = (String[])null;
            Boolean $needRegex = false;
            if ($line.indexOf("/") == 0) {
                $chunks = Strings.split("\\|", $line.substring(1));
                $needRegex = true;
            }
            else {
                $chunks = Strings.split("\\|", $line);
            }
            String $to = SIZE($chunks) > 1 ? $chunks[1] : "";
            $output = $needRegex ?
                Regex.replace($output, $chunks[0], $to) :
                Strings.replace($chunks[0], $to, $output);
        }
        return $output;
    }

    /**
     * Check whether translation table is initialized (loaded).
     * @return Boolean True if the table is initialized, False otherwise.
     */
    public static Boolean isInitialized() {
        return $pairs != null;
    }
}
