// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Model;
import Bula.Meta;

import Bula.Objects.TArrayList;
import Bula.Objects.THashtable;

import Bula.Objects.Arrays;

/**
 * Implement operations with record sets.
 */
public class RecordSet extends Meta {
    /** Current result */
    public Object $result = null;
    /** Current record */
    public THashtable $record = null;

    private int $numRows = 0;
    private int $numPages = 0;
    private int $pageRows = 10;
    private int $pageNo = 0;

    /** Public constructor */
    public RecordSet () {
        this.$numRows = 0;
        this.$numPages = 0;
        this.$pageRows = 10;
        this.$pageNo = 0;
    }

    /**
     * Set number of page rows in record set.
     * @param $no Number of rows.
     */
    public void setPageRows(int $no) {
        this.$pageRows = $no;
    }

    /**
     * Set current number of rows (and pages) in the record set.
     * @param $no Number of rows.
     */
    public void setRows(int $no) {
        this.$numRows = $no;
        this.$numPages = INT(($no - 1) / this.$pageRows) + 1;
    }

    /**
     * Get current number of rows in the record set.
     * @return int Number of rows.
     */
    public int getRows() {
        return this.$numRows;
    }

    /**
     * Get current number of pages in the record set.
     * @return int Number of pages.
     */
    public int getPages() {
        return this.$numPages;
    }

    /**
     * Set current page of the record set.
     * @param $no Current page.
     */
    public void setPage(int $no) {
        this.$pageNo = $no;
        if ($no != 1) {
            int $n = ($no - 1) * this.$pageRows;
            while ($n-- > 0)
                this.next();
        }
    }

    /**
     * Get current page of the record set.
     * @return int Current page number.
     */
    public int getPage() {
        return this.$pageNo;
    }

    /**
     * Get next record from the result of operation.
     * @return int Status of operation:
     *   1 - next record exists.
     *   0 - next record not exists.
     */
    public int next() {
        Object $arr = DataAccess.fetchArray(this.$result);

        if ($arr != null) {
            this.$record = (THashtable)$arr;
            return 1;
        }
        else
            return 0;
    }

    /**
     * Get value from the record.
     * @param $par Number of value.
     * @return Object
     */
    public Object getValue(String $par) {
        return this.$record.get($par);
    }

    /**
     * Get String value from the record.
     * @param $par Number of value.
     * @return String
     */
    public String getString(String $par) {
        return STR(this.$record.get($par));
    }

    /**
     * Get DateTime value from the record.
     * @param $par Number of value.
     * @return String
     */
    public String getDate(String $par) {
        return STR(this.$record.get($par));
    }

    /**
     * Get integer value from the record.
     * @param $par Number of value.
     * @return Integer
     */
    public int getInt(String $par) {
        return INT(this.$record.get($par));
    }

    /**
     * Get real value from the record.
     * @param $par Number of value.
     * @return Float
     */
    public Float getFloat(String $par) {
        return FLOAT(this.$record.get($par));
    }

    /**
     * Close this record set.
     */
    public void close() {
        DataAccess.freeResult(this.$result);
    }
}

