
package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import java.util.ArrayList;
import Bula.Objects.Enumerator;
import java.util.Hashtable;
import Bula.Objects.Regex;
import Bula.Objects.RegexOptions;

import Bula.Objects.Request;
import Bula.Objects.Response;

import Bula.Objects.DateTimes;
import Bula.Objects.Helper;
import Bula.Objects.Strings;

import Bula.Model.DBConfig;
import Bula.Model.DataSet;

import Bula.Fetcher.Model.DOCategory;
import Bula.Fetcher.Model.DOItem;
import Bula.Fetcher.Model.DOSource;

import Bula.Fetcher.Controller.Util;
import Bula.Fetcher.Controller.Page;

/**
 * Main logic for generating RSS-feeds and REST API responses.
 */
abstract class RssBase extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public RssBase(Context $context) { super($context); }

    /**
     * Execute main logic for generating RSS-feeds.
     */
    public void execute() {
        //this.$context.$Request.initialize();
        this.$context.$Request.extractAllVars();

        String $errorMessage = new String();

        // Check source
        String $source = this.$context.$Request.get("source");
        if (!NUL($source)) {
            if (BLANK($source))
                $errorMessage += "Empty source!";
            else {
                DOSource $doSource = new DOSource();
                Hashtable[] $oSource =
                    {new Hashtable()};
                if (!$doSource.checkSourceName($source, $oSource))
                    $errorMessage += CAT("Incorrect source '", $source, "'!");
            }
        }

        Boolean $anyFilter = false;
        if (this.$context.$Request.contains("code")) {
            if (EQ(this.$context.$Request.get("code"), Config.SECURITY_CODE))
                $anyFilter = true;
        }

        // Check filter
        String $filter = null;
        String $filterName = null;
        DOCategory $doCategory = new DOCategory();
        DataSet $dsCategories = $doCategory.enumCategories();
        if ($dsCategories.getSize() > 0) {
            $filterName = this.$context.$Request.get("filter");
            if (!NUL($filterName)) {
                if (BLANK($filterName)) {
                    if ($errorMessage.length() > 0)
                        $errorMessage += " ";
                    $errorMessage += "Empty filter!";
                }
                else {
                    Hashtable[] $oCategory =
                        {new Hashtable()};
                    if ($doCategory.checkFilterName($filterName, $oCategory))
                        $filter = STR($oCategory[0].get("s_Filter"));
                    else {
                        if ($anyFilter)
                            $filter = $filterName;
                        else {
                            if ($errorMessage.length() > 0)
                                $errorMessage += " ";
                            $errorMessage += CAT("Incorrect filter '", $filterName, "'!");
                        }
                    }
                }
            }
        }

        // Check that parameters contain only 'source' or/and 'filter'
        Enumerator $keys = this.$context.$Request.getKeys();
        while ($keys.hasMoreElements()) {
            String $key = (String)$keys.nextElement();
            if (EQ($key, "source") || EQ($key, "filter") || EQ($key, "code") || EQ($key, "count")) {
                //OK
            }
            else {
                //Not OK
                if ($errorMessage.length() > 0)
                    $errorMessage += " ";
                $errorMessage += CAT("Incorrect parameter '", $key, "'!");
            }
        }

        if ($errorMessage.length() > 0) {
            this.writeErrorMessage($errorMessage);
            return;
        }

        Boolean $fullTitle = false;
        if (this.$context.$Request.contains("title") && STR(this.$context.$Request.get("title")) == "full")
            $fullTitle = true;

        int $count = Config.MAX_RSS_ITEMS;
        Boolean $countSet = false;
        if (this.$context.$Request.contains("count")) {
            if (INT(this.$context.$Request.get("count")) > 0) {
                $count = INT(this.$context.$Request.get("count"));
                if ($count < Config.MIN_RSS_ITEMS)
                    $count = Config.MIN_RSS_ITEMS;
                if ($count > Config.MAX_RSS_ITEMS)
                    $count = Config.MAX_RSS_ITEMS;
                $countSet = true;
            }
        }

        // Get content from cache (if enabled and cache data exists)
        String $cachedFile = new String();
        if (Config.CACHE_RSS && !$countSet) {
            $cachedFile = Strings.concat(
                this.$context.$RssFolder, "/rss",
                (BLANK($source) ? null : CAT("-s=", $source)),
                (BLANK($filterName) ? null : CAT("-f=", $filterName)),
                ($fullTitle ? "-full" : null), ".xml");
            if (Helper.fileExists($cachedFile)) {
                this.$context.$Response.writeHeader("Content-type", "text/xml; charset=UTF-8");
                String $tempContent = Helper.readAllText($cachedFile);
                //this.$context.$Response.write($tempContent.substring(3)); //TODO -- BOM?
                this.$context.$Response.write($tempContent); //TODO -- BOM?
                return;
            }
        }

        DOItem $doItem = new DOItem();

        // 0 - item url
        // 1 - item title
        // 2 - marketplace url
        // 3 - marketplace name
        // 4 - date
        // 5 - description
        // 6 - category

        String $pubDate = DateTimes.format(DateTimes.XML_DTS);
        String $nowDate = DateTimes.format(DateTimes.SQL_DTS);
        long $nowTime = DateTimes.getTime($nowDate);
        String $fromDate = DateTimes.gmtFormat(DateTimes.SQL_DTS, $nowTime - 6*60*60);
        DataSet $dsItems = $doItem.enumItemsFromSource($fromDate, $source, $filter, $count);
        int $current = 0;

        String $contentToCache = "";
        if ($dsItems.getSize() == 0)
            $contentToCache = this.writeStart($source, $filterName, $pubDate);

        for (int $n = 0; $n < $dsItems.getSize(); $n++) {
            Hashtable $oItem = $dsItems.getRow($n);
            String $date = STR($oItem.get("d_Date"));
            if (DateTimes.getTime($date) > $nowTime)
                continue;

            if ($current == 0) {
                // Get puDate from the first item and write starting block
                $pubDate = DateTimes.format(DateTimes.XML_DTS, DateTimes.getTime($date));
                $contentToCache = this.writeStart($source, $filterName, $pubDate);
            }

            String $category = this.$context.contains("Name_Category") ? STR($oItem.get("s_Category")) : null;
            String $creator = this.$context.contains("Name_Creator") ? STR($oItem.get("s_Creator")) : null;
            String $custom1 = this.$context.contains("Name_Custom1") ? STR($oItem.get("s_Custom1")) : null;
            String $custom2 = this.$context.contains("Name_Custom2") ? STR($oItem.get("s_Custom2")) : null;

            String $sourceName = STR($oItem.get("s_SourceName"));
            String $description = STR($oItem.get("t_Description"));
            if (!BLANK($description)) {
                $description = Regex.replace($description, "<br/>", " ", RegexOptions.IgnoreCase);
                $description = Regex.replace($description, "&nbsp;", " ");
                $description = Regex.replace($description, "[ \r\n\t]+", " ");
                if ($description.length() > 512) {
                    $description = $description.substring(0, 511);
                    int $lastSpaceIndex = $description.lastIndexOf(" ");
                    $description = Strings.concat($description.substring(0, $lastSpaceIndex), " ...");
                }
                //Boolean $utfIsValid = mb_check_encoding($description, "UTF-8");
                //if ($utfIsValid == false)
                //    $description = new String(); //TODO
            }
            String $itemTitle = CAT(
                ($fullTitle == true && !BLANK($custom2) ? CAT($custom2, " | ") : null),
                Strings.removeTags(Strings.stripSlashes(STR($oItem.get("s_Title")))),
                ($fullTitle == true ? CAT(" [", $sourceName, "]") : null)
            );

            String $link = null;
            if (this.$context.$ImmediateRedirect)
                $link = STR($oItem.get("s_Link"));
            else {
                String $url = STR($oItem.get("s_Url"));
                String $idField = $doItem.getIdField();
                $link = this.getAbsoluteLink(Config.INDEX_PAGE, "?p=view_item&amp;id=", "item/", $oItem.get($idField));
                if (!BLANK($url))
                    $link = this.appendLink($link, "&amp;title=", "/", $url);
            }

            Object[] $args = ARR(7);
            $args[0] = $link;
            $args[1] = $itemTitle;
            $args[2] = this.getAbsoluteLink(Config.ACTION_PAGE, "?p=do_redirect_source&amp;source=", "redirect/source/", $sourceName);
            $args[3] = $sourceName;
            $args[4] = DateTimes.format(DateTimes.XML_DTS, DateTimes.getTime($date));
            String $additional = CAT(
                (BLANK($creator) ? null : CAT(this.$context.get("Name_Creator"), ": ", $creator, "<br/>")),
                (BLANK($category) ? null : CAT(this.$context.get("Name_Categories"), ": ", $category, "<br/>")),
                (BLANK($custom2) ? null : CAT(this.$context.get("Name_Custom2"), ": ", $custom2, "<br/>")),
                (BLANK($custom1) ? null : CAT(this.$context.get("Name_Custom1"), ": ", $custom1, "<br/>"))
            );
            String $extendedDescription = null;
            if (!BLANK($description)) {
                if (BLANK($additional))
                    $extendedDescription = $description;
                else
                    $extendedDescription = CAT($additional, "<br/>", $description);
            }
            else if (!BLANK($additional))
                $extendedDescription = $additional;
            $args[5] = $extendedDescription;
            $args[6] = $category;

            String $itemContent = this.writeItem($args);
            if (!BLANK($itemContent))
                $contentToCache += $itemContent;

            $current++;
        }

        String $endContent = this.writeEnd();
        if (!BLANK($endContent))
            $contentToCache += $endContent;

        // Save content to cache (if applicable)
        if (Config.CACHE_RSS && !$countSet) {
            Helper.testFileFolder($cachedFile);
            //Helper.writeText($cachedFile, Strings.concat("\\xEF\\xBB\\xBF", $xmlContent));
            Helper.writeText($cachedFile, $contentToCache);
        }

        if (DBConfig.$Connection != null) {
            DBConfig.$Connection.close();
            DBConfig.$Connection = null;
        }
    }

    /**
     * Write error message.
     * @param $errorMessage Error message.
     */
    public abstract void writeErrorMessage(String $errorMessage);

    /**
     * Write start block (header) of an RSS-feed.
     * @param $source Source selected (or empty).
     * @param $filterName Filter name selected (or empty).
     * @param $pubDate Date shown in the header.
     */
    public abstract String writeStart(String $source, String $filterName, String $pubDate);

    /**
     * Write end block of an RSS-feed.
     */
    public abstract String writeEnd();

    /**
     * Write RSS-feed item.
     * @param[] $args Parameters to fill an item.
     */
    public abstract String writeItem(Object[] $args);
}
