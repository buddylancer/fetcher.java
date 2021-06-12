
package Bula.Model;
import Bula.Meta;

import java.util.ArrayList;
import Bula.Objects.Enumerator;
import java.util.Hashtable;

/**
 * Non-typed data set implementation.
 */
public class DataSet extends Meta {
    private ArrayList $rows;
    private int $pageSize;
    private int $totalPages;

    /** Default public constructor */
    public DataSet () {
        this.$rows = new ArrayList();
        this.$pageSize = 10;
        this.$totalPages = 0;
    }

    /**
     * Get the size (number of rows) of the DataSet.
     * @return Integer DataSet size.
     */
    public int getSize() {
        return this.$rows.size();
    }

    /**
     * Get a row from the DataSet.
     * @param $n Number of the row.
     * @return Hashtable Required row or null.
     */
    public Hashtable getRow(int $n) {
        return (Hashtable) this.$rows.get($n);
    }

    /**
     * Add new row into the DataSet.
     * @param $row New row to add.
     */
    public void addRow(Hashtable $row) {
        this.$rows.add($row);
    }

    /**
     * Get page size of the DataSet.
     * @return Integer Current page size.
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
     * @return Integer Number of pages.
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
    public String toXml() {
        int $level = 0;
        String $spaces = null;
        String $output = new String();
        $output += CAT("<DataSet Rows=\"", this.$rows.size(), "\">\n");
        for (int $n = 0; $n < this.getSize(); $n++) {
            Hashtable $row = this.getRow($n);
            $level++; $spaces = this.addSpaces($level);
            $output += CAT($spaces, "<Row>\n");
            Enumerator $keys = 
                    new Enumerator($row.keys());
            while ($keys.hasMoreElements()) {
                $level++; $spaces = this.addSpaces($level);
                String $key = (String)$keys.nextElement();
                $output += CAT($spaces, "<Item Name=\"", $key, "\">");
                $output += STR($row.get($key));
                $output += "</Item>\n";
                $level--; $spaces = this.addSpaces($level);
            }
            $output += CAT($spaces, "</Row>\n");
            $level--; $spaces = this.addSpaces($level);
        }
        $output += "</DataSet>\n";
        return $output;
    }
}
