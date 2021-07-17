// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;
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
     * @return THashtable Parsed parameters (or null in case of any error).
     */
    public THashtable check() {
        return new THashtable();
    }

    /** Execute main logic for Home block. */
    public void execute() {
        THashtable $pars = this.check();
        if ($pars == null)
            return;

        THashtable $prepare = new THashtable();

        DOItem $doItem = new DOItem(this.$context.$Connection);

        $prepare.put("[#BrowseItemsLink]", this.getLink(Config.INDEX_PAGE, "?p=", null, "items"));
        if (Config.SHOW_IMAGES)
            $prepare.put("[#Show_Images]", 1);

        String $source = null;
        String $search = null;
        int $maxRows = Config.DB_HOME_ROWS;
        DataSet $dsItems = $doItem.enumItems($source, $search, 1, $maxRows);
        int $rowCount = 1;
        TArrayList $items = new TArrayList();
        for (int $n = 0; $n < $dsItems.getSize(); $n++) {
            THashtable $oItem = $dsItems.getRow($n);
            THashtable $row = fillItemRow($oItem, $doItem.getIdField(), $rowCount);
            $items.add($row);
            $rowCount++;
        }
        $prepare.put("[#Items]", $items);

        this.write("Pages/home", $prepare);
    }
}
