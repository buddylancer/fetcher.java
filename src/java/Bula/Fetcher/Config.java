// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher;
import Bula.Meta;

/**
 * Main class for configuring data.
 */
public class Config extends Meta {
    /** Platform */
    public static final String PLATFORM = "Java";
    /** Exactly the same as RewriteBase in .htaccess */
    public static final String TOP_DIR = "/";
    /** Index page name */
    public static final String INDEX_PAGE = "";
    /** Action page name */
    public static final String ACTION_PAGE = "action[#File_Ext]";
    /** RSS-feeds page name */
    public static final String RSS_PAGE = "rss[#File_Ext]";
    /** Current API output format (can be "Json" or "Xml" for now) */
    public static final String API_FORMAT = "Json";
    /** Current API output content type (can be "application/json" or "text/xml" for now) */
    public static final String API_CONTENT = "application/json";
    /** File prefix for constructing real path */
    public static final String FILE_PREFIX = "src/java/";

    /** Security code */
    public static final String SECURITY_CODE = "1234";

    /** Use fine or full URLs */
    public static final Boolean FINE_URLS = false;

    /** Cache Web-pages */
    public static final Boolean CACHE_PAGES = false;
    /** Cache RSS-feeds */
    public static final Boolean CACHE_RSS = false;
    /** Show what source an item is originally from */
    public static final Boolean SHOW_FROM = false;
    /** Whether to show images for sources */
    public static final Boolean SHOW_IMAGES = true;
    /** File extension for images */
    public static final String EXT_IMAGES = "gif";
    /** Show an item or immediately redirect to external source item */
    public static final Boolean IMMEDIATE_REDIRECT = false;
    /** How much items to show on "Sources" page */
    public static final int LATEST_ITEMS = 3;
    /** Minimum number of items in RSS-feeds */
    public static final int MIN_RSS_ITEMS = 5;
    /** Maximum number of items in RSS-feeds */
    public static final int MAX_RSS_ITEMS = 50;

    /** Default number of rows on page */
    public static final int DB_ROWS = 20;
    /** Default number of rows on "Home" page */
    public static final int DB_HOME_ROWS = 15;
    /** Default number of rows on "Items" page */
    public static final int DB_ITEMS_ROWS = 25;

    // Fill these fields by your site data
    /** Site language (default - null) */
    public static final String SITE_LANGUAGE = null;
    /** Site name */
    public static final String SITE_NAME = "Buddy Fetcher";
    /** Site comments */
    public static final String SITE_COMMENTS = "Latest News Headlines";
    /** Site keywords */
    public static final String SITE_KEYWORDS = "Buddy Fetcher, rss, fetcher, aggregator, [#Platform], MySQL";
    /** Site description */
    public static final String SITE_DESCRIPTION = "Buddy Fetcher is a simple RSS fetcher/aggregator written in [#Platform]/MySQL";

    /** Name of item (in singular form) */
    public static final String NAME_ITEM = "Headline";
    /** Name of items (in plural form) */
    public static final String NAME_ITEMS = "Headlines";
    // Uncomment what fields should be extracted and name them appropriately
    /** Name of category (in singular form) */
    public static final String NAME_CATEGORY = "Region";
    /** Name of categories (in plural form) */
    public static final String NAME_CATEGORIES = "Regions";
    /** Name of creator */
    public static final String NAME_CREATOR = "Creator";
    /** Name of custom field 1 (comment when not extracted) */
    //const String NAME_CUSTOM1 = "Custom1";
    /** Name of custom field 2 (comment when not extracted) */
    //const String NAME_CUSTOM2 = "Custom2";

    /** Show bottom blocks (Filtering and RSS) */
    public static final Boolean SHOW_BOTTOM = true;
    /** Show empty categories */
    public static final Boolean SHOW_EMPTY = false;
    /** Sort categories by Id (s_CatId) or Name (s_Name) or NULL for default (as-is) */
    public static final String SORT_CATEGORIES = null;

    /** Site time shift with respect to GMT (hours*100+minutes) */
    public static final int TIME_SHIFT = 0;
    /** Site time zone name (GMT or any other) */
    public static final String TIME_ZONE = "GMT";

    /** Powered By string */
    public static final String POWERED_BY = "Buddy Fetcher for [#Platform]";
    /** GitHub repository */
    public static final String GITHUB_REPO = "buddylancer/fetcher.[#Platform]";
}
