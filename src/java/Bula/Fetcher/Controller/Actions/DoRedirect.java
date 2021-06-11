
package Bula.Fetcher.Controller.Actions;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.Response;
import java.util.Hashtable;

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
        Hashtable $prepare = new Hashtable();
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
        Response.write($engine.showTemplate($templateName, $prepare));
    }
}
