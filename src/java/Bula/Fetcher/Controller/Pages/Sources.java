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
import Bula.Fetcher.Model.DOSource;
import Bula.Fetcher.Model.DOItem;
import Bula.Fetcher.Controller.Engine;

/**
 * Controller for Sources block.
 */
public class Sources extends ItemsBase {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Sources(Context $context) { super($context); }

    /**
     * Fast check of input query parameters.
     * @return THashtable Parsed parameters (or null in case of any error).
     */
    public THashtable check() {
        return new THashtable();
    }

    /** Execute main logic for Source block. */
    public void execute() {
        THashtable $prepare = new THashtable();
        if (Config.SHOW_IMAGES)
            $prepare.put("[#Show_Images]", 1);

        DOSource $doSource = new DOSource(this.$context.$Connection);
        DOItem $doItem = new DOItem(this.$context.$Connection);

        DataSet $dsSources = $doSource.enumSources();
        int $count = 1;
        TArrayList $sources = new TArrayList();
        for (int $ns = 0; $ns < $dsSources.getSize(); $ns++) {
            THashtable $oSource = $dsSources.getRow($ns);
            String $sourceName = STR($oSource.get("s_SourceName"));

            THashtable $sourceRow = new THashtable();
            $sourceRow.put("[#ColSpan]", Config.SHOW_IMAGES ? 4 : 3);
            $sourceRow.put("[#SourceName]", $sourceName);
            $sourceRow.put("[#ExtImages]", Config.EXT_IMAGES);
            //$sourceRow["[#RedirectSource]"] = Config.TOP_DIR .
            //    (Config.FINE_URLS ? "redirect/source/" : "action.php?p=do_redirect_source&source=") .
            //        $oSource["s_SourceName"];
            $sourceRow.put("[#RedirectSource]", this.getLink(Config.INDEX_PAGE, "?p=items&source=", "items/source/", $sourceName));

            DataSet $dsItems = $doItem.enumItemsFromSource(null, $sourceName, null, 3);
            TArrayList $items = new TArrayList();
            int $itemCount = 0;
            for (int $ni = 0; $ni < $dsItems.getSize(); $ni++) {
                THashtable $oItem = $dsItems.getRow($ni);
                THashtable $item = fillItemRow($oItem, $doItem.getIdField(), $itemCount);
                if (Config.SHOW_IMAGES)
                    $item.put("[#Show_Images]", 1);
                $items.add($item);
                $itemCount++;
            }
            $sourceRow.put("[#Items]", $items);

            $sources.add($sourceRow);
            $count++;
        }
        $prepare.put("[#Sources]", $sources);

        this.write("Pages/sources", $prepare);
    }
}
