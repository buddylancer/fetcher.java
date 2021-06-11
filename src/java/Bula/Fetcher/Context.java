
package Bula.Fetcher;
import Bula.Meta;

import Bula.Objects.Request;
import Bula.Objects.Arrays;
import Bula.Objects.Strings;

import java.util.ArrayList;
import java.util.Hashtable;

import Bula.Fetcher.Controller.Engine;

/**
 * Class for request context.
 */
public class Context extends Config {
    /** Default constructor. */
    public Context () throws Exception {
        this.initialize();
    }

    /** Storage for internal variables */
    protected Hashtable $Values = new Hashtable();

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
        return this.$Values.contains($name);
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
    public Hashtable $GlobalConstants = null;

    /** Is current request from test script? */
    public Boolean $TestRun = false;

    /**
     * Check whether current request is from test script?
     */
    public void checkTestRun() {
        String $httpTester = Request.getVar(Request.INPUT_SERVER, "HTTP_USER_AGENT");
        if ($httpTester == null)
            return;
        if (EQ($httpTester, "TestFull")) {
            this.$TestRun = true;
            this.$FineUrls = false;
            this.$ImmediateRedirect = false;
            this.$Site = "http://www.test.com";
        }
        else if (EQ($httpTester, "TestFine")) {
            this.$TestRun = true;
            this.$FineUrls = true;
            this.$ImmediateRedirect = false;
            this.$Site = "http://www.test.com";
        }
        else if (EQ($httpTester, "TestDirect")) {
            this.$TestRun = true;
            this.$FineUrls = true;
            this.$ImmediateRedirect = true;
            this.$Site = "http://www.test.com";
        }
    }

    /**
     * Initialize all variables for current request.
     */
    public void initialize() throws Exception {
        //------------------------------------------------------------------------------
        // You can change something below this line if you know what are you doing :)
        String $rootDir = Request.getVar(Request.INPUT_SERVER, "APPL_PHYSICAL_PATH"); //TODO!!!
        $rootDir = $rootDir.replace("\\", "/"); // Fix for IIS
        // Regarding that we have the ordinary local website (not virtual directory)
        for (int $n = 0; $n <= 2; $n++) {
            int $lastSlashIndex = $rootDir.lastIndexOf("/");
            $rootDir = $rootDir.substring(0, $lastSlashIndex);
        }
        this.$LocalRoot = $rootDir.concat("/");

        this.$Host = Request.getVar(Request.INPUT_SERVER, "HTTP_HOST");
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
    private void defineConstants() throws Exception {
        this.$GlobalConstants = new Hashtable();
        this.$GlobalConstants.put("[#Site_Name]", Config.SITE_NAME);
        this.$GlobalConstants.put("[#Site_Comments]", Config.SITE_COMMENTS);
        this.$GlobalConstants.put("[#Top_Dir]", Config.TOP_DIR);
        this.$GlobalConstants.put("[#Index_Page]", Config.INDEX_PAGE);
        this.$GlobalConstants.put("[#Action_Page]", Config.ACTION_PAGE);
        this.$GlobalConstants.put("[#Powered_By]", Config.POWERED_BY);
        this.$GlobalConstants.put("[#Github_Repo]", Config.GITHUB_REPO);
        //if (this.$IsMobile)
        //    this.$GlobalConstants.put("[#Is_Mobile]", "1");
        this.$GlobalConstants.put("[#Lang]", this.$Lang);

        java.lang.reflect.Field fieldInfo = Config.class.getField("NAME_CATEGORY");
        if (fieldInfo != null) set("Name_Category", fieldInfo.get(null));
        fieldInfo = Config.class.getField("NAME_CATEGORIES");
        if (fieldInfo != null) set("Name_Categories", fieldInfo.get(null));
        fieldInfo = Config.class.getField("NAME_CREATOR");
        if (fieldInfo != null) set("Name_Creator", fieldInfo.get(null));
        fieldInfo = Config.class.getField("NAME_CUSTOM1");
        if (fieldInfo != null) set("Name_Custom1", fieldInfo.get(null));
        fieldInfo = Config.class.getField("NAME_CUSTOM2");
        if (fieldInfo != null) set("Name_Custom2", fieldInfo.get(null));

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

    private ArrayList $EngineInstances = null;
    private int $EngineIndex = -1;

    /**
     * Push engine.
     * @param $printFlag Whether to print content immediately (true) or save it for further processing (false).
     */
    public Engine pushEngine(Boolean $printFlag) {
        Engine $engine = new Engine(this);
        $engine.setPrintFlag($printFlag);
        this.$EngineIndex++;
        if (this.$EngineInstances == null)
            this.$EngineInstances = new ArrayList();
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

    /** Get current engine */
    public Engine getEngine() {
        return (Engine)this.$EngineInstances.get(this.$EngineIndex);
    }
}
