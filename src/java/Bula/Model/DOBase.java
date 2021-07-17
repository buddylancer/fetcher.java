// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Model;
import Bula.Meta;

import Bula.Objects.TArrayList;
import Bula.Objects.TEnumerator;
import Bula.Objects.THashtable;
import Bula.Objects.Strings;

import Bula.Model.Connection;

/**
 * Base class for manipulating with DB objects.
 */
public class DOBase extends Meta {
    private Connection $dbConnection = null;

    /**
     * Name of a DB table.
     * @var String
     */
    protected String $tableName;

    /**
     * Name of a table ID field.
     * @var String
     */
    protected String $idField;

    /** Public constructor */
    public DOBase (Connection $connection) {
        this.$dbConnection = $connection;
    }

    /**
     * Get current connection.
     * @return Connection Current connection.
     */
    public Connection getConnection() {
        return this.$dbConnection;
    }

    /**
     * Get current ID field name.
     * @return String
     */
    public String getIdField() {
        return this.$idField;
    }

    /**
     * Get DataSet based on query and parameters (all records).
     * @param $query SQL-query to execute.
     * @param[] $pars Query parameters.
     * @return DataSet Resulting data set.
     */
    public DataSet getDataSet(String $query, Object[] $pars) {
        PreparedStatement $oStmt = this.$dbConnection.prepareStatement($query);
        if ($pars != null && SIZE($pars) > 0) {
            int $n = 1;
            for (int $i = 0; $i < SIZE($pars); $i += 2) {
                String $type = (String)$pars[$i];
                Object $value = $pars[$i+1];
                CALL($oStmt, $type, ARR($n, $value));
                $n++;
            }
        }
        RecordSet $oRs = $oStmt.executeQuery();
        if ($oRs == null) {
            $oStmt.close();
            return null;
        }

        DataSet $ds = new DataSet();
        while ($oRs.next() != 0) {
            $ds.addRow($oRs.$record);
        }
        $oRs.close();
        $oStmt.close();
        return $ds;
    }

    /**
     * Get DataSet based on query and parameters (only records of the list with rows length).
     * @param $query SQL-query to execute.
     * @param[] $pars Query parameters.
     * @param $list List number.
     * @param $rows Number of rows in a list.
     * @return DataSet Resulting data set.
     */
    public DataSet getDataSetList(String $query, Object[] $pars, int $list, int $rows) {
        if ($rows <= 0 || $list <= 0)
            return this.getDataSet($query, $pars);

        PreparedStatement $oStmt = this.$dbConnection.prepareStatement($query);
        if (SIZE($pars) > 0) {
            int $n = 1;
            for (int $p = 0; $p < SIZE($pars); $p += 2) {
                String $type = (String) $pars[$p];
                Object $value = $pars[$p+1];
                CALL($oStmt, $type, ARR($n, $value));
                $n++;
            }
        }
        RecordSet $oRs = $oStmt.executeQuery();
        if ($oRs == null)
            return null;

        DataSet $ds = new DataSet();
        int $totalRows = $oRs.getRows();
        $ds.setTotalPages(INT(($totalRows - 1) / $rows + 1));

        int $count = 0;
        if ($list != 1) {
            $count = ($list - 1) * $rows;
            while ($oRs.next() != 0) {
                $count--;
                if ($count == 0)
                    break;
            }
        }

        $count = 0;
        while ($oRs.next() != 0) {
            if ($count == $rows)
                break;
            $ds.addRow($oRs.$record);
            //$ds.setSize($ds.getSize() + 1);
            $count++;
        }

        $oRs.close();
        $oStmt.close();
        return $ds;
    }

    /**
     * Update database using $query and $parameters
     * @param $query SQL-query to execute.
     * @param[] $pars Query parameters.
     * @return int Update status.
     */
    protected int updateInternal(String $query, Object[] $pars) {
        return updateInternal($query, $pars, "update");}

    /**
     * Update database using $query and $parameters
     * @param $query SQL-query to execute.
     * @param[] $pars Query parameters.
     * @param $operation Operation - "update" (default) or "insert".
     * @return int Update status (or inserted ID for "insert" operation).
     */
    protected int updateInternal(String $query, Object[] $pars, Object $operation/* = "update"*/) {
        PreparedStatement $oStmt = this.$dbConnection.prepareStatement($query);
        if (SIZE($pars) > 0) {
            int $n = 1;
            for (int $i = 0; $i < SIZE($pars); $i += 2) {
                String $type = (String)$pars[$i];
                Object $value = $pars[$i+1];
                CALL($oStmt, $type, ARR($n, $value));
                $n++;
            }
        }
        int $ret = $oStmt.executeUpdate();
        if ($ret > 0 && EQ($operation, "insert"))
            $ret = $oStmt.getInsertId();
        $oStmt.close();
        return $ret;
    }

    /**
     * Get DataSet based on record ID.
     * @param $id Unique ID.
     * @return DataSet Resulting data set.
     */
    public DataSet getById(int $id) {
        String $query = Strings.concat(
            " select * from ", this.$tableName,
            " where ", this.$idField, " = ?"
        );
        Object[] $pars = ARR("setInt", $id);
        return this.getDataSet($query, $pars);
    }

    /**
     * Get DataSet containing IDs only.
     * @return DataSet Resulting data set.
     */
    public DataSet enumIds() {
        return enumIds(null, null); }

    /**
     * Get DataSet containing IDs only.
     * @param $where Where condition.
     * @return DataSet Resulting data set.
     */
    public DataSet enumIds(String $where) {
        return enumIds($where, null); }

    /**
     * Get DataSet containing IDs only.
     * @param $where Where condition [optional].
     * @param $order Field to order by [optional].
     * @return DataSet Resulting data set.
     */
    public DataSet enumIds(String $where/* = null*/, String $order/* = null*/) {
        String $query = Strings.concat(
            " select ", this.$idField, " from ", this.$tableName, " _this ",
            (BLANK($where) ? null : CAT(" where ", $where)),
            " order by ",
            (BLANK($order) ? this.$idField : $order)
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Get DataSet containing counter only.
     * @return DataSet Resulting data set.
     */
    public DataSet countIds() {
        return countIds(null); }

    /**
     * Get DataSet containing counter only.
     * @param $where Where condition [optional].
     * @return DataSet Resulting data set.
     */
    public DataSet countIds(String $where/* = null*/) {
        String $query = Strings.concat(
            " select count(", this.$idField, ") as i_Counter from ", this.$tableName, " _this ",
            (BLANK($where) ? null : CAT(" where ", $where))
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Get DataSet with all records enumerated.
     * @return DataSet Resulting data set.
     */
    public DataSet enumAll() { return enumAll(null, null); }

    /**
     * Get DataSet with all records enumerated.
     * @param $where Where condition.
     * @return DataSet Resulting data set.
     */
    public DataSet enumAll(String $where) { return enumAll($where, null); }

    /**
     * Get DataSet with all records enumerated.
     * @param $where Where condition [optional].
     * @param $order Field to order by [optional].
     * @return DataSet Resulting data set.
     */
    public DataSet enumAll(String $where/* = null*/, String $order/* = null*/) {
        String $query = Strings.concat(
            " select * from ", this.$tableName, " _this ",
            (BLANK($where) ? null : CAT(" where ", $where)),
            (BLANK($order) ? null : CAT(" order by ", $order))
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Get DataSet containing only required fields.
     * @param $fields Fields to include (divided by ',').
     * @return DataSet Resulting data set.
     */
    public DataSet enumFields(String $fields) {
        return enumFields($fields, null, null); }

    /**
     * Get DataSet containing only required fields.
     * @param $fields Fields to include (divided by ',').
     * @param $where Where condition [optional].
     * @return DataSet Resulting data set.
     */
    public DataSet enumFields(String $fields, String $where) {
        return enumFields($fields, $where, null); }

    /**
     * Get DataSet containing only required fields.
     * @param $fields Fields to include (divided by ',').
     * @param $where Where condition [optional].
     * @param $order Field to order by [optional].
     * @return DataSet Resulting data set.
     */
    public DataSet enumFields(String $fields, String $where/* = null*/, String $order/* = null*/) {
        String $query = Strings.concat(
            " select ", $fields, " from ", this.$tableName, " _this ",
            (BLANK($where) ? null : CAT(" where ", $where)),
            (BLANK($order) ? null : CAT(" order by ", $order))
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Get DataSet containing all fields.
     * @return DataSet Resulting data set.
     */
    public DataSet select() {
        return select(null, null, null); }

    /**
     * Get DataSet containing only required fields.
     * @param $fields Fields to include (divided by ',').
     * @return DataSet Resulting data set.
     */
    public DataSet select(String $fields) {
        return select($fields, null, null); }

    /**
     * Get DataSet containing only required fields.
     * @param $fields Fields to include (divided by ',').
     * @param $where Where condition.
     * @return DataSet Resulting data set.
     */
    public DataSet select(String $fields, String $where) {
        return select($fields, $where, null); }

    /**
     * Get DataSet containing only required fields or all fields [default].
     * @param $fields Fields to include (divided by ',').
     * @param $where Where condition [optional].
     * @param $order Field to order by [optional].
     * @return DataSet Resulting data set.
     */
    public DataSet select(String $fields/* = null*/, String $where/* = null*/, String $order/* = null*/) {
        if ($fields == null)
            $fields = "_this.*";

        String $query = Strings.concat(
            " select ", $fields,
            " from ", this.$tableName, " _this ",
            (BLANK($where) ? null : CAT(" where ", $where)),
            (BLANK($order) ? null : CAT(" order by ", $order))
        );
        Object[] $pars = ARR();
        return this.getDataSet($query, $pars);
    }

    /**
     * Get DataSet containing only the given list of rows.
     * @param $list List number.
     * @param $rows Number of rows in a list.
     * @return DataSet Resulting data set.
     */
    public DataSet selectList(int $list, int $rows) {
        return selectList($list, $rows, null, null, null); }

    /**
     * Get DataSet containing only the given list of rows (with required fields).
     * @param $list List number.
     * @param $rows Number of rows in a list.
     * @param $fields Fields to include (divided by ',').
     * @return DataSet Resulting data set.
     */
    public DataSet selectList(int $list, int $rows, String $fields) {
        return selectList($list, $rows, $fields, null, null); }

    /**
     * Get DataSet containing only the given list of rows (with required fields).
     * @param $list List number.
     * @param $rows Number of rows in a list.
     * @param $fields Fields to include (divided by ',').
     * @param $where Where condition [optional].
     * @return DataSet Resulting data set.
     */
    public DataSet selectList(int $list, int $rows, String $fields, String $where) {
        return selectList($list, $rows, $fields, $where, null); }

    /**
     * Get DataSet containing only the given list of rows (with required fields or all fields).
     * @param $list List number.
     * @param $rows Number of rows in a list.
     * @param $fields Fields to include (divided by ',').
     * @param $where Where condition [optional].
     * @param $order Field to order by [optional].
     * @return DataSet Resulting data set.
     */
    public DataSet selectList(int $list, int $rows, String $fields/* = null*/, String $where/* = null*/, String $order/* = null*/) {
        if ($fields == null)
            $fields = "_this.*";
        String $query = Strings.concat(
            " select ",  $fields,
            " from ", this.$tableName, " _this ",
            (BLANK($where) ? null : CAT(" where ", $where)),
            (BLANK($order) ? null : CAT(" order by ", $order))
        );

        Object[] $pars = ARR();
        DataSet $ds = this.getDataSetList($query, $pars, $list, $rows);
        return $ds;
    }

    /**
     * Delete record by ID.
     * @param $id Unique ID.
     * @return int Result of operation.
     */
    public int deleteById(int $id) {
        String $query = Strings.concat(
            " delete from ", this.$tableName,
            " where ", this.$idField, " = ?"
        );
        Object[] $pars = ARR("setInt", $id);
        return this.updateInternal($query, $pars, "update");
    }

    /**
     * Insert new record based on given fields.
     * @param $fields The set of fields.
     * @return int Result of SQL-query execution.
     */
    public int insert(THashtable $fields) {
        TEnumerator $keys = new TEnumerator($fields.keys());
        String $fieldNames = new String();
        String $fieldValues = new String();
        Object[] $pars = ARR();
        //$pars.setPullValues(true);
        int $n = 0;
        while ($keys.moveNext()) {
            String $key = (String)$keys.getCurrent();
            if ($n != 0) $fieldNames += ", ";
            if ($n != 0) $fieldValues += ", ";
            $fieldNames += $key;
            $fieldValues += "?";
            $pars = ADD($pars, this.setFunction($key), $fields.get($key));
            $n++;
        }
        String $query = Strings.concat(
            " insert into ", this.$tableName, " (", $fieldNames, ") ",
            " values (", $fieldValues, ")"
        );
        return this.updateInternal($query, $pars, "insert");
    }

    /**
     * Execute update query.
     * @param $setValues String with "set" clause.
     * @param $where String with "where" clause.
     * @return int Number of records updated.
     */
    public int update(String $setValues, String $where) {
        String $query = Strings.concat(
            " update ", this.$tableName, " _this set ", $setValues,
            " where (", $where, ")"
        );
        Object[] $pars = ARR();
        return this.updateInternal($query, $pars, "update");
    }

    /**
     * Update existing record by ID based on given fields.
     * @param $id Unique record ID.
     * @param $fields The set of fields.
     * @return int Result of SQL-query execution.
     */
    public int updateById(Object $id, THashtable $fields) {
        TEnumerator $keys = new TEnumerator($fields.keys());
        String $setValues = new String();
        Object[] $pars = ARR();
        int $n = 0;
        while ($keys.moveNext()) {
            String $key = (String)$keys.getCurrent();
            if ($key == this.$idField) //TODO PHP
                continue;
            if ($n != 0)
                $setValues += ", ";
            $setValues += CAT($key, " = ?");
            $pars = ADD($pars, this.setFunction($key), $fields.get($key));
            $n++;
        }
        $pars = ADD($pars, this.setFunction(this.$idField), $id);
        String $query = Strings.concat(
            " update ", this.$tableName, " set ", $setValues,
            " where (", this.$idField, " = ?)"
        );
        return this.updateInternal($query, $pars, "update");
    }

    /**
     * Map for setting parameters.
     * @param $key  Field name.
     * @return String Function name for setting that field.
     */
    private String setFunction(String $key) {
        String $prefix = $key.substring(0, 2);
        String $func = "setString";
        if ($prefix.equals("s_") || $prefix.equals("t_"))
            $func = "setString";
        else if ($prefix.equals("i_") || $prefix.equals("b_"))
            $func = "setInt";
        else if ($prefix.equals("f_"))
            $func = "setFloat";
        else if ($prefix.equals("d_"))
            $func = "setDate";
        return $func;
    }
}
