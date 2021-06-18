// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Objects.DataList;
import Bula.Objects.DataRange;

/**
 * Logic for generating Menu block.
 */
public class Menu extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public Menu(Context $context) { super($context); }

    /** Execute main logic for Menu block */
    public void execute() {
        DataList $publicPages = new DataList();

        $publicPages.add("Home");
        $publicPages.add("home");
        if (this.$context.$IsMobile) {
            $publicPages.add(Config.NAME_ITEMS); $publicPages.add("items");
            if (Config.SHOW_BOTTOM && this.$context.contains("Name_Categories")) {
                $publicPages.add(CAT("By ", this.$context.get("Name_Categories")));
                $publicPages.add("#items_by_skills");
                //$publicPages.add("RSS Feeds");
                //$publicPages.add("#read_rss_feeds");
            }
            $publicPages.add("Sources");
            $publicPages.add("sources");
        }
        else {
            $publicPages.add(CAT("Browse ", Config.NAME_ITEMS));
            $publicPages.add("items");
            if (Config.SHOW_BOTTOM && this.$context.contains("Name_Categories")) {
                $publicPages.add(CAT(Config.NAME_ITEMS, " by ", this.$context.get("Name_Categories")));
                $publicPages.add("#items_by_skills");

                $publicPages.add("Read RSS Feeds");
                $publicPages.add("#read_rss_feeds");
            }
            $publicPages.add("Sources");
            $publicPages.add("sources");
        }

        DataList $menuItems = new DataList();
        for (int $n = 0; $n < $publicPages.size(); $n += 2) {
            DataRange $row = new DataRange();
            String $title = STR($publicPages.get($n+0));
            String $page = STR($publicPages.get($n+1));
            String $href = null;
            if (EQ($page, "home"))
                $href = Config.TOP_DIR;
            else {
                if (EQ($page.substring(0, 1), "#"))
                    $href = $page;
                else {
                    $href = this.getLink(Config.INDEX_PAGE, "?p=", null, $page);
                }
            }
            $row.put("[#Link]", $href);
            $row.put("[#LinkText]", $title);
            $row.put("[#Prefix]", $n != 0 ? " &bull; " : " ");
            $menuItems.add($row);
        }

        DataRange $prepare = new DataRange();
        $prepare.put("[#MenuItems]", $menuItems);
        this.write("menu", $prepare);
    }
}

