// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import java.util.Hashtable;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.DateTimes;

/**
 * Basic logic for generating Page block.
 */
public abstract class Page extends Meta {
    /** Current context */
    protected Context $context = null;

    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Page (Context $context) {
        this.$context = $context;
        //echo "In Page constructor -- " + print_r($context, true);
    }

    /** Execute main logic for page block */
    abstract public void execute();

    /**
     * Merge template with variables and write to engine.
     * @param $template Template name.
     * @param $prepare Prepared variables.
     */
    public void write(String $template, Hashtable $prepare) {
        Engine $engine = this.$context.getEngine();
        $engine.write($engine.showTemplate($template, $prepare));
    }

    /**
     * Get link for the page.
     * @param $page Page to get link for.
     * @param $ordinaryUrl Url portion of full Url.
     * @param $fineUrl Url portion of fine Url.
     * @return String Resulting link.
     */
    public String getLink(String $page, String $ordinaryUrl, String $fineUrl) {
        return getLink($page, $ordinaryUrl, $fineUrl, null);
    }

    /**
     * Get link for the page.
     * @param $page Page to get link for.
     * @param $ordinaryUrl Url portion of full Url.
     * @param $fineUrl Url portion of fine Url.
     * @param $extraData Optional prefix.
     * @return String Resulting link.
     */
    public String getLink(String $page, String $ordinaryUrl, String $fineUrl, Object $extraData/* = null*/) {
        if (!BLANK(this.$context.$Api))
            return this.getAbsoluteLink($page, $ordinaryUrl, $fineUrl, $extraData);
        else
            return this.getRelativeLink($page, $ordinaryUrl, $fineUrl, $extraData);
    }

    /**
     * Get relative link for the page.
     * @param $page Page to get link for.
     * @param $ordinaryUrl Url portion of full Url.
     * @param $fineUrl Url portion of fine Url.
     * @return String Resulting relative link.
     */
    public String getRelativeLink(String $page, String $ordinaryUrl, String $fineUrl) {
        return getRelativeLink($page, $ordinaryUrl, $fineUrl, null);
    }

    /**
     * Get relative link for the page.
     * @param $page Page to get link for.
     * @param $ordinaryUrl Url portion of full Url.
     * @param $fineUrl Url portion of fine Url.
     * @param $extraData Optional prefix.
     * @return String Resulting relative link.
     */
     public String getRelativeLink(String $page, String $ordinaryUrl, String $fineUrl, Object $extraData/* = null*/) {
        String $link = CAT(
            Config.TOP_DIR,
            (this.$context.$FineUrls ? $fineUrl : CAT($page, this.quoteLink($ordinaryUrl))),
            $extraData);
        return $link;
    }

    /**
     * Get absolute link for the page.
     * @param $page Page to get link for.
     * @param $ordinaryUrl Url portion of full Url.
     * @param $fineUrl Url portion of fine Url.
     * @return String Resulting absolute link.
     */
    public String getAbsoluteLink(String $page, String $ordinaryUrl, String $fineUrl) {
        return getAbsoluteLink($page, $ordinaryUrl, $fineUrl, null);
    }

    /**
     * Get absolute link for the page.
     * @param $page Page to get link for.
     * @param $ordinaryUrl Url portion of full Url.
     * @param $fineUrl Url portion of fine Url.
     * @param $extraData Optional prefix.
     * @return String Resulting absolute link.
     */
     public String getAbsoluteLink(String $page, String $ordinaryUrl, String $fineUrl, Object $extraData/* = null*/) {
        return CAT(this.$context.$Site, this.getRelativeLink($page, $ordinaryUrl, $fineUrl, $extraData));
    }

    /**
     * Append info to a link.
     * @param $link Link to append info to.
     * @param $ordinaryUrl Url portion of full Url.
     * @param $fineUrl Url portion of fine Url.
     * @return String Resulting link.
     */
    public String appendLink(String $link, String $ordinaryUrl, String $fineUrl) {
        return appendLink($link, $ordinaryUrl, $fineUrl, null);
    }

    /**
     * Append info to a link.
     * @param $link Link to append info to.
     * @param $ordinaryUrl Url portion of full Url.
     * @param $fineUrl Url portion of fine Url.
     * @param $extraData Optional prefix.
     * @return String Resulting link.
     */
    public String appendLink(String $link, String $ordinaryUrl, String $fineUrl, Object $extraData/* = null*/) {
        return CAT($link, (this.$context.$FineUrls ? $fineUrl : this.quoteLink($ordinaryUrl)), $extraData);
    }

    /**
     * Quote (escape special characters) a link.
     * @param $link Source link.
     * @return String Target (quoted) link.
     */
    public String quoteLink(String $link) {
        return !BLANK(this.$context.$Api) && EQ(Config.API_FORMAT, "Xml") ? Util.safe($link) : $link;
    }
}
