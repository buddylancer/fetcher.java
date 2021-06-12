
package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.Request;
import java.util.ArrayList;
import java.util.Hashtable;
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
        if (Request.contains("source"))
            $source = Request.get("source");

        Hashtable $prepare = new Hashtable();
        if (this.$context.$FineUrls)
            $prepare.put("[#Fine_Urls]", 1);
        $prepare.put("[#Selected]", BLANK($source) ? " selected=\"selected\" " : " ");
        DataSet $dsSources = null;
        //TODO -- This can be too long on big databases... Switch off counters for now.
        Boolean $useCounters = true;
        if ($useCounters)
            $dsSources = $doSource.enumSourcesWithCounters();
        else
            $dsSources = $doSource.enumSources();
        ArrayList $options = new ArrayList();
        for (int $n = 0; $n < $dsSources.getSize(); $n++) {
            Hashtable $oSource = $dsSources.getRow($n);
            Hashtable $option = new Hashtable();
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
