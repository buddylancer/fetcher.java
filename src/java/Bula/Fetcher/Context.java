// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher;
import Bula.Meta;

import Bula.Objects.TRequest;
import Bula.Objects.TResponse;
import Bula.Objects.Arrays;
import Bula.Objects.Strings;

import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;

import Bula.Fetcher.Controller.Engine;

import Bula.Model.Connection;



/**
 * Class for request context.
 */
public class Context extends Config {
    /** Default constructor. */
    public Context() throws Exception { initialize(); }

    /**
     * Constructor for injecting TRequest and TResponse.
     * @param $request Current request.
     * @param $response Current response.
     */
    public Context (Object $request/* = null*/, Object $response/* = null*/) throws Exception {
        this.$Request = new TRequest($request);
        this.$Response = new TResponse($response);
        this.$Request.$response = this.$Response;

        this.$Connection = Connection.createConnection();

        this.initialize();
    }

    /** Public desctructor */
    public void close() {
        if (this.$Connection != null) {
            this.$Connection.close();
            this.$Connection = null;
        }
    }

    /** Current DB connection */
    public Connection $Connection = null;
    /** Current request */
    public TRequest $Request = null;
    /** Current response */
    public TResponse $Response = null;

    /** Storage for internal variables */
    protected THashtable $Values = new THashtable();

    /**
     * Get internal variable.
     * @param $name Name of internal variable.
     * @return String Value of variable.
     */
    public String get(String $name) {
        return (String)this.$Values.get($name);
    }

    /**
     * Set internal variable.
     * @param $name Name of internal variable.
     * @param $value Value of internal variable to set.
     */
    public void set(String $name, Object $value) {
        this.$Values.put($name, $value);
    }

    /**
     * Check whether variable is contained in internal storage.
     * @param $name Name of internal variable.
     * @return Boolean True - variable exists, False - not exists.
     */
    public Boolean contains(String $name) {
        return this.$Values.containsKey($name);
    }

    /** Project root (where Bula folder is located) */
    public String $LocalRoot;

    /** Host name (copied from request HOST_NAME) */
    public String $Host;
    /** Site name (copied from Config.SITE_NAME) */
    public String $Site;
    /** Is request for mobile version? */
    public Boolean $IsMobile;
    /** Optional -- API used. Currently can be blank for HTML or "rest" (for REST API) */
    public String $Api;
    /** Current language */
    public String $Lang;
    /** Current file extension */
    /* Filename extension */
    public static final String FILE_EXT = ".jsp";

    /** Root cache folder for pages */
    public String $CacheFolderRoot;
    /** Cache folder for pages */
    public String $CacheFolder;
    /** Root cache folder for output RSS-feeds */
    public String $RssFolderRoot;
    /** Cache folder for output RSS-feeds */
    public String $RssFolder;
    /** Cache folder for input RSS-feeds */
    public String $FeedFolder;
    /** Unique host ID for current request */
    public String $UniqueHostId;

    /** Use fine or full URLs */
    public Boolean $FineUrls = Config.FINE_URLS;
    /** Show an item or immediately redirect to external source item */
    public Boolean $ImmediateRedirect = Config.IMMEDIATE_REDIRECT;

    /** Storage for global constants */
    public THashtable $GlobalConstants = null;

    /** Is current request from test script? */
    public Boolean $TestRun = false;

    /**
     * Check whether current request is from test script?
     */
    public void checkTestRun() {
        String $httpTester = this.$Request.getVar(TRequest.INPUT_SERVER, "HTTP_USER_AGENT");
        if ($httpTester == null)
            return;
        if (EQ($httpTester, "TestFull")) {
            this.$TestRun = true;
            this.$FineUrls = false;
            this.$ImmediateRedirect = false;
            //this.$Site = "http://www.test.com";
        }
        else if (EQ($httpTester, "TestFine")) {
            this.$TestRun = true;
            this.$FineUrls = true;
            this.$ImmediateRedirect = false;
            //this.$Site = "http://www.test.com";
        }
        else if (EQ($httpTester, "TestDirect")) {
            this.$TestRun = true;
            this.$FineUrls = true;
            this.$ImmediateRedirect = true;
            //this.$Site = "http://www.test.com";
        }
    }

    /**
     * Initialize all variables for current request.
     */
    public void initialize() {
        //------------------------------------------------------------------------------
        // You can change something below this line if you know what are you doing :)
        String $rootDir = $Request.$HttpRequest.getRealPath("/"); //TODO!!!
        $rootDir = $rootDir.replace("\\", "/"); // Fix for IIS
        int $removeSlashes =
            2;
        // Regarding that we have the ordinary local website (not virtual directory)
        for (int $n = 0; $n <= $removeSlashes; $n++) {
            int $lastSlashIndex = $rootDir.lastIndexOf("/");
            $rootDir = $rootDir.substring(0, $lastSlashIndex);
        }
        this.$LocalRoot = $rootDir += "/";

        this.$Host = this.$Request.getVar(TRequest.INPUT_SERVER, "HTTP_HOST");
        this.$Site = Strings.concat("http://", this.$Host);
        this.$IsMobile = this.$Host.indexOf("m.") == 0;
        this.$Lang = this.$Host.lastIndexOf(".ru") != -1 ? "ru" : "en";

        this.checkTestRun();
        this.$UniqueHostId = Strings.concat(
            this.$IsMobile ? "mob_" : "www_",
            this.$FineUrls ? (this.$ImmediateRedirect ? "direct_" : "fine_") : "full_",
            this.$Lang);
        this.$CacheFolderRoot = Strings.concat(this.$LocalRoot, "local/cache/www");
        this.$CacheFolder = Strings.concat(this.$CacheFolderRoot, "/", this.$UniqueHostId);
        this.$RssFolderRoot = Strings.concat(this.$LocalRoot, "local/cache/rss");
        this.$RssFolder = Strings.concat(this.$RssFolderRoot, "/", this.$UniqueHostId);
        this.$FeedFolder = Strings.concat(this.$LocalRoot, "local/cache/feed");

        this.defineConstants();
    }

    /**
     * Define global constants.
     */
    private void defineConstants() {
        this.$GlobalConstants = new THashtable();
        this.$GlobalConstants.put("[#Site_Name]", Config.SITE_NAME);
        this.$GlobalConstants.put("[#Site_Comments]", Config.SITE_COMMENTS);
        this.$GlobalConstants.put("[#Top_Dir]", Config.TOP_DIR);

        if (!this.$TestRun)
            this.$GlobalConstants.put("[#File_Ext]", FILE_EXT);
        this.$GlobalConstants.put("[#Index_Page]", this.$TestRun ? Config.INDEX_PAGE :
            Strings.replace("[#File_Ext]", FILE_EXT, Config.INDEX_PAGE));
        this.$GlobalConstants.put("[#Action_Page]", this.$TestRun ? Config.ACTION_PAGE :
            Strings.replace("[#File_Ext]", FILE_EXT, Config.ACTION_PAGE));
        this.$GlobalConstants.put("[#Rss_Page]", this.$TestRun ? Config.RSS_PAGE :
            Strings.replace("[#File_Ext]", FILE_EXT, Config.RSS_PAGE));

        //if (this.$IsMobile)
        //    this.$GlobalConstants.put("[#Is_Mobile]", "1");
        this.$GlobalConstants.put("[#Lang]", this.$Lang);

        java.lang.reflect.Field fieldInfo = null;
        try {
			fieldInfo = Config.class.getField("NAME_CATEGORY");
			if (fieldInfo != null) set("Name_Category", fieldInfo.get(null));
			fieldInfo = Config.class.getField("NAME_CATEGORIES");
			if (fieldInfo != null) set("Name_Categories", fieldInfo.get(null));
			fieldInfo = Config.class.getField("NAME_CREATOR");
			if (fieldInfo != null) set("Name_Creator", fieldInfo.get(null));
			fieldInfo = Config.class.getField("NAME_CUSTOM1");
			if (fieldInfo != null) set("Name_Custom1", fieldInfo.get(null));
			fieldInfo = Config.class.getField("NAME_CUSTOM2");
			if (fieldInfo != null) set("Name_Custom2", fieldInfo.get(null));
		}
		catch (Exception ex) { } //TODO

        // Map custom names
        this.$GlobalConstants.put("[#Name_Item]", Config.NAME_ITEM);
        this.$GlobalConstants.put("[#Name_Items]", Config.NAME_ITEMS);
        if (this.contains("Name_Category"))
            this.$GlobalConstants.put("[#Name_Category]", this.get("Name_Category"));
        if (this.contains("Name_Categories"))
            this.$GlobalConstants.put("[#Name_Categories]", this.get("Name_Categories"));
        if (this.contains("Name_Creator"))
            this.$GlobalConstants.put("[#Name_Creator]", this.get("Name_Creator"));
        if (this.contains("Name_Custom1"))
            this.$GlobalConstants.put("[#Name_Custom1]", this.get("Name_Custom1"));
        if (this.contains("Name_Custom2"))
            this.$GlobalConstants.put("[#Name_Custom2]", this.get("Name_Custom2"));
    }

    private TArrayList $EngineInstances = null;
    private int $EngineIndex = -1;

    /**
     * Push engine.
     * @param $printFlag Whether to print content immediately (true) or save it for further processing (false).
     * @return Engine New Engine instance.
     */
    public Engine pushEngine(Boolean $printFlag) {
        Engine $engine = new Engine(this);
        $engine.setPrintFlag($printFlag);
        this.$EngineIndex++;
        if (this.$EngineInstances == null)
            this.$EngineInstances = new TArrayList();
        if (this.$EngineInstances.size() <= this.$EngineIndex)
            this.$EngineInstances.add($engine);
        else
            this.$EngineInstances.set(this.$EngineIndex, $engine);
        return $engine;
    }

    /** Pop engine back. */
    public void popEngine() {
        if (this.$EngineIndex == -1)
            return;
        Engine $engine = (Engine)this.$EngineInstances.get(this.$EngineIndex);
        $engine.setPrintString(null);
        //TODO Dispose engine?
        this.$EngineIndex--;
    }

    /**
     * Get current engine
     * @return Engine Current engine instance.
     */
    public Engine getEngine() {
        return (Engine)this.$EngineInstances.get(this.$EngineIndex);
    }
}
