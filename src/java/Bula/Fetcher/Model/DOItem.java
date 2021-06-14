// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Model;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Objects.DateTimes;
import java.util.Hashtable;
import Bula.Objects.Strings;
import Bula.Model.DBConfig;
import Bula.Model.DOBase;
import Bula.Model.DataSet;

/**
 * Manipulating with items.
 */
public class DOItem extends DOBase {
    /** Public constructor (overrides base constructor) */
    public DOItem () {
        this.$tableName = "items";
        this.$idField = "i_ItemId";
    }

    /**
     * Get item by ID.
     * @param $itemid ID of the item.
     * @return DataSet Resulting data set.
     */
    @Override
    public DataSet getById(int $itemid) { // overloaded
        if ($itemid <= 0) return null;
        String $query = Strings.concat(
            " SELECT _this.*, s.s_SourceName FROM ", this.$tableName, " _this ",
            " LEFT JOIN sources s ON (s.i_SourceId = _this.i_SourceLink) ",
            " WHERE _this.", this.$idField, " = ? ");
        Object[] $pars = ARR("setInt", $itemid);
        return this.getDataSet($query, $pars);
    }

    /**
     * Find item with given link.
     * @param $link Link to find.
     * @return DataSet Resulting data set.
     */
    public DataSet findItemByLink(String $link) {
        return findItemByLink($link, 0);
    }

    /**
     * Find item with given link.
     * @param $link Link to find.
     * @param $sourceId Source ID to find in (default = 0).
     * @return DataSet Resulting data set.
     */
    public DataSet findItemByLink(String $link, int $sourceId/* = 0 */) {
        if ($link == null)
            return null;
        String $query = Strings.concat(
            " SELECT _this.", this.$idField, " FROM ", this.$tableName, " _this ",
            //(BLANK($source) ? null : " LEFT JOIN sources s ON (s.i_SourceId = _this.i_SourceLink) "),
            " WHERE ", ($sourceId == 0 ? null : " _this.i_SourceLink = ? AND "), "_this.s_Link = ?");
        Object[] $pars = ARR();
        if ($sourceId != 0)
            $pars = ARR("setInt", $sourceId);
        $pars = ADD($pars, ARR("setString", $link));
        return this.getDataSet($query, $pars);
    }

    /**
     * Build SQL query from categories filter.
     * @param $filter Filter from the category.
     * @return String Appropriate SQL-query.
     */
    public String buildSqlFilter(String $filter) {
        String[] $filterChunks = Strings.split("~", $filter);
        String[] $includeChunks = SIZE($filterChunks) > 0 ?
            Strings.split("|", $filterChunks[0]) : null;
        String[] $excludeChunks = SIZE($filterChunks) > 1 ?
            Strings.split("|", $filterChunks[1]) : null;
        String $includeFilter = new String();
        for (int $n = 0; $n < SIZE($includeChunks); $n++) {
            if (!$includeFilter.isEmpty())
                $includeFilter += " OR ";
            $includeFilter += "(_this.s_Title LIKE '%";
                $includeFilter += $includeChunks[$n];
            $includeFilter += "%' OR _this.t_FullDescription LIKE '%";
                $includeFilter += $includeChunks[$n];
            $includeFilter += "%')";
        }
        if (!$includeFilter.isEmpty())
            $includeFilter = Strings.concat(" (", $includeFilter, ") ");

        String $excludeFilter = new String();
        for (int $n = 0; $n < SIZE($excludeChunks); $n++) {
            if (!BLANK($excludeFilter))
                $excludeFilter = Strings.concat($excludeFilter, " AND ");
            $excludeFilter = Strings.concat($excludeFilter,
                "(_this.s_Title not like '%", $excludeChunks[$n], "%' AND _this.t_Description not like '%", $excludeChunks[$n], "%')");
        }
        if (!$excludeFilter.isEmpty())
            $excludeFilter = Strings.concat(" (", $excludeFilter, ") ");

        String $realFilter = $includeFilter;
        if (!$excludeFilter.isEmpty())
            $realFilter = CAT($realFilter, " AND ", $excludeFilter);
        return $realFilter;
    }

    /**
     * Enumerate items.
     * @param $source Source name to include items from (default - all sources).
     * @param $search Filter for the category (or empty).
     * @param $list Include the list No.
     * @param $rows List size.
     * @return DataSet Resulting data set.
     */
    public DataSet enumItems(String $source, String $search, int $list, int $rows) { //, $totalRows) {
        String $realSearch = BLANK($search) ? null : this.buildSqlFilter($search);
        String $query1 = Strings.concat(
            " SELECT _this.", this.$idField, " FROM ", this.$tableName, " _this ",
            " LEFT JOIN sources s ON (s.i_SourceId = _this.i_SourceLink) ",
            " WHERE s.b_SourceActive = 1 ",
            (BLANK($source) ? null : CAT(" AND s.s_SourceName = '", $source, "' ")),
            (BLANK($realSearch) ? null : CAT(" AND (", $realSearch, ") ")),
            " ORDER BY _this.d_Date DESC, _this.", this.$idField, " DESC "
        );

        Object[] $pars1 = ARR();
        DataSet $ds1 = this.getDataSetList($query1, $pars1, $list, $rows); //, $totalRows);
        if ($ds1.getSize() == 0)
            return $ds1;

        int $totalPages = $ds1.getTotalPages();
        String $inList = new String();
        for (int $n = 0; $n < $ds1.getSize(); $n++) {
            Hashtable $o = $ds1.getRow($n);
            if ($n != 0)
                $inList += ", ";
            Object $id = $o.get(this.$idField);
            $inList += STR($id);
        }

        String $query2 = Strings.concat(
            " SELECT _this.", this.$idField, ", s.s_SourceName, _this.s_Title, _this.s_Url, _this.d_Date, _this.s_Category, ",
            " _this.s_Creator, _this.s_Custom1, _this.s_Custom2, s.s_SourceName ",
            " FROM ", this.$tableName, " _this ",
            " LEFT JOIN sources s ON (s.i_SourceId = _this.i_SourceLink ) ",
            " WHERE _this.", this.$idField, " IN (", $inList, ") ",
            " ORDER BY _this.d_Date DESC, _this.", this.$idField, " DESC "
        );
        Object[] $pars2 = ARR();
        DataSet $ds2 = this.getDataSet($query2, $pars2);
        $ds2.setTotalPages($totalPages);

        return $ds2;
    }

    /**
     * Enumerate items from date.
     * @param $fromdate Date to include items starting from.
     * @return DataSet Resulting data set.
     */
    public DataSet enumItemsFromDate(String $fromdate) {
        String $query = Strings.concat(
            " SELECT _this.*, s.s_SourceName FROM ", this.$tableName, " _this ",
            " INNER JOIN sources s ON (s.i_SourceId = _this.i_SourceLink) ",
            " WHERE _this.d_Date > ? ",
            " ORDER BY _this.d_Date DESC, _this.", this.$idField, " DESC "
        );
        Object[] $pars = ARR("setDate", $fromdate);
        return this.getDataSet($query, $pars);

    }

    /**
     * Enumerate items from given date.
     * @param $fromDate Date to include items starting from.
     * @param $source Source name to include items from (default - all sources).
     * @param $filter Filter for the category (or empty - no filtering).
     * @return DataSet Resulting data set.
     */
    public DataSet enumItemsFromSource(String $fromDate, String $source, String $filter) {
        return this.enumItemsFromSource($fromDate, $source, $filter, 20);
    }

    /**
     * Enumerate items from given date.
     * @param $fromDate Date to include items starting from.
     * @param $source Source name to include items from (default - all sources).
     * @param $filter Filter for the category (or empty - no filtering).
     * @param $maxItems Max number of returned items.
     * @return DataSet Resulting data set.
     */
    public DataSet enumItemsFromSource(String $fromDate, String $source, String $filter, int $maxItems/* = 20 */) {
        String $realFilter = BLANK($filter) ? null : this.buildSqlFilter($filter);
        String $query1 = Strings.concat(
            " SELECT _this.*, s.s_SourceName FROM ", this.$tableName, " _this ",
            " INNER JOIN sources s ON (s.i_SourceId = _this.i_SourceLink) ",
            " WHERE s.b_SourceActive = 1 ",
            (BLANK($source) ? null : Strings.concat(" AND s.s_SourceName = '", $source, "' ")),
            (BLANK($realFilter) ? null : Strings.concat(" AND (", $realFilter, ") ")),
            " ORDER BY _this.d_Date DESC, _this.", this.$idField, " DESC ",
            " LIMIT ", STR($maxItems)
        );
        Object[] $pars1 = ARR();
        DataSet $ds1 = this.getDataSet($query1, $pars1);
        if ($fromDate == null)
            return $ds1;

        String $query2 = Strings.concat(
            " SELECT _this.*, s.s_SourceName FROM ", this.$tableName, " _this ",
            " INNER JOIN sources s ON (s.i_SourceId = _this.i_SourceLink) ",
            " WHERE s.b_SourceActive = 1 ",
            (BLANK($source) ? null : Strings.concat(" AND s.s_SourceName = '", $source, "' ")),
            " AND _this.d_Date > ? ",
            (BLANK($realFilter) ? null : Strings.concat(" AND (", $realFilter, ") ")),
            " ORDER BY _this.d_Date DESC, _this.", this.$idField, " DESC ",
            " LIMIT ", STR($maxItems)
        );
        Object[] $pars2 = ARR("setDate", $fromDate);
        DataSet $ds2 = this.getDataSet($query2, $pars2);

        return $ds1.getSize() > $ds2.getSize() ? $ds1 : $ds2;
    }

    /**
     * Purge items.
     * @param $days Remove items older than $days.
     * @return DataSet Resulting data set.
     */
    public int purgeOldItems(int $days) {
        String $purgeDate = DateTimes.format(DBConfig.SQL_DTS, DateTimes.getTime(CAT("-", $days, " days")));
        String $query = Strings.concat("DELETE FROM ", this.$tableName, " WHERE d_Date < ?");
        Object[] $pars = ARR("setDate", $purgeDate);

        return this.updateInternal($query, $pars, "update");
    }
}
