// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.TResponse;
import Bula.Objects.Strings;

/**
 * Main logic for generating RSS-feeds.
 */
public class Rss extends RssBase {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Rss(Context $context) { super($context); }

    /**
     * Write error message.
     * @param $errorMessage Error message.
     */
    public  void writeErrorMessage(String $errorMessage) {
        this.$context.$Response.writeHeader("Content-type", "text/xml; charset=UTF-8");
        this.$context.$Response.write(CAT("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>", EOL));
        this.$context.$Response.write(CAT("<data>", $errorMessage, "</data>"));
    }

    /**
     * Write starting block of RSS-feed.
     * @param $source RSS-feed source name.
     * @param $category RSS-feed 'filtered by' category.
     * @param $pubDate Publication date.
     * @return String Resulting XML-content of starting block.
     */
    public  String writeStart(String $source, String $category, String $pubDate) {
        String $rssTitle = CAT(
            "Items for ", (BLANK($source) ? "ALL sources" : CAT("'", $source, "'")),
            (BLANK($category) ? null : CAT(" and filtered by '", $category, "'"))
        );
        String $xmlContent = Strings.concat(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>", EOL,
            "<rss version=\"2.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">", EOL,
            "<channel>", EOL,
            //"<title>" + Config.SITE_NAME + "</title>", EOL,
            "<title>", $rssTitle, "</title>", EOL,
            "<link>", this.$context.$Site, Config.TOP_DIR, "</link>", EOL,
            "<description>", $rssTitle, "</description>", EOL,
            (this.$context.$Lang == "ru" ? "<language>ru-RU</language>\r\n" : "<language>en-US</language>"), EOL,
            "<pubDate>", $pubDate, "</pubDate>", EOL,
            "<lastBuildDate>", $pubDate, "</lastBuildDate>", EOL,
            "<generator>", Config.SITE_NAME, "</generator>", EOL
        );
        return $xmlContent;
    }

    /**
     * Write ending block of RSS-feed.
     */
    public  String writeEnd() {
        String $xmlContent = Strings.concat(
            "</channel>", EOL,
            "</rss>", EOL);
        return $xmlContent;
    }

    /**
     * Write an item of RSS-feed.
     * @param[] $args Array of item parameters.
     * @return String Resulting XML-content of an item.
     */
    public  String writeItem(Object[] $args) {
        String $xmlTemplate = Strings.concat(
            "<item>", EOL,
            "<title><![CDATA[{1}]]></title>", EOL,
            "<link>{0}</link>", EOL,
            "<pubDate>{4}</pubDate>", EOL,
            BLANK($args[5]) ? null : CAT("<description><![CDATA[{5}]]></description>", EOL),
            BLANK($args[6]) ? null : CAT("<category><![CDATA[{6}]]></category>", EOL),
            "<guid>{0}</guid>", EOL,
            "</item>", EOL
        );
        String $itemContent = Util.formatString($xmlTemplate, $args);
        return $itemContent;
    }
}
