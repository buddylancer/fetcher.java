
package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import java.util.ArrayList;
import java.util.Hashtable;
import Bula.Objects.Regex;
import Bula.Objects.RegexOptions;
import Bula.Objects.Request;
import Bula.Objects.Response;
import Bula.Model.DBConfig;
import Bula.Model.DataAccess;
import Bula.Fetcher.Controller.Util;
import Bula.Fetcher.Controller.Engine;

/**
 * Controller for main Index page.
 */
public class Index extends Page {
    private static Object[] $pagesArray = null;

    private static void initialize() {
        $pagesArray = ARR(
            // page name,   class,          post,   code
            "home",         "Home",         0,      0,
            "items",        "Items",        0,      0,
            "view_item",    "ViewItem",     0,      0,
            "sources",      "Sources",      0,      0
        );
    }

    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Index(Context $context) { super($context); }

    /** Execute main logic for Index block */
    public void execute() {
        if ($pagesArray == null)
            initialize();

        Hashtable $pageInfo = Request.testPage($pagesArray, "home");

        // Test action name
        if (!$pageInfo.containsKey("page")) {
            Response.end("Error in parameters -- no page");
            return;
        }

        String $pageName = (String)$pageInfo.get("page");
        String $className = (String)$pageInfo.get("class");

        Request.initialize();
        if (INT($pageInfo.get("post_required")) == 1)
            Request.extractPostVars();
        else
            Request.extractAllVars();
        //echo "In Index -- " + print_r(this, true);
        this.$context.set("Page", $pageName);

        String $apiName = (String)$pageInfo.get("api");
        this.$context.$Api = BLANK($apiName) ? "" : $apiName; // Blank (html) or "rest" for now

        Engine $engine = this.$context.pushEngine(true);

        Hashtable $prepare = new Hashtable();
        $prepare.put("[#Site_Name]", Config.SITE_NAME);
        String $pFromVars = Request.contains("p") ? Request.get("p") : "home";
        String $idFromVars = Request.contains("id") ? Request.get("id") : null;
        String $title = Config.SITE_NAME;
        if ($pFromVars != "home")
            $title = CAT($title, " + ", $pFromVars, (!NUL($idFromVars) ? CAT(" + ", $idFromVars) : null));

        $prepare.put("[#Title]", $title); //TODO -- need unique title on each page
        $prepare.put("[#Keywords]", Config.SITE_KEYWORDS);
        $prepare.put("[#Description]", Config.SITE_DESCRIPTION);
        $prepare.put("[#Styles]", CAT(
                (this.$context.$TestRun ? null : Config.TOP_DIR),
                this.$context.$IsMobile ? "styles2" : "styles"));
        $prepare.put("[#ContentType]", "text/html; charset=UTF-8");
        $prepare.put("[#Top]", $engine.includeTemplate("Top"));
        $prepare.put("[#Menu]", $engine.includeTemplate("Menu"));

        // Get included page either from cache or build it from the scratch
        String $errorContent = $engine.includeTemplate(CAT("Pages/", $className), "check");
        if (!BLANK($errorContent)) {
            $prepare.put("[#Page]", $errorContent);
        }
        else {
            if (Config.CACHE_PAGES/* && !Config.$DontCache.contains($pageName)*/) //TODO!!!
                $prepare.put("[#Page]", Util.showFromCache($engine, this.$context.$CacheFolder, $pageName, $className));
            else
                $prepare.put("[#Page]", $engine.includeTemplate(CAT("Pages/", $className)));
        }

        if (/*Config.$RssAllowed != null && */Config.SHOW_BOTTOM) {
            // Get bottom block either from cache or build it from the scratch
            if (Config.CACHE_PAGES)
                $prepare.put("[#Bottom]", Util.showFromCache($engine, this.$context.$CacheFolder, "bottom", "Bottom"));
            else
                $prepare.put("[#Bottom]", $engine.includeTemplate("Bottom"));
        }

        Response.writeHeader("Content-type", CAT(
            (BLANK($apiName) ? "text/html" : Config.API_CONTENT), "; charset=UTF-8")
        );
        this.write("index", $prepare);

        // Fix <title>
        //TODO -- comment for now
        //$newTitle = Util.extractInfo($content, "<input type=\"hidden\" name=\"s_Title\" value=\"", "\" />");
        //if (!BLANK($newTitle))
        //    $content = Regex.replace($content, "<title>(.*?)</title>", CAT("<title>", Config.SITE_NAME, " -- ", $newTitle, "</title>"), RegexOptions.IgnoreCase);

        Response.write($engine.getPrintString());
        Response.end("");

        if (DBConfig.$Connection != null) {
            DBConfig.$Connection.close();
            DBConfig.$Connection = null;
        }
    }
}
