
package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;

import java.util.ArrayList;
import java.util.Hashtable;
import Bula.Objects.Regex;
import Bula.Objects.RegexOptions;

import Bula.Objects.Strings;
import Bula.Model.DataSet;

/**
 * Manipulating with items.
 */
public class BOItem extends Meta {
    // Input fields
    /** Source name */
    private String $source = null;
    /** RSS-item */
    private Hashtable $item = null;

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
    public BOItem (String $source, Hashtable $item) {
        this.initialize($source, $item);
    }

    /**
     * Initialize this BOItem.
     * @param $source Current processed source.
     * @param $item Current processed RSS-item from given source.
     */
    private void initialize(String $source, Hashtable $item) {
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
        $title = $title.replace("&#", "[--amp--]");
        $title = $title.replace("#", "[sharp]");
        $title = $title.replace("[--amp--]", "&#");
        $title = $title.replace("&amp;", "&");
        this.$title = $title;

        if (this.$fullDescription == null)
            return;
        String $description = this.$fullDescription;

        //TODO -- Fixes for FetchRSS feeds (parsed from Twitter) here...
        $description = $description.replace("&#160;", "");
        $description = $description.replace("&nbsp;", "");

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
        while ($description.indexOf(" \n") != -1)
            $description = $description.replace(" \n", "\n");
        while ($description.indexOf("\n\n\n") != -1)
            $description = $description.replace("\n\n\n", "\n\n");
        $description = Regex.replace($description, "\n\n[ \t]*[+\\-\\*][^+\\-\\*][ \t]*", "\n* ");
        $description = Regex.replace($description, "[ \t]+", " ");

        this.$description = $description.trim();
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
            String[] $categoriesArr = $categoryItem.replace(",&,", " & ").split(",");
            ArrayList $categoriesNew = new ArrayList();
            for (int $c = 0; $c < SIZE($categoriesArr); $c++) {
                String $temp = $categoriesArr[$c];
                if (!BLANK($temp))
                    $categoriesNew.add($temp);
            }
            $category = Strings.join(", ", (String[])$categoriesNew.toArray());
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
     */
    public void addStandardCategories(DataSet $dsCategories, String $lang) {
        //if (BLANK(this.$description))
        //    return;

        String[] $categoryTags = BLANK(this.$category) ?
            Strings.emptyArray() : this.$category.split(",");
        for (int $n1 = 0; $n1 < $dsCategories.getSize(); $n1++) {
            Hashtable $oCategory = $dsCategories.getRow($n1);
            String $rssAllowedKey = STR($oCategory.get("s_CatId"));
            String $name = STR($oCategory.get("s_Name"));

            String $filterValue = STR($oCategory.get("s_Filter"));
            String[] $filterChunks = Strings.split("~", $filterValue);
            String[] $includeChunks = SIZE($filterChunks) > 0 ?
                Strings.split("|", $filterChunks[0]) : Strings.emptyArray();
            String[] $excludeChunks = SIZE($filterChunks) > 1 ?
                Strings.split("|", $filterChunks[1]) : Strings.emptyArray();

            Boolean $includeFlag = false;
            for (int $n2 = 0; $n2 < SIZE($includeChunks); $n2++) {
                String $includeChunk = Regex.escape($includeChunks[$n2]);
                if (!BLANK(this.$description) && Regex.isMatch(this.$description, $includeChunk, RegexOptions.IgnoreCase))
                    $includeFlag |= true;
                if (Regex.isMatch(this.$title, $includeChunk, RegexOptions.IgnoreCase))
                    $includeFlag |= true;
            }
            for (int $n3 = 0; $n3 < SIZE($excludeChunks); $n3++) {
                String $excludeChunk = Regex.escape($excludeChunks[$n3]);
                if (!BLANK(this.$description) && Regex.isMatch(this.$description, $excludeChunk, RegexOptions.IgnoreCase))
                    $includeFlag &= false;
                if (Regex.isMatch(this.$title, $excludeChunk, RegexOptions.IgnoreCase))
                    $includeFlag |= true;
            }
            if ($includeFlag) {
            }
        }
        if (SIZE($categoryTags) == 0)
            return;

        //TODO
        //ArrayList $uniqueCategories = this.NormalizeList($categoryTags, $lang);
        //$category = String.join(", ", $uniqueCategories);

        this.$category = Strings.join(", ", $categoryTags);
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
                Hashtable $temp = (Hashtable)this.$item.get("dc");
                if (!BLANK($temp.get("creator")))
                    this.$creator = STR($temp.get("creator"));
            }
        }
        if (this.$creator != null)
            this.$creator = Regex.replace(this.$creator, "[ \t\r\n]+", " ");

        //TODO -- Implement your own logic for extracting creator here
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
        $title = Regex.replace($title, "[^A-Za-z0-9\\-\\. ]", " ");
        $title = Regex.replace($title, " +", " ");
        $title = $title.trim();
        $title = Regex.replace($title, "\\.+", "-");
        $title = Regex.replace($title, " \\- ", "-");
        $title = Regex.replace($title, " \\. ", ".");
        $title = Regex.replace($title, "[ ]+", "-");
        $title = Regex.replace($title, "\\-+", "-");
        $title = Strings.trim($title, "-").toLowerCase();
        return $title;
    }
}
