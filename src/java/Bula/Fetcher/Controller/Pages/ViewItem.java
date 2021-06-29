// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.TRequest;
import Bula.Objects.THashtable;
import Bula.Model.DataSet;
import Bula.Fetcher.Model.DOItem;
import Bula.Fetcher.Controller.Util;
import Bula.Fetcher.Controller.Page;
import Bula.Fetcher.Controller.Engine;

/**
 * Controller for View Item block.
 */
public class ViewItem extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public ViewItem(Context $context) { super($context); }

    /**
     * Fast check of input query parameters.
     * @return THashtable Parsed parameters (or null in case of any error).
     */
    public THashtable check() {
        THashtable $prepare = new THashtable();
        if (!this.$context.$Request.contains("id")) {
            $prepare.put("[#ErrMessage]", "Item ID is required!");
            this.write("error", $prepare);
            return null;
        }
        String $id = this.$context.$Request.get("id");
        if (!TRequest.isInteger($id)) {
            $prepare.put("[#ErrMessage]", "Item ID must be positive integer!");
            this.write("error", $prepare);
            return null;
        }

        THashtable $pars = new THashtable();
        $pars.put("id", $id);
        return $pars;
    }

    /** Execute main logic for View Item block. */
    public void execute() {
        THashtable $pars = check();
        if ($pars == null)
            return;

        String $id = (String)$pars.get("id");

        THashtable $prepare = new THashtable();

        DOItem $doItem = new DOItem();
        DataSet $dsItems = $doItem.getById(INT($id));
        if ($dsItems == null || $dsItems.getSize() == 0) {
            $prepare.put("[#ErrMessage]", "Wrong item ID!");
            this.write("error", $prepare);
            return;
        }

        THashtable $oItem = $dsItems.getRow(0);
        String $title = STR($oItem.get("s_Title"));
        String $sourceName = STR($oItem.get("s_SourceName"));

        this.$context.set("Page_Title", $title);
        String $leftWidth = "25%";
        if (this.$context.$IsMobile)
            $leftWidth = "20%";

        String $idField = $doItem.getIdField();
        if (Config.SHOW_IMAGES)
            $prepare.put("[#Show_Images]", 1);
        $prepare.put("[#RedirectLink]", this.getLink(Config.ACTION_PAGE, "?p=do_redirect_item&id=", "redirect/item/", $oItem.get($idField)));
        $prepare.put("[#LeftWidth]", $leftWidth);
        $prepare.put("[#Title]", Util.show($title));
        $prepare.put("[#InputTitle]", Util.safe($title));
        $prepare.put("[#RedirectSource]", this.getLink(Config.ACTION_PAGE, "?p=do_redirect_source&source=", "redirect/source/", $sourceName));
        $prepare.put("[#SourceName]", $sourceName);
        $prepare.put("[#ExtImages]", Config.EXT_IMAGES);
        $prepare.put("[#SourceLink]", this.getLink(Config.INDEX_PAGE, "?p=items&source=", "items/source/", $sourceName));
        $prepare.put("[#Date]", Util.showTime(STR($oItem.get("d_Date"))));
        if (!NUL($oItem.get("s_Creator")))
            $prepare.put("[#Creator]", STR($oItem.get("s_Creator")));
        if ($oItem.containsKey("t_Description") && !BLANK(STR($oItem.get("t_Description"))))
            $prepare.put("[#Description]", Util.show(STR($oItem.get("t_Description"))));
        $prepare.put("[#ItemID]", $oItem.get($idField));
        if (this.$context.contains("Name_Category") && !NUL($oItem.get("s_Category")))
            $prepare.put("[#Category]", $oItem.get("s_Category"));
        if (this.$context.contains("Name_Custom1") && !NUL($oItem.get("s_Custom1")))
            $prepare.put("[#Custom1]", $oItem.get("s_Custom1"));
        if (this.$context.contains("Name_Custom2") && !NUL($oItem.get("s_Custom2")))
            $prepare.put("[#Custom2]", $oItem.get("s_Custom2"));

        if (this.$context.$Lang == "ru" && !this.$context.$IsMobile)
            $prepare.put("[#Share]", 1);

        Engine $engine = this.$context.getEngine();

        if (Config.CACHE_PAGES)
            $prepare.put("[#Home]", Util.showFromCache($engine, this.$context.$CacheFolder, "home", "Home", "p=home&from_view_item=1"));
        else
            $prepare.put("[#Home]", $engine.includeTemplate("Pages/Home"));

        this.write("Pages/view_item", $prepare);
    }
}
