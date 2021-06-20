// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.TRequest;
import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;
import Bula.Model.DataSet;
import Bula.Fetcher.Model.DOSource;
import Bula.Fetcher.Controller.Engine;
import Bula.Fetcher.Controller.Page;

/**
 * Controller for Filter Items block.
 */
public class FilterItems extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public FilterItems(Context $context) { super($context); }

    /** Execute main logic for FilterItems block. */
    public void execute() {
        DOSource $doSource = new DOSource();

        String $source = null;
        if (this.$context.$Request.contains("source"))
            $source = this.$context.$Request.get("source");

        THashtable $prepare = new THashtable();
        if (this.$context.$FineUrls)
            $prepare.put("[#Fine_Urls]", 1);
        $prepare.put("[#Selected]", BLANK($source) ? " selected=\"selected\" " : "");
        DataSet $dsSources = null;
        //TODO -- This can be too long on big databases... Switch off counters for now.
        Boolean $useCounters = true;
        if ($useCounters)
            $dsSources = $doSource.enumSourcesWithCounters();
        else
            $dsSources = $doSource.enumSources();
        TArrayList $options = new TArrayList();
        for (int $n = 0; $n < $dsSources.getSize(); $n++) {
            THashtable $oSource = $dsSources.getRow($n);
            THashtable $option = new THashtable();
            $option.put("[#Selected]", ($oSource.get("s_SourceName").equals($source) ? "selected=\"selected\"" : " "));
            $option.put("[#Id]", STR($oSource.get("s_SourceName")));
            $option.put("[#Name]", STR($oSource.get("s_SourceName")));
            if ($useCounters)
                $option.put("[#Counter]", $oSource.get("cntpro"));
            $options.add($option);
        }
        $prepare.put("[#Options]", $options);
        this.write("Pages/filter_items", $prepare);
    }
}
