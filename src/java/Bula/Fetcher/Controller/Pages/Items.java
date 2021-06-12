
package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import java.util.ArrayList;
import java.util.Hashtable;

import Bula.Objects.Request;

import Bula.Model.DataSet;

import Bula.Fetcher.Controller.Engine;

import Bula.Fetcher.Model.DOItem;
import Bula.Fetcher.Model.DOSource;
import Bula.Fetcher.Model.DOCategory;

/**
 * Controller for Items block.
 */
public class Items extends ItemsBase {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Items(Context $context) { super($context); }

    /**
     * Fast check of input query parameters.
     * @return Hashtable Parsed parameters (or null in case of any error).
     */
    public Hashtable check() {
        String $errorMessage = new String();

        String $list = Request.get("list");
        if (!NUL($list)) {
            if (BLANK($list))
                $errorMessage += "Empty list number!";
            else if (!Request.isInteger($list))
                $errorMessage += "Incorrect list number!";
        }

        String $sourceName = Request.get("source");
        if (!NUL($sourceName)) {
            if (BLANK($sourceName)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Empty source name!";
            }
            else if (!Request.isDomainName($sourceName)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Incorrect source name!";
            }
        }

        String $filterName = Request.get("filter");
        if (!NUL($filterName)) {
            if (BLANK($filterName)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Empty filter name!";
            }
            else if (!Request.isName($filterName)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Incorrect filter name!";
            }
        }

        if ($errorMessage.length() > 0) {
            Hashtable $prepare = new Hashtable();
            $prepare.put("[#ErrMessage]", $errorMessage);
            this.write("error", $prepare);
            return null;
        }

        Hashtable $pars = new Hashtable();
        if (!NUL($list))
            $pars.put("list", $list);
        if (!NUL($sourceName))
            $pars.put("source_name", $sourceName);
        if (!NUL($filterName))
            $pars.put("filter_name", $filterName);
        return $pars;
    }

    /** Execute main logic for Items block. */
    public void execute() {
        Hashtable $pars = this.check();
        if ($pars == null)
            return;

        String $list = (String)$pars.get("list");
        int $listNumber = $list == null ? 1 : INT($list);
        String $sourceName = (String)$pars.get("source_name");
        String $filterName = (String)$pars.get("filter_name");

        String $errorMessage = new String();
        String $filter = null;

        if (!NUL($filterName)) {
            DOCategory $doCategory = new DOCategory();
            Hashtable[] $oCategory =
                {new Hashtable()};
            if (!$doCategory.checkFilterName($filterName, $oCategory))
                $errorMessage += "Non-existing filter name!";
            else
                $filter = STR($oCategory[0].get("s_Filter"));
        }

        if (!NUL($sourceName)) {
            DOSource $doSource = new DOSource();
            Hashtable[] $oSource =
                {new Hashtable()};
            if (!$doSource.checkSourceName($sourceName, $oSource)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Non-existing source name!";
            }
        }

        Engine $engine = this.$context.getEngine();

        Hashtable $prepare = new Hashtable();
        if ($errorMessage.length() > 0) {
            $prepare.put("[#ErrMessage]", $errorMessage);
            this.write("error", $prepare);
            return;
        }

        // Uncomment to enable filtering by source and/or category
        $prepare.put("[#FilterItems]", $engine.includeTemplate("Pages/FilterItems"));

        String $s_Title = CAT(
            "Browse ",
            Config.NAME_ITEMS,
            (this.$context.$IsMobile ? "<br/>" : null),
            (!BLANK($sourceName) ? CAT(" ... from '", $sourceName, "'") : null),
            (!BLANK($filter) ? CAT(" ... for '", $filterName, "'") : null)
        );

        $prepare.put("[#Title]", $s_Title);

        int $maxRows = Config.DB_ITEMS_ROWS;

        DOItem $doItem = new DOItem();
        DataSet $dsItems = $doItem.enumItems($sourceName, $filter, $listNumber, $maxRows);

        int $listTotal = $dsItems.getTotalPages();
        if ($listNumber > $listTotal) {
            $prepare.put("[#ErrMessage]", "List number is too large!");
            this.write("error", $prepare);
            return;
        }
        if ($listTotal > 1) {
            $prepare.put("[#List_Total]", $listTotal);
            $prepare.put("[#List]", $listNumber);
        }

        int $count = 1;
        ArrayList $rows = new ArrayList();
        for (int $n = 0; $n < $dsItems.getSize(); $n++) {
            Hashtable $oItem = $dsItems.getRow($n);
            Hashtable $row = fillItemRow($oItem, $doItem.getIdField(), $count);
            $count++;
            $rows.add($row);
        }
        $prepare.put("[#Rows]", $rows);

        if ($listTotal > 1) {
            int $chunk = 2;
            Boolean $before = false;
            Boolean $after = false;

            ArrayList $pages = new ArrayList();
            for (int $n = 1; $n <= $listTotal; $n++) {
                Hashtable $page = new Hashtable();
                if ($n < $listNumber - $chunk) {
                    if (!$before) {
                        $before = true;
                        $page.put("[#Text]", "1");
                        $page.put("[#Link]", getPageLink(1));
                        $pages.add($page);
                        $page = new Hashtable();
                        $page.put("[#Text]", " ... ");
                        //$row.remove("[#Link]");
                        $pages.add($page);
                    }
                    continue;
                }
                if ($n > $listNumber + $chunk) {
                    if (!$after) {
                        $after = true;
                        $page.put("[#Text]", " ... ");
                        $pages.add($page);
                        $page = new Hashtable();
                        $page.put("[#Text]", $listTotal);
                        $page.put("[#Link]", getPageLink($listTotal));
                        $pages.add($page);
                    }
                    continue;
                }
                if ($listNumber == $n) {
                    $page.put("[#Text]", CAT("=", $n, "="));
                    $pages.add($page);
                }
                else {
                    if ($n == 1) {
                        $page.put("[#Link]", getPageLink(1));
                        $page.put("[#Text]", 1);
                    }
                    else  {
                        $page.put("[#Link]", getPageLink($n));
                        $page.put("[#Text]", $n);
                    }
                    $pages.add($page);
                }
            }
            $prepare.put("[#Pages]", $pages);
        }

        this.write("Pages/items", $prepare);
    }
}
