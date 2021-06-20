// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Internal;
import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.TRequest;
import Bula.Objects.TResponse;
import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;
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

        THashtable $actionInfo = this.$context.$Request.testPage($actionsArray);

        // Test action name
        if (!$actionInfo.containsKey("page")) {
            this.$context.$Response.end("Error in parameters -- no page");
            return;
        }

        // Test action context
        if (INT($actionInfo.get("post_required")) == 1 && INT($actionInfo.get("from_post")) == 0) {
            this.$context.$Response.end("Error in parameters -- inconsistent pars");
            return;
        }

        //this.$context.$Request.initialize();
        if (INT($actionInfo.get("post_required")) == 1)
            this.$context.$Request.extractPostVars();
        else
            this.$context.$Request.extractAllVars();

        //TODO!!!
        //if (!this.$context.$Request.CheckReferer(Config.$Site))
        //    err404();

        if (INT($actionInfo.get("code_required")) == 1) {
            if (!this.$context.$Request.contains("code") || !EQ(this.$context.$Request.get("code"), Config.SECURITY_CODE)) { //TODO -- hardcoded!!!
                this.$context.$Response.end("No access.");
                return;
            }
        }

        String $actionClass = CAT("Bula/Fetcher/Controller/Actions/", $actionInfo.get("class"));
        TArrayList $args0 = new TArrayList(); $args0.add(this.$context);
        Internal.callMethod($actionClass, $args0, "execute", null);

        if (DBConfig.$Connection != null) {
            DBConfig.$Connection.close();
            DBConfig.$Connection = null;
        }
    }
}
