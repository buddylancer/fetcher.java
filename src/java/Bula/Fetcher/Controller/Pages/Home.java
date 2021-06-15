// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import java.util.ArrayList;
import java.util.Hashtable;
import Bula.Model.DataSet;
import Bula.Fetcher.Model.DOItem;
import Bula.Fetcher.Controller.Engine;

/**
 * Controller for Home block.
 */
public class Home extends ItemsBase {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Home(Context $context) { super($context); }

    /**
     * Fast check of input query parameters.
     * @return Hashtable Parsed parameters (or null in case of any error).
     */
    public Hashtable check() {
        return new Hashtable();
    }

    /** Execute main logic for Home block. */
    public void execute() {
        Hashtable $pars = this.check();
        if ($pars == null)
            return;

        Hashtable $prepare = new Hashtable();

        DOItem $doItem = new DOItem();

        $prepare.put("[#BrowseItemsLink]", this.getLink(Config.INDEX_PAGE, "?p=", null, "items"));
        if (Config.SHOW_IMAGES)
            $prepare.put("[#Show_Images]", 1);

        String $source = null;
        String $search = null;
        int $maxRows = Config.DB_HOME_ROWS;
        DataSet $dsItems = $doItem.enumItems($source, $search, 1, $maxRows);
        int $rowCount = 1;
        ArrayList $items = new ArrayList();
        for (int $n = 0; $n < $dsItems.getSize(); $n++) {
            Hashtable $oItem = $dsItems.getRow($n);
            Hashtable $row = fillItemRow($oItem, $doItem.getIdField(), $rowCount);
            $items.add($row);
            $rowCount++;
        }
        $prepare.put("[#Items]", $items);

        this.write("Pages/home", $prepare);
    }
}
