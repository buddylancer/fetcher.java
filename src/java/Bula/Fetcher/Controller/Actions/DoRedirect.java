// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Actions;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.TResponse;
import Bula.Objects.THashtable;

import Bula.Fetcher.Controller.Page;
import Bula.Fetcher.Controller.Engine;

/**
 * Base class for redirecting from the web-site.
 */
abstract class DoRedirect extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public DoRedirect(Context $context) { super($context); }

    /**
     * Execute main logic for this action.
     * @param $linkToRedirect Link to redirect (or null if there were some errors).
     * @param $errorMessage Error to show (or null if no errors).
     */
    public void executeRedirect(String $linkToRedirect, String $errorMessage) {
        THashtable $prepare = new THashtable();
        String $templateName = null;
        if (!NUL($errorMessage)) {
            $prepare.put("[#Title]", "Error");
            $prepare.put("[#ErrMessage]", $errorMessage);
            $templateName = "error_alone";
        }
        else if (!BLANK($linkToRedirect)) {
            $prepare.put("[#Link]", $linkToRedirect);
            $templateName = "redirect";
        }

        Engine $engine = this.$context.pushEngine(true);
        this.$context.$Response.write($engine.showTemplate($templateName, $prepare));
    }
}
