// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Model;
import Bula.Meta;

import Bula.Fetcher.Config;
import java.util.ArrayList;
import java.util.Hashtable;
import Bula.Objects.Strings;
import Bula.Model.DOBase;
import Bula.Model.DataSet;

/**
 * Manipulating with categories.
 */
public class DOCategory extends DOBase {
    /** Public constructor (overrides base constructor) */
    public DOCategory () {
        this.$tableName = "categories";
        this.$idField = "s_CatId";
    }

    /**
     * Get category by ID.
     * @param $catid Category ID.
     * @return DataSet Resulting data set.
     */
    public DataSet getCategoryById(String $catid) {
        if (BLANK($catid))
            return null;
        String $query = Strings.concat(
            " SELECT * FROM ", this.$tableName, " _this " ,
            " WHERE _this.", this.$idField, " = ? ");
        Object[] $pars = ARR("setString", $catid);
        return this.getDataSet($query, $pars);
    }

    /**
     * Get category by name.
     * @param $catname Category name.
     * @return DataSet Resulting data set.
     */
    public DataSet getCategoryByName(String $catname) {
        if (BLANK($catname))
            return null;
        String $query = Strings.concat(
            " SELECT * FROM ", this.$tableName, " _this ",
            " WHERE _this.s_Name = ? ");
        Object[] $pars = ARR("setString", $catname);
        return this.getDataSet($query, $pars);
    }

    /**
     * Enumerate categories.
     * @return DataSet Resulting data set.
     */
    public DataSet enumCategories() {
        return this.enumCategories(null, 0, 0); }

    /**
     * Enumerate categories.
     * @param $order Field name to sort result by (default = null).
     * @return DataSet Resulting data set.
     */
    public DataSet enumCategories(String $order) {
        return this.enumCategories($order, 0, 0); }

    /**
     * Enumerate categories.
     * @param $order Field name to sort result by (default = null).
     * @param $minCount Include categories with Counter >= min_count.
     * @return DataSet Resulting data set.
     */
    public DataSet enumCategories(String $order, int $minCount) {
        return this.enumCategories($order, $minCount, 0); }

    /**
     * Enumerate categories.
     * @param $order Field name to sort result by (default = null).
     * @param $minCount Include categories with Counter >= min_count.
     * @param $limit Include not more than "limit" records (default = no limit).
     * @return DataSet Resulting data set.
     */
    public DataSet enumCategories(String $order /*= null*/, int $minCount /*= 0*/, int $limit /*= 0*/) {
        if ($minCount < 0)
            return null;
        String $query = Strings.concat(
            " SELECT * FROM ", this.$tableName, " _this ",
            ($minCount > 0 ? CAT(" WHERE _this.i_Counter > ", $minCount) : null),
            " ORDER BY ", (EQ($order, "counter") ? " _this.i_Counter desc " : " _this.s_CatId asc "),
            ($limit == 0 ? null : CAT(" LIMIT ", $limit))
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Check whether category (filter) exists.
     * @param $filterName Category ID.
     * @param[] $category Category object (if found) copied to element 0 of object array.
     * @return boolean True if exists.
     */
    public Boolean checkFilterName(String $filterName, Object[] /*&*/$category /* = null */) {
        DataSet $dsCategories = this.select("_this.s_CatId, _this.s_Filter");
        Boolean $filterFound = false;
        for (int $n = 0; $n < $dsCategories.getSize(); $n++) {
            Hashtable $oCategory = $dsCategories.getRow($n);
            if (EQ($oCategory.get("s_CatId"), $filterName)) {
                $filterFound = true;
                if ($category != null)
                    $category[0] = $oCategory;
                break;
            }
        }
        return $filterFound;
    }
}
