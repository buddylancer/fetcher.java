// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;

import Bula.Objects.Regex;
import Bula.Objects.RegexOptions;

import Bula.Objects.Arrays;
import Bula.Objects.Strings;
import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;

import Bula.Model.DataSet;

/**
 * Manipulating with items.
 */
public class BOItem extends Meta {
    // Input fields
    /** Source name */
    private String $source = null;
    /** RSS-item */
    private THashtable $item = null;

    /** Link to external item */
    public String $link = null;
    /** Original title */
    public String $fullTitle = null;
    /** Original description */
    public String $fullDescription = null;

    // Output fields
    /** Final (processed) title */
    public String $title = null;
    /** Final (processed) description */
    public String $description = null;
    /** Final (processed) date */
    public String $date = null;

    // Custom output fields
    /** Extracted creator (publisher) */
    public String $creator = null;
    /** Extracted category */
    public String $category = null;
    /** Extracted custom field 1 */
    public String $custom1 = null;
    /** Extracted custom field 2 */
    public String $custom2 = null;

    /**
     * Instantiate BOItem from given source and RSS-item.
     * @param $source Current processed source.
     * @param $item Current processed RSS-item from given source.
     */
    public BOItem (String $source, THashtable $item) {
            this.initialize($source, $item);
    }

    /**
     * Initialize this BOItem.
     * @param $source Current processed source.
     * @param $item Current processed RSS-item from given source.
     */
    private void initialize(String $source, THashtable $item) {
        this.$source = $source;
        this.$item = $item;

        this.$link = (String)$item.get("link");

        // Pre-process full description & title
        // Trick to eliminate non-UTF-8 characters
        this.$fullTitle = Regex.replace((String)$item.get("title"), "[\\xF0-\\xF7][\\x80-\\xBF]{3}", "");
        if ($item.containsKey("description") && !BLANK($item.get("description")))
            this.$fullDescription = Regex.replace((String)$item.get("description"), "[\\xF0-\\xF7][\\x80-\\xBF]{3}", "");

        this.preProcessLink();
    }

    /**
     * Pre-process link (just placeholder for now)
     */
    protected void preProcessLink()
    {}

    /**
     * Process description.
     */
    public void processDescription() {
        String $BR = "\n";

        String $title = Strings.removeTags(this.$fullTitle);
        // Normalize \r\n to \n
        $title = Regex.replace($title, "\r\n", $BR);
        $title = Regex.replace($title, "(^&)#", "$1[sharp]");
        $title = $title.replace("&amp;", "&");
        $title = Regex.replace($title, "&amp;laquo;", "&laquo;");
        $title = Regex.replace($title, "&amp;raquo;", "&raquo;");
        $title = $title.trim();
        $title = Regex.replace($title, "[\n]+", " ");

        // Moved to Rules
        //int $httpIndex = Strings.lastIndexOf("http", $title);
        //if ($httpIndex != -1)
        //    $title = Strings.substring($title, 0, $httpIndex);

        this.$title = $title;

        if (this.$fullDescription == null)
            return;
        // Normalize \r\n to \n
        String $description = Regex.replace(this.$fullDescription, "\r\n", $BR);

        //TODO -- Fixes for FetchRSS feeds (parsed from Twitter) here...
        $description = $description.replace("&#160;", " ");
        $description = $description.replace("&nbsp;", " ");

        // Start -- Fixes and workarounds for some sources here...
        // End

        Boolean $hasP = Regex.isMatch($description, "<p[^>]*>");
        Boolean $hasBr = $description.indexOf("<br") != -1;
        Boolean $hasLi = $description.indexOf("<li") != -1;
        Boolean $hasDiv = $description.indexOf("<div") != -1;
        String $includeTags = Strings.concat(
            "<br>",
            ($hasP ? "<p>" : null),
            ($hasLi ? "<ul><ol><li>" : null),
            ($hasDiv ? "<div>" : null)
        );

        $description = Strings.removeTags($description, $includeTags);

        if ($hasBr)
            $description = Regex.replace($description, "[ \t\r\n]*<br[ ]*[/]*>[ \t\r\n]*", $BR, RegexOptions.IgnoreCase);
        if ($hasLi) {
            $description = Regex.replace($description, "<ul[^>]*>", $BR, RegexOptions.IgnoreCase);
            $description = Regex.replace($description, "<ol[^>]*>", "* ", RegexOptions.IgnoreCase);
            $description = Regex.replace($description, "<li[^>]*>", "* ", RegexOptions.IgnoreCase);
            $description = Regex.replace($description, "</ul>", $BR, RegexOptions.IgnoreCase);
            $description = Regex.replace($description, "</ol>", $BR, RegexOptions.IgnoreCase);
            $description = Regex.replace($description, "</li>", $BR, RegexOptions.IgnoreCase);
        }
        if ($hasP) {
            $description = Regex.replace($description, "<p[^>]*>", $BR, RegexOptions.IgnoreCase);
            $description = Regex.replace($description, "</p>", $BR, RegexOptions.IgnoreCase);
        }
        if ($hasDiv) {
            $description = Regex.replace($description, "<div[^>]*>", $BR, RegexOptions.IgnoreCase);
            $description = Regex.replace($description, "</div>", $BR, RegexOptions.IgnoreCase);
        }

        // Process end-of-lines...
        //$description = Regex.replace($description, "[\n]+$", "");
        $description = Regex.replace($description, "[ \t]+\n", "\n");
        $description = Regex.replace($description, "[\n]+\n\n", "\n\n");
        $description = Regex.replace($description, "\n\n[ \t]*[\\+\\-\\*][^\\+\\-\\*][ \t]*", "\n* ");
        $description = Regex.replace($description, "[ \t]+", " ");

        $description = Regex.replace($description, "&amp;laquo;", "&laquo;");
        $description = Regex.replace($description, "&amp;raquo;", "&raquo;");

        // Normalize back to \r\n
        this.$description = Regex.replace($description.trim(), $BR, EOL);
    }

    /**
     * Process category (if any).
     */
    public void processCategory() {
        // Set or fix category from item
        String $category = null;
        if (!BLANK(this.$item.get("category")))
            $category = this.preProcessCategory(STR(this.$item.get("category")));
        else {
            if (!BLANK(this.$item.get("tags")))
                $category = this.preProcessCategory(STR(this.$item.get("tags")));
            else
                $category = this.extractCategory();
        }
        this.$category = $category;
    }

    /**
     * Pre-process category.
     * @param $categoryItem Input category.
     * @return String Pre-processed category.
     */
    private String preProcessCategory(String $categoryItem) {
        // Pre-process category from $item["category"]

        // This is just sample - implement your own logic
        if (EQ(this.$source, "something.com")) {
            // Fix categories from something.com
        }

        String $category = null;
        if (!$categoryItem.isEmpty()) {
            String[] $categoriesArr = Strings.split(",", $categoryItem.replace(",&,", " & "));
            TArrayList $categoriesNew = new TArrayList();
            for (int $c = 0; $c < SIZE($categoriesArr); $c++) {
                String $temp = $categoriesArr[$c];
                $temp = Strings.trim($temp);
                if (BLANK($temp))
                    continue;
                $temp = Strings.firstCharToUpper($temp);
                if ($category == null)
                    $category = $temp;
                else
                    $category += CAT(", ", $temp);
            }
        }

        return $category;
    }

    /**
     * Extract category.
     * @return String Resulting category.
     */
    private String extractCategory() {
        // Try to extract category from description body (if no $item["category"])

        String $category = null;

        //TODO -- This is just sample - implement your own logic for extracting category
        //if (Config.$RssAllowed == null)
        //    $category = this.$source;

        return $category;
    }

    /**
     * Add standard categories (from DB) to current item.
     * @param $dsCategories DataSet with categories (pre-loaded from DB).
     * @param $lang Input language.
     * @return int Number of added categories.
     */
    public int addStandardCategories(DataSet $dsCategories, String $lang) {
        //if (BLANK(this.$description))
        //    return;

        TArrayList $categoryTags = new TArrayList();
        if (!BLANK(this.$category))
            $categoryTags.addAll(Strings.split(", ", this.$category));
        for (int $n1 = 0; $n1 < $dsCategories.getSize(); $n1++) {
            THashtable $oCategory = $dsCategories.getRow($n1);
            String $rssAllowedKey = STR($oCategory.get("s_CatId"));
            String $name = STR($oCategory.get("s_Name"));

            String $filterValue = STR($oCategory.get("s_Filter"));
            String[] $filterChunks = Strings.split("~", $filterValue);
            String[] $includeChunks = SIZE($filterChunks) > 0 ?
                Strings.split("\\|", $filterChunks[0]) : Strings.emptyArray();
            String[] $excludeChunks = SIZE($filterChunks) > 1 ?
                Strings.split("\\|", $filterChunks[1]) : Strings.emptyArray();

            Boolean $includeFlag = false;
            for (int $n2 = 0; $n2 < SIZE($includeChunks); $n2++) {
                String $includeChunk = $includeChunks[$n2]; //Regex.escape($includeChunks[$n2]);
                if (Regex.isMatch(this.$title, $includeChunk, RegexOptions.IgnoreCase)) {
                    $includeFlag |= true;
                    break;
                }
                if (!BLANK(this.$description) && Regex.isMatch(this.$description, $includeChunk, RegexOptions.IgnoreCase)) {
                    $includeFlag |= true;
                    break;
                }
            }
            for (int $n3 = 0; $n3 < SIZE($excludeChunks); $n3++) {
                String $excludeChunk = $excludeChunks[$n3]; //Regex.escape($excludeChunks[$n3]);
                if (Regex.isMatch(this.$title, $excludeChunk, RegexOptions.IgnoreCase)) {
                    $includeFlag &= false;
                    break;
                }
                if (!BLANK(this.$description) && Regex.isMatch(this.$description, $excludeChunk, RegexOptions.IgnoreCase)) {
                    $includeFlag &= false;
                    break;
                }
           }
            if ($includeFlag)
                $categoryTags.add($name);
        }
        if ($categoryTags.size() == 0)
            return 0;

        //TODO
        //TArrayList $uniqueCategories = this.NormalizeList($categoryTags, $lang);
        //$category = String.join(", ", $uniqueCategories);

        this.$category = Strings.join(", ", (String[])$categoryTags.toArray(
            new String[] {}
        ));

        return $categoryTags.size();
    }

    /**
     * Process creator (publisher, company etc).
     */
    public void processCreator() {
        // Extract creator from item (if it is not set yet)
        if (this.$creator == null) {
            if (!BLANK(this.$item.get("company")))
                this.$creator = STR(this.$item.get("company"));
            else if (!BLANK(this.$item.get("source")))
                this.$creator = STR(this.$item.get("source"));
            else if (!BLANK(this.$item.get("dc"))) { //TODO implement [dc][creator]
                THashtable $temp = (THashtable)this.$item.get("dc");
                if (!BLANK($temp.get("creator")))
                    this.$creator = STR($temp.get("creator"));
            }
        }
        if (this.$creator != null)
            this.$creator = Regex.replace(this.$creator, "[ \t\r\n]+", " ");

        //TODO -- Implement your own logic for extracting creator here
    }

    /**
     * Process rules.
     * @param $rules The list of rules to process.
     */
    public int processRules(DataSet $rules) {
        int $counter = 0;
        for (int $n = 0; $n < $rules.getSize(); $n++) {
            THashtable $rule = $rules.getRow($n);
            String $sourceName = STR($rule.get("s_SourceName"));
            if (EQ($sourceName, "*") || EQ($sourceName, this.$source))
                $counter += this.processRule($rule);
        }
        return $counter;
    }

    private int processRule(THashtable $rule) {
        int $counter = 0;
        String $nameTo = STR($rule.get("s_To"));
        String $valueTo = null;
        String $nameFrom = NUL($rule.get("s_From")) ? $nameTo : STR($rule.get("s_From"));
        String $valueFrom = STR(this.getString($nameFrom));
        String $operation = STR($rule.get("s_Operation"));
        int $intValue = INT($rule.get("i_Value"));
        String $pattern = STR($rule.get("s_Pattern"));
        String $stringValue = STR($rule.get("s_Value"));
        if (EQ($operation, "shrink") && !NUL($valueFrom) && LEN($pattern) > 0) {
            int $shrinkIndex = $valueFrom.indexOf($pattern);
            if ($shrinkIndex != -1) 
                $valueTo = $valueFrom.substring(0, $shrinkIndex).trim();
        }
        if (EQ($operation, "cut") && !NUL($valueFrom) && LEN($pattern) > 0) {
            int $cutIndex = $valueFrom.indexOf($pattern);
            if ($cutIndex == 0) 
                $valueTo = $valueFrom.substring($cutIndex + LEN($pattern));
        }
        if (EQ($operation, "replace") && !NUL($valueFrom) && LEN($pattern) > 0) {
            $valueTo = Regex.replace($valueFrom, $pattern, $stringValue, RegexOptions.IgnoreCase);
            int $replaceIndex = $valueFrom.indexOf($pattern);
            if ($replaceIndex != -1) 
                $valueTo = $valueFrom.replace($pattern, $stringValue);
        }
        if (EQ($operation, "remove") && !NUL($valueFrom) && LEN($pattern) > 0) {
            String[] $matches =
                Regex.matches($valueFrom, $pattern, RegexOptions.IgnoreCase);
            if (SIZE($matches) > 0)
                $valueTo = $valueFrom.replace($matches[0] , "");
        }
        else if (EQ($operation, "truncate") && !NUL($valueFrom) && $intValue > 0) {
            if (LEN($valueFrom) > $intValue) {
                $valueTo = $valueFrom.substring(0, $intValue);
                while (!$valueTo.endsWith(" "))
                    $valueTo = $valueTo.substring(0, LEN($valueTo) - 1);
                $valueTo = $valueTo += "...";
            }
            //print "valueTo: '" + $valueTo + "'<br/>\r\n";
        }
        else if (EQ($operation, "extract") && !NUL($valueFrom)) {
            String[] $matches =
                Regex.matches($valueFrom, $pattern, RegexOptions.IgnoreCase);
            if (SIZE($matches) > $intValue) {
                if (BLANK($stringValue))
                    $valueTo = $matches[$intValue] ;
                else {
                    $valueTo = $stringValue;
                    for (int $n = 0; $n < SIZE($matches); $n++) {
                        if ($valueTo.indexOf(CAT("$", $n)) != -1)
                            $valueTo = $valueTo.replace(CAT("$", $n), $matches[$n] );
                    }
                }
            }
        }
        if (!NUL($valueTo))
            this.setString($nameTo, $valueTo);
        return $counter;
    }

    private void setString(String $name, String $value) {
        if (EQ($name, "link"))
            this.$link = $value;
        else if (EQ($name, "title"))
            this.$title = $value;
        else if (EQ($name, "description"))
            this.$description = $value;
        else if (EQ($name, "date"))
            this.$date = $value;
        else if (EQ($name, "category")) {
            if (BLANK(this.$category))
                this.$category = $value;
            else
                this.$category = CAT($value, ", ", this.$category);
        }
        else if (EQ($name, "creator"))
            this.$creator = $value;
        else if (EQ($name, "custom1"))
            this.$custom1 = $value;
        else if (EQ($name, "custom2"))
            this.$custom2 = $value;
    }

    private String getString(String $name) {
        if (EQ($name, "link"))
            return this.$link;
        else if (EQ($name, "title"))
            return this.$title;
        else if (EQ($name, "description"))
            return this.$description;
        else if (EQ($name, "date"))
            return this.$date;
        else if (EQ($name, "creator"))
            return this.$creator;
        else if (EQ($name, "custom1"))
            return this.$custom1;
        else if (EQ($name, "custom2"))
            return this.$custom2;
        else if (this.$item.containsKey($name))
            return STR(this.$item.get($name));
        return null;
    }

    /**
     * Generate URL title from item title.
     * @return String Resulting URL title.
     */
    public String getUrlTitle() {
        return getUrlTitle(false);
    }

    /**
     * Generate URL title from item title.
     * @param $translit Whether to apply transliteration or not.
     * @return String Resulting URL title.
     *
     * For example:
     * "Officials: Fireworks Spark Utah Wildfire, Evacuations"
     *    will become
     * "officials-fireworks-spark-utah-wildfire-evacuations"
     */
    public String getUrlTitle(Boolean $translit/* = false*/) {
        String $title = Strings.addSlashes(this.$title);

        if ($translit)
            $title = Util.transliterateRusToLat($title);

        $title = Regex.replace($title, "\\&amp\\;", " and ");
        $title = Regex.replace($title, "[^A-Za-z0-9]", "-");
        $title = Regex.replace($title, "[\\-]+", "-");
        $title = Strings.trim($title, "-").toLowerCase();
        return $title;
    }
}
