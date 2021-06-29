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

import Bula.Objects.TRequest;

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
     * @return THashtable Parsed parameters (or null in case of any error).
     */
    public THashtable check() {
        String $errorMessage = new String();

        String $list = this.$context.$Request.get("list");
        if (!NUL($list)) {
            if (BLANK($list))
                $errorMessage += "Empty list number!";
            else if (!TRequest.isInteger($list))
                $errorMessage += "Incorrect list number!";
        }

        String $sourceName = this.$context.$Request.get("source");
        if (!NUL($sourceName)) {
            if (BLANK($sourceName)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Empty source name!";
            }
            else if (!TRequest.isDomainName($sourceName)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Incorrect source name!";
            }
        }

        String $filterName = this.$context.$Request.get("filter");
        if (!NUL($filterName)) {
            if (BLANK($filterName)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Empty filter name!";
            }
            else if (!TRequest.isName($filterName)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Incorrect filter name!";
            }
        }

        if ($errorMessage.length() > 0) {
            THashtable $prepare = new THashtable();
            $prepare.put("[#ErrMessage]", $errorMessage);
            this.write("error", $prepare);
            return null;
        }

        THashtable $pars = new THashtable();
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
        THashtable $pars = this.check();
        if ($pars == null)
            return;

        String $list = (String)$pars.get("list");
        int $listNumber = $list == null ? 1 : INT($list);
        String $sourceName = (String)$pars.get("source_name");
        String $filterName = (String)$pars.get("filter_name");

        String $errorMessage = new String();
        String $filter = null;
        String $category = null;

        if (!NUL($filterName)) {
            DOCategory $doCategory = new DOCategory();
            THashtable[] $oCategory =
                {new THashtable()};
            if (!$doCategory.checkFilterName($filterName, $oCategory))
                $errorMessage += "Non-existing filter name!";
            else  {
                $category = STR($oCategory[0].get("s_Name"));
                $filter = STR($oCategory[0].get("s_Filter"));
            }
        }

        int $sourceId = -1;
        if (!NUL($sourceName)) {
            DOSource $doSource = new DOSource();
            THashtable[] $oSource =
                {new THashtable()};
            if (!$doSource.checkSourceName($sourceName, $oSource)) {
                if ($errorMessage.length() > 0)
                    $errorMessage += "<br/>";
                $errorMessage += "Non-existing source name!";
            }
            else
                $sourceId = INT($oSource[0].get("i_SourceId"));
        }

        Engine $engine = this.$context.getEngine();

        THashtable $prepare = new THashtable();
        if ($errorMessage.length() > 0) {
            $prepare.put("[#ErrMessage]", $errorMessage);
            this.write("error", $prepare);
            return;
        }

        if (Config.SHOW_IMAGES)
            $prepare.put("[#Show_Images]", 1);
        $prepare.put("[#ColSpan]", Config.SHOW_IMAGES ? 4 : 3);

        // Uncomment to enable filtering by source and/or category
        $prepare.put("[#FilterItems]", $engine.includeTemplate("Pages/FilterItems"));

        String $s_Title = CAT(
            "Browse ",
            Config.NAME_ITEMS,
            (this.$context.$IsMobile ? "<br/>" : null),
            (!BLANK($sourceName) ? CAT(" ... from '", $sourceName, "'") : null),
            (!BLANK($filter) ? CAT(" ... for '", $category, "'") : null)
        );

        $prepare.put("[#Title]", $s_Title);

        int $maxRows = Config.DB_ITEMS_ROWS;

        DOItem $doItem = new DOItem();
        //String $realFilter = DOItem.buildSqlByFilter($filter);
        String $realFilter = DOItem.buildSqlByCategory($category);
        DataSet $dsItems = $doItem.enumItems($sourceName, $realFilter, $listNumber, $maxRows);

        int $listTotal = $dsItems.getTotalPages();
        if ($listNumber > $listTotal) {
            if ($listTotal > 0) {
                $prepare.put("[#ErrMessage]", "List number is too large!");
                this.write("error", $prepare);
                return;
            }
            else {
                $prepare.put("[#ErrMessage]", "Empty list!");
                this.write("error", $prepare);
                return;
            }
        }
        if ($listTotal > 1) {
            $prepare.put("[#List_Total]", $listTotal);
            $prepare.put("[#List]", $listNumber);
        }

        int $count = 1;
        TArrayList $rows = new TArrayList();
        for (int $n = 0; $n < $dsItems.getSize(); $n++) {
            THashtable $oItem = $dsItems.getRow($n);
            THashtable $row = fillItemRow($oItem, $doItem.getIdField(), $count);
            $count++;
            $rows.add($row);
        }
        $prepare.put("[#Rows]", $rows);

        if ($listTotal > 1) {
            int $chunk = 2;
            Boolean $before = false;
            Boolean $after = false;

            TArrayList $pages = new TArrayList();
            for (int $n = 1; $n <= $listTotal; $n++) {
                THashtable $page = new THashtable();
                if ($n < $listNumber - $chunk) {
                    if (!$before) {
                        $before = true;
                        $page.put("[#Text]", "1");
                        $page.put("[#Link]", getPageLink(1));
                        $pages.add($page);
                        $page = new THashtable();
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
                        $page = new THashtable();
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
