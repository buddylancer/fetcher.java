// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Actions;
import Bula.Meta;

import Bula.Objects.DateTimes;
import Bula.Objects.TEnumerator;
import Bula.Objects.Helper;
import Bula.Objects.Logger;
import Bula.Objects.TRequest;
import Bula.Objects.Strings;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Fetcher.Controller.Page;
import Bula.Fetcher.Controller.Util;

/**
 * Action for cleaning cache.
 */
public class DoCleanCache extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public DoCleanCache(Context $context) { super($context); }

    /** Execute main logic for DoCleanCache action */
    public void execute() {
        Logger $oLogger = new Logger();
        int $log = this.$context.$Request.getOptionalInteger("log");
        if (!NUL($log) && $log != -99999) {
            String $filenameTemplate = new String("C:/Temp/Log_{0}_{1}.html");
            String $filename = Util.formatString($filenameTemplate, ARR("do_clean_cache", DateTimes.format(DateTimes.SQL_DTS)));
            $oLogger.initFile($filename);
        }
        else
            $oLogger.initResponse(this.$context.$Response);
        this.cleanCache($oLogger);
    }

    /**
     * Actual cleaning of cache folder.
     * @param $oLogger Logger instance.
     * @param $pathName Cache folder name (path).
     * @param $ext Files extension to clean.
     */
    private void cleanCacheFolder(Logger $oLogger, String $pathName, String $ext) {
        if (!Helper.dirExists($pathName))
            return;

        TEnumerator $entries = Helper.listDirEntries($pathName);
        while ($entries.moveNext()) {
            String $entry = (String)$entries.getCurrent();

            if (Helper.isFile($entry) && $entry.endsWith($ext)) {
                $oLogger.output(CAT("Deleting of ", $entry, " ...<br/>", EOL));
                Helper.deleteFile($entry);
            }
            else if (Helper.isDir($entry)) {
                $oLogger.output(CAT("Drilling to ", $entry, " ...<br/>", EOL));
                cleanCacheFolder($oLogger, $entry, $ext);
            }
            //unlink($pathName); //Comment for now -- dangerous operation!!!
        }
    }

    /**
     * Clean all cached info (both for Web and RSS).
     */
    public void cleanCache(Logger $oLogger) {
        // Clean cached rss content
        $oLogger.output(CAT("Cleaning Rss Folder ", this.$context.$RssFolderRoot, " ...<br/>", EOL));
        String $rssFolder = Strings.concat(this.$context.$RssFolderRoot);
        this.cleanCacheFolder($oLogger, $rssFolder, ".xml");

        // Clean cached pages content
        $oLogger.output(CAT("Cleaning Cache Folder ", this.$context.$CacheFolderRoot,  "...<br/>", EOL));
        String $cacheFolder = Strings.concat(this.$context.$CacheFolderRoot);
        this.cleanCacheFolder($oLogger, $cacheFolder, ".cache");

        $oLogger.output(CAT("<br/>... Done.<br/>", EOL));
    }

}
