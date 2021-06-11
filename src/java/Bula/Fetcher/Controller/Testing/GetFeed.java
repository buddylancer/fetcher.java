
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
        Request.initialize();
        Request.extractAllVars();

        // Check source
        if (!Request.contains("source")) {
            Response.end("Source is required!");
            return;
        }
        String $source = Request.get("source");
        if (BLANK($source)) {
            Response.end("Empty source!");
            return;
        }

        Response.writeHeader("Content-type", "text/xml; charset=UTF-8");
        Response.write(Helper.readAllText(CAT(this.$context.$LocalRoot, "local/tests/input/U.S. News - ", $source, ".xml")));
        Response.end("");
    }
}
