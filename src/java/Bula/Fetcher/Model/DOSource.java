// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Model;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;
import Bula.Objects.Strings;
import Bula.Model.DOBase;
import Bula.Model.DataSet;

/**
 * Manipulations with sources.
 */
public class DOSource extends DOBase {
    /** Public constructor (overrides base constructor) */
    public DOSource () {
        this.$tableName = "sources";
        this.$idField = "i_SourceId";
    }

    /**
     * Enumerates all sources.
     * @return DataSet Resulting data set.
     */
    public DataSet enumSources() {
        String $query = Strings.concat(
            " SELECT _this.* FROM ", this.$tableName, " _this ",
            " where _this.b_SourceActive = 1 ",
            " order by _this.s_SourceName asc"
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Enumerates sources, which are active for fetching.
     * @return DataSet Resulting data set.
     */
    public DataSet enumFetchedSources() {
        String $query = Strings.concat(
            " SELECT _this.* FROM ", this.$tableName, " _this ",
            " where _this.b_SourceFetched = 1 ",
            " order by _this.s_SourceName asc"
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Enumerates all sources with counters.
     * @return DataSet Resulting data set.
     */
    public DataSet enumSourcesWithCounters() {
        String $query = Strings.concat(
            " select _this.", this.$idField, ", _this.s_SourceName, ",
            " count(p.i_SourceLink) as cntpro ",
            " from ", this.$tableName, " _this ",
            " left outer join items p on (p.i_SourceLink = _this.i_SourceId) ",
            " where _this.b_SourceActive = 1 ",
            " group by _this.i_SourceId ",
            " order by _this.s_SourceName asc "
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Get source by ID.
     * @param $sourceid Source ID.
     * @return DataSet Resulting data set.
     */
    public DataSet getSourceById(int $sourceid) {
        if ($sourceid <= 0) return null;
        String $query = Strings.concat("SELECT * FROM sources where i_SourceId = ?");
        Object[] $pars = ARR("setInt", $sourceid);
        return this.getDataSet($query, $pars);
    }

    /**
     * Get source by name.
     * @param $sourcename Source name.
     * @return DataSet Resulting data set.
     */
    public DataSet getSourceByName(String $sourcename) {
        if ($sourcename == null || $sourcename == "") return null;
        String $query = Strings.concat("SELECT * FROM sources where s_SourceName = ?");
        Object[] $pars = ARR("setString", $sourcename);
        return this.getDataSet($query, $pars);
    }

    /**
     * Check whether source exists.
     * @param $sourcename Source name.
     * @return boolean True if exists.
     */
    public Boolean checkSourceName(String $sourcename) {
        return checkSourceName($sourcename, null);
    }

    /**
     * Check whether source exists.
     * @param $sourcename Source name.
     * @param[] $source Source object (if found) copied to element 0 of object array.
     * @return boolean True if exists.
     */
    public Boolean checkSourceName(String $sourcename, Object[] /*&*/$source /* = null */) {
        DataSet $dsSources = this.enumSources();
        Boolean $sourceFound = false;
        for (int $n = 0; $n < $dsSources.getSize(); $n++) {
            THashtable $oSource = $dsSources.getRow($n);
            if (EQ($oSource.get("s_SourceName"), $sourcename)) {
                $sourceFound = true;
                if ($source != null)
                    $source[0] = $oSource;
                break;
            }
        }
        return $sourceFound;
    }
}
