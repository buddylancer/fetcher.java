// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Testing;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Fetcher.Controller.Page;

import Bula.Objects.Helper;
import Bula.Objects.Request;
import Bula.Objects.Response;

/**
 * Logic for getting test feed.
 */
public class GetFeed extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public GetFeed(Context $context) { super($context); }

    /** Get test feed using parameters from request. */
    public void execute() {
        //this.$context.$Request.initialize();
        this.$context.$Request.extractAllVars();

        // Check source
        if (!this.$context.$Request.contains("source")) {
            this.$context.$Response.end("Source is required!");
            return;
        }
        String $source = this.$context.$Request.get("source");
        if (BLANK($source)) {
            this.$context.$Response.end("Empty source!");
            return;
        }

        this.$context.$Response.writeHeader("Content-type", "text/xml; charset=UTF-8");
        this.$context.$Response.write(Helper.readAllText(CAT(this.$context.$LocalRoot, "local/tests/input/U.S. News - ", $source, ".xml"), "UTF-8"));
        this.$context.$Response.end();
    }
}
