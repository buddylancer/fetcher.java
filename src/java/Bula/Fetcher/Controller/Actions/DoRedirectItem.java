// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Actions;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.THashtable;
import Bula.Objects.TRequest;

import Bula.Model.DataSet;
import Bula.Fetcher.Model.DOItem;

/**
 * Redirecting to the external item.
 */
public class DoRedirectItem extends DoRedirect {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public DoRedirectItem(Context $context) { super($context); }

    /** Execute main logic for DoRedirectItem action */
    public void execute() {
        String $errorMessage = null;
        String $linkToRedirect = null;
        if (!this.$context.$Request.contains("id"))
            $errorMessage = "Item ID is required!";
        else {
            String $id = this.$context.$Request.get("id");
            if (!TRequest.isInteger($id) || INT($id) <= 0)
                $errorMessage = "Incorrect item ID!";
            else {
                DOItem $doItem = new DOItem(this.$context.$Connection);
                DataSet $dsItems = $doItem.getById(INT($id));
                if ($dsItems.getSize() == 0)
                    $errorMessage = "No item with such ID!";
                else {
                    THashtable $oItem = $dsItems.getRow(0);
                    $linkToRedirect = STR($oItem.get("s_Link"));
                }
            }
        }
        this.executeRedirect($linkToRedirect, $errorMessage);
    }
}
