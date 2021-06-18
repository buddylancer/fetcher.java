// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.Request;
import Bula.Objects.DataList;
import Bula.Objects.DataRange;
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

        DataRange $prepare = new DataRange();
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
        DataList $options = new DataList();
        for (int $n = 0; $n < $dsSources.getSize(); $n++) {
            DataRange $oSource = $dsSources.getRow($n);
            DataRange $option = new DataRange();
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
