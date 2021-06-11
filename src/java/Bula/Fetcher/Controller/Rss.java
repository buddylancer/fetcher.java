
package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.Response;
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

    public  void writeErrorMessage(String $errorMessage) {
        Response.writeHeader("Content-type", "text/xml; charset=UTF-8");
        Response.write(CAT("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>", EOL));
        Response.write(CAT("<data>", $errorMessage, "</data>"));
    }

    public  String writeStart(String $source, String $filterName, String $pubDate) {
        String $rssTitle = CAT(
            "Items for ", (BLANK($source) ? "ALL sources" : CAT("'", $source, "'")),
            (BLANK($filterName) ? null : CAT(" and filtered by '", $filterName, "'"))
        );
        String $xmlContent = Strings.concat(
            "<rss version=\"2.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\r\n",
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
        Response.writeHeader("Content-type", "text/xml; charset=UTF-8");
        Response.write(CAT("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>", EOL));
        Response.write($xmlContent);
        return $xmlContent;
    }

    public  String writeEnd() {
        String $xmlContent = Strings.concat(
            "</channel>", EOL,
            "</rss>", EOL);
        Response.write($xmlContent);
        Response.end("");
        return $xmlContent;
    }

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
        Response.write($itemContent);
        return $itemContent;
    }
}
