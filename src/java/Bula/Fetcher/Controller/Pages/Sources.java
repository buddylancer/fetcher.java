
package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import java.util.ArrayList;
import java.util.Hashtable;
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
     * @return Hashtable Parsed parameters (or null in case of any error).
     */
    public Hashtable check() {
        return new Hashtable();
    }

    /** Execute main logic for Source block. */
    public void execute() {
        Hashtable $prepare = new Hashtable();

        DOSource $doSource = new DOSource();
        DOItem $doItem = new DOItem();

        DataSet $dsSources = $doSource.enumSources();
        int $count = 1;
        ArrayList $sources = new ArrayList();
        for (int $ns = 0; $ns < $dsSources.getSize(); $ns++) {
            Hashtable $oSource = $dsSources.getRow($ns);
            String $sourceName = STR($oSource.get("s_SourceName"));

            Hashtable $sourceRow = new Hashtable();
            $sourceRow.put("[#SourceName]", $sourceName);
            //$sourceRow["[#RedirectSource]"] = Config.TOP_DIR .
            //    (Config.FINE_URLS ? "redirect/source/" : "action.php?p=do_redirect_source&source=") .
            //        $oSource["s_SourceName"];
            $sourceRow.put("[#RedirectSource]", this.getLink(Config.INDEX_PAGE, "?p=items&source=", "items/source/", $sourceName));

            DataSet $dsItems = $doItem.enumItemsFromSource(null, $sourceName, null, 3);
            ArrayList $items = new ArrayList();
            int $itemCount = 0;
            for (int $ni = 0; $ni < $dsItems.getSize(); $ni++) {
                Hashtable $oItem = $dsItems.getRow($ni);
                $items.add(fillItemRow($oItem, $doItem.getIdField(), $itemCount));
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
