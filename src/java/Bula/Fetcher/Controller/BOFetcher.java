// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Internal;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.Arrays;
import Bula.Objects.DateTimes;
import Bula.Objects.TEnumerator;
import Bula.Objects.THashtable;
import Bula.Objects.Helper;
import Bula.Objects.Logger;
import Bula.Objects.TRequest;
import Bula.Objects.Strings;

import Bula.Model.DBConfig;
import Bula.Model.DataSet;

import Bula.Fetcher.Model.DOCategory;
import Bula.Fetcher.Model.DOItem;
import Bula.Fetcher.Model.DOMapping;
import Bula.Fetcher.Model.DORule;
import Bula.Fetcher.Model.DOSource;
import Bula.Fetcher.Controller.Actions.DoCleanCache;

/**
 * Logic for fetching data.
 */
public class BOFetcher extends Meta {
    private Context $context = null;
    private Logger $oLogger = null;
    private DataSet $dsCategories = null;
    private DataSet $dsRules = null;
    private DataSet $dsMappings = null;

    /** Public default constructor */
    public BOFetcher (Context $context) {
        this.$context = $context;
        this.initializeLog();
        this.preLoadCategories();
    }

    /**
     * Initialize logging.
     */
    private void initializeLog() {
        this.$oLogger = new Logger();
        this.$context.set("Log_Object", this.$oLogger);
        int $log = this.$context.$Request.getOptionalInteger("log");
        if (!NUL($log) && $log != -99999) { //TODO
            String $filenameTemplate = new String(CAT(this.$context.$LocalRoot, "local/logs/{0}_{1}.html"));
            String $filename = Util.formatString($filenameTemplate, ARR("fetch_items", DateTimes.format(DateTimes.LOG_DTS)));
            this.$oLogger.initFile($filename);
        }
        else
            this.$oLogger.initResponse(this.$context.$Response);
    }

    /**
     * Pre-load categories into DataSet.
     */
    private void preLoadCategories() {
        DOCategory $doCategory = new DOCategory(this.$context.$Connection);
        this.$dsCategories = $doCategory.enumCategories();
        DORule $doRule = new DORule(this.$context.$Connection);
        this.$dsRules = $doRule.enumAll();
        DOMapping $doMapping = new DOMapping(this.$context.$Connection);
        this.$dsMappings = $doMapping.enumAll();
    }

    /**
     * Fetch data from the source.
     * @param $oSource Source object.
     * @return Object[] Resulting items.
     * @param $from Addition to feed URL (for testing purposes)
     */
    private Object[] fetchFromSource(THashtable $oSource, String $from) {
        String $url = STR($oSource.get("s_Feed"));
        if ($url.isEmpty())
            return null;

        if (!NUL($from))
            $url = Strings.concat($url, "&from=", $from);

        if ($url.indexOf("[#File_Ext]") != -1)
            $url = $url.replace("[#File_Ext]", Context.FILE_EXT);

        String $source = STR($oSource.get("s_SourceName"));
        if (this.$context.$Request.contains("m") && !$source.equals(this.$context.$Request.get("m")))
            return null;

        this.$oLogger.output(CAT("<br/>", EOL, "Started "));

        //if ($url.indexOf("https") != -1) {
        //    String $encUrl = $url.replace("?", "%3F");
        //    $encUrl = $encUrl.replace("&", "%26");
        //    $url = Strings.concat(Config.$Site, "/get_ssl_rss.php?url=", $encUrl);
        //}
        this.$oLogger.output(CAT("[[[", $url, "]]]"));
        Object[] $rss = Internal.fetchRss($url);
        if ($rss == null) {
            this.$oLogger.output(CAT("-- problems --<br/>", EOL));
            //$problems++;
            //if ($problems == 5) {
            //    this.$oLogger.output(CAT("<br/>", EOL, "Too many problems... Stopped.<br/>", EOL));
            //    break;
            //}
            return null;
        }
        return $rss;
    }

    /**
     * Parse data from the item.
     * @param $oSource Source object.
     * @param $item Item object.
     * @return int Result of executing SQL-query.
     */
    private int parseItemData(THashtable $oSource, THashtable $item) {
        // Load original values

        String $sourceName = STR($oSource.get("s_SourceName"));
        int $sourceId = INT($oSource.get("i_SourceId"));
        BOItem $boItem = new BOItem($sourceName, $item);
        String $pubDate = STR($item.get("pubDate"));
        if (BLANK($pubDate) && !BLANK($item.get("dc"))) { //TODO implement [dc][time]
            THashtable $temp = (THashtable)$item.get("dc");
            if (!BLANK($temp.get("date"))) {
                $pubDate = STR($temp.get("date"));
                $item.put("pubDate", $pubDate);
            }
        }

        $boItem.processMappings(this.$dsMappings);

        $boItem.processDescription();
        //$boItem.processCustomFields(); // Uncomment for processing custom fields
        $boItem.processCategory();
        $boItem.processCreator();

        // Process rules AFTER processing description (as some info can be extracted from it)
        $boItem.processRules(this.$dsRules);

        if (BLANK($boItem.$link)) //TODO - what we can do else?
            return 0;

        // Get date here as it can be extracted in rules processing
        if ($boItem.$date != null)
            $pubDate = $boItem.$date;
        if (!BLANK($pubDate))
            $pubDate = $pubDate.trim();
        String $date = DateTimes.gmtFormat(DateTimes.SQL_DTS, DateTimes.fromRss($pubDate));

        // Check whether item with the same link exists already
        DOItem $doItem = new DOItem(this.$context.$Connection);
        DataSet $dsItems = $doItem.findItemByLink($boItem.$link, $sourceId);
        if ($dsItems.getSize() > 0)
            return 0;

        // Try to add/embed standard categories from description
        int $countCategories = $boItem.addStandardCategories(this.$dsCategories, this.$context.$Lang);

        $boItem.normalizeCategories();

        // Check the link once again after processing rules
        if ($dsItems == null && !BLANK($boItem.$link)) {
            $doItem.findItemByLink($boItem.$link, $sourceId);
            if ($dsItems.getSize() > 0)
                return 0;
        }

        String $url = $boItem.getUrlTitle(true); //TODO -- Need to pass true if transliteration is required
        THashtable $fields = new THashtable();
        $fields.put("s_Link", $boItem.$link);
        $fields.put("s_Title", $boItem.$title);
        $fields.put("s_FullTitle", $boItem.$fullTitle);
        $fields.put("s_Url", $url);
        $fields.put("i_Categories", $countCategories);
        if ($boItem.$description != null)
            $fields.put("t_Description", $boItem.$description);
        if ($boItem.$fullDescription != null)
            $fields.put("t_FullDescription", $boItem.$fullDescription);
        $fields.put("d_Date", $date);
        $fields.put("i_SourceLink", INT($oSource.get("i_SourceId")));
        if (!BLANK($boItem.$category))
            $fields.put("s_Category", $boItem.$category);
        if (!BLANK($boItem.$creator))
            $fields.put("s_Creator", $boItem.$creator);
        if (!BLANK($boItem.$custom1))
            $fields.put("s_Custom1", $boItem.$custom1);
        if (!BLANK($boItem.$custom2))
            $fields.put("s_Custom2", $boItem.$custom2);

        int $result = $doItem.insert($fields);
        return $result;
    }

    /**
     * Main logic.
     * @param $from Addition to feed URL (for testing purposes)
     */
    public void fetchFromSources(String $from) {
        this.$oLogger.output(CAT("Start logging<br/>", EOL));

        //TODO -- Purge old items
        //$doItem = new DOItem(this.$context.$Connection);
        //$doItem.purgeOldItems(10);

        DOSource $doSource = new DOSource(this.$context.$Connection);
        DataSet $dsSources = $doSource.enumFetchedSources();

        int $totalCounter = 0;
        this.$oLogger.output(CAT("<br/>", EOL, "Checking ", $dsSources.getSize(), " sources..."));

        // Loop through sources
        for (int $n = 0; $n < $dsSources.getSize(); $n++) {
            THashtable $oSource = $dsSources.getRow($n);

            Object[] $itemsArray = this.fetchFromSource($oSource, $from);
            if ($itemsArray == null)
                continue;

            // Fetch done for this source
            //this.$oLogger.output(" fetched ");

            int $itemsCounter = 0;
            // Loop through fetched items and parse their data
            for (int $i = SIZE($itemsArray) - 1; $i >= 0; $i--) {
                THashtable $hash = (THashtable)$itemsArray[$i];
                if (BLANK($hash.get("link")))
                    continue;
                int $itemid = this.parseItemData($oSource, $hash);
                if ($itemid > 0) {
                    $itemsCounter++;
                    $totalCounter++;
                }
            }
            this.$oLogger.output(CAT("<br/>", EOL, "... fetched (", $itemsCounter, " items) end"));
        }

        // Re-count categories
        this.recountCategories();

        this.$oLogger.output(CAT("<br/>", EOL, "<hr/>Total items added - ", $totalCounter, "<br/>", EOL));

        if (Config.CACHE_PAGES && $totalCounter > 0) {
            DoCleanCache $doCleanCache = new DoCleanCache(this.$context);
            $doCleanCache.cleanCache(this.$oLogger);
        }
    }

    /**
     * Execute re-counting of categories.
     */
    private void recountCategories() {
        this.$oLogger.output(CAT("<br/>", EOL, "Recount categories ... "));
        DOCategory $doCategory = new DOCategory(this.$context.$Connection);
        DOItem $doItem = new DOItem(this.$context.$Connection);
        DataSet $dsCategories = $doCategory.enumCategories();
        for (int $n = 0; $n < $dsCategories.getSize(); $n++) {
            THashtable $oCategory = $dsCategories.getRow($n);
            String $categoryId = STR($oCategory.get("s_CatId"));
            int $oldCounter = INT($oCategory.get("i_Counter"));

            //String $filter = STR($oCategory.get("s_Filter"));
            //String $sqlFilter = DOItem.buildSqlByFilter($filter);

            String $categoryName = STR($oCategory.get("s_Name"));
            String $sqlFilter = DOItem.buildSqlByCategory($categoryName);

            DataSet $dsCounters = $doItem.enumIds(CAT("_this.b_Counted = 0 AND ", $sqlFilter));
            if ($dsCounters.getSize() == 0)
                continue;

            int $newCounter = INT($dsCounters.getSize());

            //Update category
            THashtable $categoryFields = new THashtable();
            $categoryFields.put("i_Counter", $oldCounter + $newCounter);
            $doCategory.updateById($categoryId, $categoryFields);
        }

        $doItem.update("_this.b_Counted = 1", "_this.b_Counted = 0");

        this.$oLogger.output(CAT(" ... Done<br/>", EOL));
    }
}
