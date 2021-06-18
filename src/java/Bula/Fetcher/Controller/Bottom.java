// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.DataList;
import Bula.Objects.DataRange;
import Bula.Model.DataSet;
import Bula.Fetcher.Model.DOCategory;

/**
 * Logic for generating Bottom block.
 */
public class Bottom extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Bottom(Context $context) { super($context); }

    /** Execute main logic for Bottom block */
    public void execute() {
        DataRange $prepare = new DataRange();

        DOCategory $doCategory = new DOCategory();
        DataSet $dsCategory = $doCategory.enumAll("_this.i_Counter <> 0");
        int $size = $dsCategory.getSize();
        int $size3 = $size % 3;
        int $n1 = INT($size / 3) + ($size3 == 0 ? 0 : 1);
        int $n2 = $n1 * 2;
        Object[] $nn = ARR(0, $n1, $n2, $size);
        DataList $filterBlocks = new DataList();
        for (int $td = 0; $td < 3; $td++) {
            DataRange $filterBlock = new DataRange();
            DataList $rows = new DataList();
            for (int $n = INT($nn[$td]); $n < INT($nn[$td+1]); $n++) {
                DataRange $oCategory = $dsCategory.getRow($n);
                if (NUL($oCategory))
                    continue;
                int $counter = INT($oCategory.get("i_Counter"));
                if (INT($counter) == 0)
                    continue;
                String $key = STR($oCategory.get("s_CatId"));
                String $name = STR($oCategory.get("s_Name"));
                DataRange $row = new DataRange();
                $row.put("[#Link]", this.getLink(Config.INDEX_PAGE, "?p=items&filter=", "items/filter/", $key));
                $row.put("[#LinkText]", $name);
                //if ($counter > 0)
                    $row.put("[#Counter]", $counter);
                $rows.add($row);
            }
            $filterBlock.put("[#Rows]", $rows);
            $filterBlocks.add($filterBlock);
        }
        $prepare.put("[#FilterBlocks]", $filterBlocks);

        if (!this.$context.$IsMobile) {
            $dsCategory = $doCategory.enumAll();
            $size = $dsCategory.getSize(); //50
            $size3 = $size % 3; //2
            $n1 = INT($size / 3) + ($size3 == 0 ? 0 : 1); //17.3
            $n2 = $n1 * 2; //34.6
            $nn = ARR(0, $n1, $n2, $size);
            DataList $rssBlocks = new DataList();
            for (int $td = 0; $td < 3; $td++) {
                DataRange $rssBlock = new DataRange();
                DataList $rows = new DataList();
                for (int $n = INT($nn[$td]); $n < INT($nn[$td+1]); $n++) {
                    DataRange $oCategory = $dsCategory.getRow($n);
                    if (NUL($oCategory))
                        continue;
                    String $key = STR($oCategory.get("s_CatId"));
                    String $name = STR($oCategory.get("s_Name"));
                    //$counter = INT($oCategory.get("i_Counter"));
                    DataRange $row = new DataRange();
                    $row.put("[#Link]", this.getLink(Config.RSS_PAGE, "?filter=", "rss/", CAT($key, (this.$context.$FineUrls ? ".xml" : null))));
                    $row.put("[#LinkText]", $name);
                    $rows.add($row);
                }
                $rssBlock.put("[#Rows]", $rows);
                $rssBlocks.add($rssBlock);
            }
            $prepare.put("[#RssBlocks]", $rssBlocks);
        }
        this.write("bottom", $prepare);
    }
}
