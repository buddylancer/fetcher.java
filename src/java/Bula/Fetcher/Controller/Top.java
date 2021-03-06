// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.DateTimes;
import Bula.Objects.THashtable;

/**
 * Logic for generating Top block.
 */
public class Top extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Top(Context $context) { super($context); }

    /** Execute main logic for Top block */
    public void execute() {
        THashtable $prepare = new THashtable();
        $prepare.put("[#ImgWidth]", this.$context.$IsMobile ? 234 : 468);
        $prepare.put("[#ImgHeight]", this.$context.$IsMobile ? 30 : 60);
        if (this.$context.$TestRun)
            $prepare.put("[#Date]", "28-Jun-2020 16:49 GMT");
        else
            $prepare.put("[#Date]", Util.showTime());

        this.write("top", $prepare);
    }
}
