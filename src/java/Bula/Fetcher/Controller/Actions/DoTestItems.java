// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Actions;
import Bula.Meta;

import Bula.Objects.TResponse;
import Bula.Objects.DateTimes;
import Bula.Objects.THashtable;
import Bula.Model.DataSet;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Fetcher.Model.DOTime;
import Bula.Fetcher.Controller.Page;
import Bula.Fetcher.Controller.BOFetcher;

/**
 * Testing sources for necessary fetching.
 */
public class DoTestItems extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public DoTestItems(Context $context) { super($context); initialize(); }

    private static String $TOP = null;
    private static String $BOTTOM = null;

    /** Initialize TOP and BOTTOM blocks. */
    public static void initialize() {
        $TOP = CAT(
            "<!DOCTYPE html>", EOL,
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">", EOL,
            "    <head>", EOL,
            "        <title>Buddy Fetcher -- Test for new items</title>", EOL,
            "        <meta name=\"keywords\" content=\"Buddy Fetcher, rss, fetcher, aggregator, ", Config.PLATFORM, ", MySQL\" />", EOL,
            "        <meta name=\"description\" content=\"Buddy Fetcher is a simple RSS Fetcher/aggregator written in ", Config.PLATFORM, "/MySQL\" />", EOL,
            "        <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />", EOL,
            "    </head>", EOL,
            "    <body>", EOL
        );
        $BOTTOM = CAT(
            "    </body>", EOL,
            "</html>", EOL
        );
    }

    /** Execute main logic for DoTestItems action */
    public void execute() {
        Boolean $insertRequired = false;
        Boolean $updateRequired = false;

        DOTime $doTime = new DOTime(this.$context.$Connection);

        DataSet $dsTimes = $doTime.getById(1);
        int $timeShift = -1; // 240; // 4 min
        long $currentTime = DateTimes.getTime();
        if ($dsTimes.getSize() > 0) {
            THashtable $oTime = $dsTimes.getRow(0);
            if ($currentTime > DateTimes.getTime(STR($oTime.get("d_Time"))) + $timeShift)
                $updateRequired = true;
        }
        else
            $insertRequired = true;

        String $from = null;
        if (this.$context.$Request.contains("from"))
            $from = this.$context.$Request.get("from");

        this.$context.$Response.write($TOP);
        if ($updateRequired || $insertRequired) {
            this.$context.$Response.write(CAT("Fetching new items... Please wait...<br/>", EOL));

            BOFetcher $boFetcher = new BOFetcher(this.$context);
            $boFetcher.fetchFromSources($from);

            $doTime = new DOTime(this.$context.$Connection); // Need for DB reopen
            THashtable $fields = new THashtable();
            $fields.put("d_Time", DateTimes.format(DateTimes.SQL_DTS, DateTimes.getTime()));
            if ($insertRequired) {
                $fields.put("i_Id", 1);
                $doTime.insert($fields);
            }
            else
                $doTime.updateById(1, $fields);
        }
        else
            this.$context.$Response.write(CAT("<hr/>Fetch is not required<br/>", EOL));
        this.$context.$Response.write($BOTTOM);
    }
}
