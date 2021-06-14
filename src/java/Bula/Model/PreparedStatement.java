
package Bula.Model;
import Bula.Meta;

import java.util.ArrayList;

import Bula.Objects.Response;
import Bula.Objects.DateTimes;
import Bula.Objects.Strings;

/**
 * Implement operations with prepared statement.
 */
public class PreparedStatement extends Meta {
    /** Link to database instance */
    private Object $link;
    /** Initial SQL-query */
    private String $sql;
    /** List of parameters */
    private ArrayList $pars;
    /** Formed (prepared) SQL-query */
    private String $query;

    /**
     * Resulting record set of the last operation.
     * @var RecordSet
     */
    public RecordSet $recordSet;

    /** Default public constructor */
    public PreparedStatement () {
        this.$pars = new ArrayList();
        this.$pars.add("dummy"); // Parameter number will start from 1.
    }

    /**
     * Execute selection query.
     * @return RecordSet
     */
    public RecordSet executeQuery() {
        this.$recordSet = new RecordSet();
        if (this.formQuery()) {
            DataAccess.callPrintDelegate(CAT("Executing selection query [", this.$query, "] ..."));
            Object $result = DataAccess.selectQuery(this.$link, this.$query);
            if ($result == null /*|| $result == false*/) {
                DataAccess.callErrorDelegate(CAT("Selection query failed [", this.$query, "]"));
                return null;
            }
            this.$recordSet.$result = $result;
            this.$recordSet.setRows(DataAccess.numRows($result));
            this.$recordSet.setPage(1);
            return this.$recordSet;
        }
        else {
            DataAccess.callErrorDelegate(CAT("Error in query: ", this.$query, "<hr/>"));
            return null;
        }
    }

    /**
     * Execute updating query.
     * @return Integer
     *   -1 - error during form query.
     *   -2 - error during execution.
     */
    public int executeUpdate() {
        if (this.formQuery()) {
            DataAccess.callPrintDelegate(CAT("Executing update query [", this.$query, "] ..."));
            Object $result = DataAccess.updateQuery(this.$link, this.$query);
            if ($result == null) {
                DataAccess.callErrorDelegate(CAT("Query update failed [", this.$query, "]"));
                return -2;
            }
            int $ret = DataAccess.affectedRows(this.$link);
            return $ret;
        }
        else {
            DataAccess.callErrorDelegate(CAT("Error in update query [", this.$query, "]"));
            return -1;
        }
    }

    /**
     * Get ID for just inserted record.
     * @return Integer
     */
    public int getInsertId() {
        return DataAccess.insertId(this.$link);
    }

    /**
     * Form query (replace '?' marks with real parameters).
     * @return Boolean
     */
    private Boolean formQuery() {
        int $questionIndex = -1;
        int $startFrom = 0;
        int $n = 1;
        String $str = new String(this.$sql);
        while (($questionIndex = $str.indexOf("?", $startFrom)) != -1) {
            String $value = (String)this.$pars.get($n);
            String $before = $str.substring(0, $questionIndex);
            String $after = $str.substring($questionIndex + 1);
            $str = $before; $str += $value; $startFrom = $str.length();
            $str += $after;
            $n++;
        }
        this.$query = $str;
        return true;
    }

    // Set parameter value
    private void setValue(int $n, String $val) {
        if ($n >= SIZE(this.$pars))
            this.$pars.add($val);
        else
            this.$pars.set($n, $val);
    }

    /**
     * Set int parameter.
     * @param $n Parameter number.
     * @param $val Parameter value.
     */
    public void setInt(int $n, int $val) {
        setValue($n, CAT($val));
    }

    /**
     * Set String parameter.
     * @param $n Parameter number.
     * @param $val Parameter value.
     */
    public void setString(int $n, String $val) {
        setValue($n, CAT("'", Strings.addSlashes($val), "'"));
    }

    /**
     * Set DateTime parameter.
     * @param $n Parameter number.
     * @param $val Parameter value.
     */
    public void setDate(int $n, String $val) {
        setValue($n, CAT("'", DateTimes.format(DateTimes.SQL_DTS, DateTimes.getTime($val)), "'"));
    }

    /**
     * Set Float parameter.
     * @param $n Parameter number.
     * @param $val Parameter value.
     */
    public void setFloat(int $n, Double $val) {
        setValue($n, CAT($val));
    }

    /**
     * Close.
     */
    public void close() {
        this.$link = null;
    }

    /**
     * Set DB link.
     * @param $link
     */
    public void setLink(Object $link) {
        this.$link = $link;
    }

    /**
     * Set SQL-query,
     * @param $sql
     */
    public void setSql(String $sql) {
        this.$sql = $sql;
    }
}
