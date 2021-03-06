// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Pages;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.Regex;

import Bula.Objects.Strings;
import Bula.Objects.THashtable;
import Bula.Objects.TRequest;

import Bula.Fetcher.Controller.Util;
import Bula.Fetcher.Controller.Engine;
import Bula.Fetcher.Controller.Page;

/**
 * Base controller for Items block.
 */
abstract class ItemsBase extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public ItemsBase(Context $context) { super($context); }

    /**
     * Check list from current query.
     * @return Boolean True - checked OK, False - error.
     */
    public Boolean checkList() {
        if (this.$context.$Request.contains("list")) {
            if (!TRequest.isInteger(this.$context.$Request.get("list"))) {
                THashtable $prepare = new THashtable();
                $prepare.put("[#ErrMessage]", "Incorrect list number!");
                this.write("error", $prepare);
                return false;
            }
        }
        else
            this.$context.$Request.set("list", "1");
        return true;
    }

    /**
     * Check source name from current query.
     * @return Boolean True - source exists, False - error.
     */
    public Boolean checkSource() {
        String $errMessage = new String();
        if (this.$context.$Request.contains("source")) {
            String $source = this.$context.$Request.get("source");
            if (BLANK($source))
                $errMessage += "Empty source name!<br/>";
            else if (!TRequest.isDomainName("source"))
                $errMessage += "Incorrect source name!<br/>";
        }
        if ($errMessage.isEmpty())
            return true;

        THashtable $prepare = new THashtable();
        $prepare.put("[#ErrMessage]", $errMessage);
        this.write("error", $prepare);
        return false;
    }

    /**
     * Fill Row from Item.
     * @param $oItem Original Item.
     * @param $idField Name of ID field.
     * @param $count The number of inserted Row in HTML table.
     * @return THashtable Resulting Row.
     */
    protected THashtable fillItemRow(THashtable $oItem, String $idField, int $count) {
        THashtable $row = new THashtable();
        int $itemId = INT($oItem.get($idField));
        String $urlTitle = STR($oItem.get("s_Url"));
        String $itemHref = this.$context.$ImmediateRedirect ?
                getRedirectItemLink($itemId, $urlTitle) :
                getViewItemLink($itemId, $urlTitle);
        $row.put("[#Link]", $itemHref);
        if (($count % 2) == 0)
            $row.put("[#Shade]", "1");

        if (Config.SHOW_FROM)
            $row.put("[#Show_From]", 1);
        if (Config.SHOW_IMAGES)
            $row.put("[#Show_Images]", 1);
        String $sourceName = STR($oItem.get("s_SourceName"));
        $row.put("[#SourceName]", $sourceName);
        $row.put("[#ExtImages]", Config.EXT_IMAGES);
        $row.put("[#Title]", Util.show(STR($oItem.get("s_Title"))));
        $row.put("[#SourceLink]", this.getLink(Config.INDEX_PAGE, "?p=items&source=", "items/source/", $sourceName));

        if (this.$context.contains("Name_Category") && $oItem.containsKey("s_Category") && !NUL($oItem.get("s_Category")))
            $row.put("[#Category]", STR($oItem.get("s_Category")));

        if (this.$context.contains("Name_Creator") && $oItem.containsKey("s_Creator") && !NUL($oItem.get("s_Creator"))) {
            String $s_Creator = STR($oItem.get("s_Creator"));
            if ($s_Creator != null) {
                if ($s_Creator.indexOf("(") != -1)
                    $s_Creator = $s_Creator.replace("(", "<br/>(");
            }
            else
                $s_Creator = new String(" "); //TODO -- "" doesn't works somehow, need to investigate
            $row.put("[#Creator]", $s_Creator);
        }
        if (this.$context.contains("Name_Custom1") && $oItem.containsKey("s_Custom1") && !NUL($oItem.get("s_Custom1")))
            $row.put("[#Custom1]", $oItem.get("s_Custom1"));
        if (this.$context.contains("Name_Custom2") && $oItem.containsKey("s_Custom2") && !NUL($oItem.get("s_Custom2")))
            $row.put("[#Custom2]", $oItem.get("s_Custom2"));

        String $d_Date = Util.showTime(STR($oItem.get("d_Date")));
        if (this.$context.$IsMobile)
            $d_Date = Strings.replace("-", " ", $d_Date);
        else {
            if (BLANK(this.$context.$Api))
                $d_Date = Strings.replaceFirst(" ", "<br/>", $d_Date);
        }
        $row.put("[#Date]", $d_Date);
        return $row;
    }

    /**
     * Get link for redirecting to external item.
     * @param $itemId Item ID.
     * @return String Resulting external link.
     */
    public String getRedirectItemLink(int $itemId) {
        return getRedirectItemLink($itemId, null);
    }

    /**
     * Get link for redirecting to external item.
     * @param $itemId Item ID.
     * @param $urlTitle Normalized title (to include in the link).
     * @return String Resulting external link.
     */
    public String getRedirectItemLink(int $itemId, String $urlTitle /*= null*/) {
        String $link = this.getLink(Config.ACTION_PAGE, "?p=do_redirect_item&id=", "redirect/item/", $itemId);
        if (!BLANK($urlTitle))
            $link = this.appendLink($link, "&title=", "/", $urlTitle);
        return $link;
    }

    /**
     * Get link for redirecting to the item (internally).
     * @param $itemId Item ID.
     * @return String Resulting internal link.
     */
    public String getViewItemLink(int $itemId) {
        return getViewItemLink($itemId, null);
    }

    /**
     * Get link for redirecting to the item (internally).
     * @param $itemId Item ID.
     * @param $urlTitle Normalized title (to include in the link).
     * @return String Resulting internal link.
     */
    public String getViewItemLink(int $itemId, String $urlTitle /*= null*/) {
        String $link = this.getLink(Config.INDEX_PAGE, "?p=view_item&id=", "item/", $itemId);
        if (!BLANK($urlTitle))
            $link = this.appendLink($link, "&title=", "/", $urlTitle);
        return $link;
    }

    /**
     * Get internal link to the page.
     * @param $listNo Page no.
     * @return String Resulting internal link to the page.
     */
    protected String getPageLink(int $listNo) {
        String $link = this.getLink(Config.INDEX_PAGE, "?p=items", "items");
        if (this.$context.$Request.contains("source") && !BLANK(this.$context.$Request.get("source")))
            $link = this.appendLink($link, "&source=", "/source/", this.$context.$Request.get("source"));
        if (this.$context.$Request.contains("filter") && !BLANK(this.$context.$Request.get("filter")))
            $link = this.appendLink($link, "&amp;filter=", "/filter/", this.$context.$Request.get("filter"));
        if ($listNo > 1)
            $link = this.appendLink($link, "&list=", "/list/", $listNo);
        return $link;
    }

    //abstract void execute();
}
