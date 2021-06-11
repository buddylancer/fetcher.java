
package Bula.Fetcher.Controller.Actions;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.Request;
import java.util.Hashtable;

import Bula.Fetcher.Model.DOSource;

/**
 * Redirection to external source.
 */
public class DoRedirectSource extends DoRedirect {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public DoRedirectSource(Context $context) { super($context); }

    /** Execute main logic for DoRedirectSource action */
    public void execute() {
        String $errorMessage = null;
        String $linkToRedirect = null;
        if (!Request.contains("source"))
            $errorMessage = "Source name is required!";
        else {
            String $sourceName = Request.get("source");
            if (!Request.isDomainName($sourceName))
                $errorMessage = "Incorrect source name!";
            else {
                DOSource $doSource = new DOSource();
                Hashtable[] $oSource =
                    {new Hashtable()};
                if (!$doSource.checkSourceName($sourceName, $oSource))
                    $errorMessage = "No such source name!";
                else
                    $linkToRedirect = STR($oSource[0].get("s_External"));
            }
        }
        this.executeRedirect($linkToRedirect, $errorMessage);
    }
}
