
package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Internal;
import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.Request;
import Bula.Objects.Response;
import java.util.ArrayList;
import java.util.Hashtable;
import Bula.Model.DBConfig;

/**
 * Logic for executing actions.
 */
public class Action extends Page {
    private static Object[] $actionsArray = null;

    private static void initialize() {
        $actionsArray = ARR(
        //action name            page                   post      code
        "do_redirect_item",     "DoRedirectItem",       0,        0,
        "do_redirect_source",   "DoRedirectSource",     0,        0,
        "do_clean_cache",       "DoCleanCache",         0,        1,
        "do_test_items",        "DoTestItems",          0,        1
        );
    }

    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Action(Context $context) { super($context); }

    /** Execute main logic for required action. */
    public void execute() {
        if ($actionsArray == null)
            initialize();

        Hashtable $actionInfo = Request.testPage($actionsArray);

        // Test action name
        if (!$actionInfo.containsKey("page")) {
            Response.end("Error in parameters -- no page");
            return;
        }

        // Test action context
        if (INT($actionInfo.get("post_required")) == 1 && INT($actionInfo.get("from_post")) == 0) {
            Response.end("Error in parameters -- inconsistent pars");
            return;
        }

        Request.initialize();
        if (INT($actionInfo.get("post_required")) == 1)
            Request.extractPostVars();
        else
            Request.extractAllVars();

        //TODO!!!
        //if (!Request.CheckReferer(Config.$Site))
        //    err404();

        if (INT($actionInfo.get("code_required")) == 1) {
            if (!Request.contains("code") || !EQ(Request.get("code"), Config.SECURITY_CODE)) { //TODO -- hardcoded!!!
                Response.end("No access.");
                return;
            }
        }

        String $actionClass = CAT("Bula/Fetcher/Controller/Actions/", $actionInfo.get("class"));
        ArrayList $args0 = new ArrayList(); $args0.add(this.$context);
        Internal.callMethod($actionClass, $args0, "execute", null);

        if (DBConfig.$Connection != null) {
            DBConfig.$Connection.close();
            DBConfig.$Connection = null;
        }
    }
}
