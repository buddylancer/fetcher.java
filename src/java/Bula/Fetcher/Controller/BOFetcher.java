
package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Internal;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.Arrays;
import Bula.Objects.DateTimes;
import Bula.Objects.Enumerator;
import java.util.Hashtable;
import Bula.Objects.Helper;
import Bula.Objects.Logger;
import Bula.Objects.Request;
import Bula.Objects.Strings;

import Bula.Model.DBConfig;
import Bula.Model.DataSet;

import Bula.Fetcher.Model.DOCategory;
import Bula.Fetcher.Model.DOSource;
import Bula.Fetcher.Model.DOItem;
import Bula.Fetcher.Controller.Actions.DoCleanCache;

/**
 * Logic for fetching data.
 */
public class BOFetcher extends Meta {
    private Context $context = null;
    private Logger $oLogger = null;
    private DataSet $dsCategories = null;

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
        DOCategory $doCategory = new DOCategory();
        this.$dsCategories = $doCategory.enumCategories();
    }

    /**
     * Fetch data from the source.
     * @param $oSource Source object.
     * @return Object[] Resulting items.
     */
    private Object[] fetchFromSource(Hashtable $oSource) {
        String $url = STR($oSource.get("s_Feed"));
        if ($url.isEmpty())
            return null;

        String $source = STR($oSource.get("s_SourceName"));
        if (this.$context.$Request.contains("m") && !$source.equals(this.$context.$Request.get("m")))
            return null;

        this.$oLogger.output(CAT("<br/>", EOL, "Started "));

        //if ($url.indexOf("https") != -1) {
        //    String $encUrl = $url.replace("?", "%3F");
        //    $encUrl = $encUrl.replace("&", "%26");
        //    $url = Strings.concat(Config.$Site, "/get_ssl_rss.php?url=", $encUrl);
        //}
        this.$oLogger.output(CAT("[[[", $url, "]]]<br/>", EOL));
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
     * @return Integer Result of executing SQL-query.
     */
    private int parseItemData(Hashtable $oSource, Hashtable $item) {
        // Load original values

        String $sourceName = STR($oSource.get("s_SourceName"));
        int $sourceId = INT($oSource.get("i_SourceId"));
        BOItem $boItem = new BOItem($sourceName, $item);
        String $pubDate = STR($item.get("pubDate"));
        String $date = DateTimes.format(DateTimes.SQL_DTS, DateTimes.fromRss($pubDate));

        // Check whether item with the same link exists already
        DOItem $doItem = new DOItem();
        DataSet $dsItems = $doItem.findItemByLink($boItem.$link, $sourceId);
        if ($dsItems.getSize() > 0)
            return 0;

        $boItem.processDescription();
        //$boItem.processCustomFields(); // Uncomment for processing custom fields
        $boItem.processCategory();
        $boItem.processCreator();

        // Try to add/embed standard categories from description
        $boItem.addStandardCategories(this.$dsCategories, this.$context.$Lang);

        String $url = $boItem.getUrlTitle(true); //TODO -- Need to pass true if transliteration is required
        Hashtable $fields = new Hashtable();
        $fields.put("s_Link", $boItem.$link);
        $fields.put("s_Title", $boItem.$title);
        $fields.put("s_FullTitle", $boItem.$fullTitle);
        $fields.put("s_Url", $url);
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
     */
    public void fetchFromSources() {
        this.$oLogger.output(CAT("Start logging<br/>", EOL));

        //TODO -- Purge old items
        //$doItem = new DOItem();
        //$doItem.purgeOldItems(10);

        DOSource $doSource = new DOSource();
        DataSet $dsSources = $doSource.enumFetchedSources();

        int $totalCounter = 0;
        this.$oLogger.output(CAT("<br/>", EOL, "Checking ", $dsSources.getSize(), " sources..."));

        // Loop through sources
        for (int $n = 0; $n < $dsSources.getSize(); $n++) {
            Hashtable $oSource = $dsSources.getRow($n);

            Object[] $itemsArray = this.fetchFromSource($oSource);
            if ($itemsArray == null)
                continue;

            // Fetch done for this source
            this.$oLogger.output(" fetched ");

            int $itemsCounter = 0;
            // Loop through fetched items and parse their data
            for (int $i = SIZE($itemsArray) - 1; $i >= 0; $i--) {
                Hashtable $hash = (Hashtable)$itemsArray[$i];
                if (BLANK($hash.get("link")))
                    continue;
                int $itemid = this.parseItemData($oSource, $hash);
                if ($itemid > 0) {
                    $itemsCounter++;
                    $totalCounter++;
                }
            }

            // Release connection after each source
            if (DBConfig.$Connection != null) {
                DBConfig.$Connection.close();
                DBConfig.$Connection = null;
            }

            this.$oLogger.output(CAT(" (", $itemsCounter, " items) end<br/>", EOL));
        }

        // Re-count categories
        this.recountCategories();

        this.$oLogger.output(CAT("<hr/>Total items added - ", $totalCounter, "<br/>", EOL));

        if (Config.CACHE_PAGES && $totalCounter > 0) {
            DoCleanCache $doCleanCache = new DoCleanCache(this.$context);
            $doCleanCache.cleanCache(this.$oLogger);
        }
    }

    /**
     * Execute re-counting of categories.
     */
    private void recountCategories() {
        this.$oLogger.output(CAT("Recount categories ... <br/>", EOL));
        DOCategory $doCategory = new DOCategory();
        DataSet $dsCategories = $doCategory.enumCategories();
        for (int $n = 0; $n < $dsCategories.getSize(); $n++) {
            Hashtable $oCategory = $dsCategories.getRow($n);
            String $id = STR($oCategory.get("s_CatId"));
            String $filter = STR($oCategory.get("s_Filter"));
            DOItem $doItem = new DOItem();
            String $sqlFilter = $doItem.buildSqlFilter($filter);
            DataSet $dsItems = $doItem.enumIds($sqlFilter);
            Hashtable $fields = new Hashtable();
            $fields.put("i_Counter", $dsItems.getSize());
            int $result = $doCategory.updateById($id, $fields);
            if ($result < 0)
                this.$oLogger.output(CAT("-- problems --<br/>", EOL));
        }
        this.$oLogger.output(CAT(" ... Done<br/>", EOL));
    }
}
