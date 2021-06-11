
package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import java.util.ArrayList;
import java.util.Hashtable;
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
        Hashtable $prepare = new Hashtable();

        DOCategory $doCategory = new DOCategory();
        DataSet $dsCategory = $doCategory.enumAll("_this.i_Counter <> 0");
        int $size = $dsCategory.getSize();
        int $size3 = $size % 3;
        int $n1 = INT($size / 3) + ($size3 == 0 ? 0 : 1);
        int $n2 = $n1 * 2;
        Object[] $nn = ARR(0, $n1, $n2, $size);
        ArrayList $filterBlocks = new ArrayList();
        for (int $td = 0; $td < 3; $td++) {
            Hashtable $filterBlock = new Hashtable();
            ArrayList $rows = new ArrayList();
            for (int $n = INT($nn[$td]); $n < INT($nn[$td+1]); $n++) {
                Hashtable $oCategory = $dsCategory.getRow($n);
                int $counter = INT($oCategory.get("i_Counter"));
                if (INT($counter) == 0)
                    continue;
                String $key = STR($oCategory.get("s_CatId"));
                String $name = STR($oCategory.get("s_Name"));
                Hashtable $row = new Hashtable();
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
            ArrayList $rssBlocks = new ArrayList();
            for (int $td = 0; $td < 3; $td++) {
                Hashtable $rssBlock = new Hashtable();
                ArrayList $rows = new ArrayList();
                for (int $n = INT($nn[$td]); $n < INT($nn[$td+1]); $n++) {
                    Hashtable $oCategory = $dsCategory.getRow($n);
                    String $key = STR($oCategory.get("s_CatId"));
                    String $name = STR($oCategory.get("s_Name"));
                    //$counter = INT($oCategory.get("i_Counter"));
                    Hashtable $row = new Hashtable();
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
