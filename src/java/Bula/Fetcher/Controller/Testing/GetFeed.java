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
import Bula.Objects.Strings;
import Bula.Objects.TRequest;
import Bula.Objects.TResponse;

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
        String $encoding = new String("UTF-8");
        if (this.$context.$Request.contains("encoding"))
            $encoding = this.$context.$Request.get("encoding");

        String $from = new String("tests/input");
        if (this.$context.$Request.contains("from"))
            $from = this.$context.$Request.get("from");

        this.$context.$Response.writeHeader("Content-type", CAT("text/xml; charset=", $encoding), $encoding);
        String $filename = Strings.concat(this.$context.$LocalRoot, "local/", $from, "/", $source, ".xml");
        if ($filename.indexOf("..") == -1) {
            String $content = Helper.readAllText($filename, $encoding);
            if (!BLANK($content))
                this.$context.$Response.write($content);
        }
        this.$context.$Response.end();
    }
}
