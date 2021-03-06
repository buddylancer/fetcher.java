// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Model;
import Bula.Meta;

import Bula.Objects.TArrayList;
import Bula.Objects.TEnumerator;
import Bula.Objects.THashtable;

/**
 * Non-typed data set implementation.
 */
public class DataSet extends Meta {
    private TArrayList $rows;
    private int $pageSize;
    private int $totalPages;

    /** Default public constructor */
    public DataSet () {
        this.$rows = new TArrayList();
        this.$pageSize = 10;
        this.$totalPages = 0;
    }

    /**
     * Get the size (number of rows) of the DataSet.
     * @return int DataSet size.
     */
    public int getSize() {
        return this.$rows.size();
    }

    /**
     * Get a row from the DataSet.
     * @param $n Number of the row.
     * @return THashtable Required row or null.
     */
    public THashtable getRow(int $n) {
        return (THashtable) this.$rows.get($n);
    }

    /**
     * Add new row into the DataSet.
     * @param $row New row to add.
     */
    public void addRow(THashtable $row) {
        this.$rows.add($row);
    }

    /**
     * Get page size of the DataSet.
     * @return int Current page size.
     */
    public int getPageSize() {
        return this.$pageSize;
    }

    /**
     * Set page size of the DataSet.
     * @param pageSize Current page size.
     */
    public void setPageSize(int $pageSize) {
        this.$pageSize = $pageSize;
    }

    /**
     * Get total number of pages in the DataSet.
     * @return int Number of pages.
     */
    public int getTotalPages() {
        return this.$totalPages;
    }

    /**
     * Set total number of pages in the DataSet.
     * @param $totalPages Number of pages.
     */
    public void setTotalPages(int $totalPages) {
        this.$totalPages = $totalPages;
    }

    private String addSpaces(int $level) {
        String $spaces = new String();
        for (int $n = 0; $n < $level; $n++)
            $spaces += "    ";
        return $spaces;
    }

    /**
     * Get serialized (XML) representation of the DataSet.
     * @return String Resulting representation.
     */
    public String toXml(String $EOL) {
        int $level = 0;
        String $spaces = null;
        String $output = new String();
        $output += CAT("<DataSet Rows=\"", this.$rows.size(), "\">", $EOL);
        for (int $n = 0; $n < this.getSize(); $n++) {
            THashtable $row = this.getRow($n);
            $level++; $spaces = this.addSpaces($level);
            $output += CAT($spaces, "<Row>", $EOL);
            TEnumerator $keys = new TEnumerator($row.keys());
            while ($keys.moveNext()) {
                $level++; $spaces = this.addSpaces($level);
                String $key = (String)$keys.getCurrent();
                Object $value = $row.get($key);
                if (NUL($value)) {
                    $output += CAT($spaces, "<Item Name=\"", $key, "\" IsNull=\"True\" />", EOL);
                }
                else {
                    $output += CAT($spaces, "<Item Name=\"", $key, "\">");
                    $output += STR($row.get($key));
                    $output += CAT("</Item>", $EOL);
                }
                $level--; $spaces = this.addSpaces($level);
            }
            $output += CAT($spaces, "</Row>", $EOL);
            $level--; $spaces = this.addSpaces($level);
        }
        $output += CAT("</DataSet>", $EOL);
        return $output;
    }
}
