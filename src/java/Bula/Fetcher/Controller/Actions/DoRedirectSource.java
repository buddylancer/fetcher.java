// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Actions;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.TRequest;
import Bula.Objects.THashtable;

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
        if (!this.$context.$Request.contains("source"))
            $errorMessage = "Source name is required!";
        else {
            String $sourceName = this.$context.$Request.get("source");
            if (!TRequest.isDomainName($sourceName))
                $errorMessage = "Incorrect source name!";
            else {
                DOSource $doSource = new DOSource(this.$context.$Connection);
                THashtable[] $oSource =
                    {new THashtable()};
                if (!$doSource.checkSourceName($sourceName, $oSource))
                    $errorMessage = "No such source name!";
                else
                    $linkToRedirect = STR($oSource[0].get("s_External"));
            }
        }
        this.executeRedirect($linkToRedirect, $errorMessage);
    }
}
