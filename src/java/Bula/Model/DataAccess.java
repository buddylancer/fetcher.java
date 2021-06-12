// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2020-2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.


package Bula.Model;
import Bula.Meta;
import java.sql.*;

import java.util.Hashtable;
import java.util.Properties;
//SKIP cs

/**
 * Facade class for interfacing with mysql database.
 */
public class DataAccess extends Meta {
    private static Object $errorDelegate = "STOP";
    private static Object $printDelegate = null; // Set "PR" for debug, set null for release

    /**
     * Connect to the database.
     * @param $host Host name.
     * @param $admin Admin name.
     * @param $password Admin password.
     * @param $db Database name.
     * @param $port Port number.
     * @return Object Link to the database.
     */
    public static Object connect(String $host, String $admin, String $password, String $db, int $port) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception ex) {
            return null;
        }

        java.sql.Connection $oConn = null;
        String $url = "jdbc:mysql://" + $host + "/" + $db;

        Properties $connInfo = new Properties();
        $connInfo.put("user", $admin);
        $connInfo.put("password", $password);
        //if (LEN($db_charset) > 0) {
        //	//$connInfo.put("useUnicode", "true");
        //	$connInfo.put("characterEncoding", $db_charset);
        //	$connInfo.put("charSet", $db_charset);
        //}

        try {
            $oConn = DriverManager.getConnection($url, $connInfo);
        }
        catch (Exception $ex) {
            $oConn = null;
        }
        if ($oConn == null) {
            STOP("Can't open DB! Check whether it exists.");
        }
        return $oConn;
     }

    /**
     * Close the connection to the database.
     * @param $link Link to the database.
     */
    public static void close(Object $link) {
        java.sql.Connection $oConn = (java.sql.Connection)$link;
		if ($oConn != null) {
			try {
				$oConn.close();
			}
			catch (Exception $ex) {
				String $s = $ex.toString();
				// TODO
			}
			$oConn = null;
		}
    }

    /**
     * Execute query.
     * @param $link Link to the database.
     * @param $input SQL-query to execute.
     * @return Object Result of query execution.
     */
    public static Object selectQuery(Object $link, String $input) {
        java.sql.Connection $oConn = (java.sql.Connection)$link;
        ResultSet $oRs = null;
        ResultSetMetaData $md = null; //Java
        try {
    		Statement $oStmt = $oConn.createStatement();
            $oRs = $oStmt.executeQuery($input);
            if ($oRs == null)
                return null;
        }
        catch (Exception ex) {
            return null;
        }
        return $oRs;
    }
    
    private static int $affected_rows = 0;
    private static int $generated_id = 0;
    public static Object updateQuery(Object $link, String $input) {
        //return mysqli_query($link, $input);
        java.sql.Connection $oConn = (java.sql.Connection)$link;
        try {
    		Statement $oStmt = $oConn.createStatement();
            $affected_rows = $oStmt.executeUpdate($input, Statement.RETURN_GENERATED_KEYS);
            ResultSet $oRs = $oStmt.getGeneratedKeys();
            $generated_id = $oRs.next() ? $oRs.getInt(1) : 0;
        }
        catch (Exception ex) {
            return null;
        }
        return $affected_rows;
    }
    
    public static Object nonQuery(Object $link, String $input) {
        //return mysqli_query($link, $input);
        java.sql.Connection $oConn = (java.sql.Connection)$link;
        try {
    		Statement $oStmt = $oConn.createStatement();
            return $oStmt.execute($input);
        }
        catch (Exception ex) {
            //TODO
            return false;
        }
     }

    /**
     * Get number of rows affected by last query.
     * @param $link Link to the database.
     * @return Integer
     */
    public static int affectedRows(Object $link) {
        //return mysqli_affected_rows($link);
        return $affected_rows; //TODO
    }

    /**
     * Get unique ID for last inserted record.
     * @param $link Link to the database.
     * @return Integer
     */
    public static int insertId(Object $link) {
        //return mysqli_insert_id($link);
        return $generated_id; //TODO
    }

    /**
     * Get number of resulting rows for last query.
     * @return Object Result of query execution.
     * @return Integer
     */
    public static int numRows(Object $result) {
        ResultSet $oRs = (ResultSet)$result;
        int $rows_count = 0;
        try {
            $oRs.last();
            $rows_count = $oRs.getRow();
            $oRs.beforeFirst();
        }
        catch (Exception ex) {
            return -1;
        }
        return $rows_count;
     }

    /**
     * Get next row (as Hashtable) for last query.
     * @param Object Result of query execution.
     * @return Hashtable Next row or null.
     */
    public static Hashtable fetchArray(Object $result) {
        ResultSet $oRs = (ResultSet)$result;
        Hashtable $row = new Hashtable();
        try {
            ResultSetMetaData $md = $oRs.getMetaData(); //Java
            if (!$oRs.next())
                return null;
            for (Integer $n = 0; $n < $md.getColumnCount(); $n++) {
                Object $obj = $oRs.getObject($n + 1);
                if (!NUL($obj))
                    $row.put($md.getColumnLabel($n + 1), $obj);
            }
        }
        catch (Exception ex) {
            return null;
        }
        return $row;
    }

    /**
     * Free last query result.
     * @return Object Result of query execution.
     */
    public static void freeResult(Object $result) {
        ResultSet $oRs = (ResultSet)$result;
        try {
            $oRs.close();
        }
        catch (Exception ex) {
            return; //TODO
        }
    }

    /**
     * Set for error printing.
     * @param $delegateFunction Function delegate.
     */
    public static void setErrorDelegate(Object $delegateFunction) {
        $errorDelegate = $delegateFunction;
    }

    /**
     * Set for debug printing.
     * @param $delegateFunction Function delegate.
     */
    public static void setPrintDelegate(Object $delegateFunction) {
        $printDelegate = $delegateFunction;
    }

    /**
     * Call delegate for error printing.
     * @param $input Error message.
     */
    public static void callErrorDelegate(String $input) {
        if ($errorDelegate == null)
            return;
        
        //call_user_func_array($errorDelegate, array($input));
        try {
            java.lang.reflect.Method method = Bula.Meta.class.getMethod((String)$errorDelegate, new Class[] { String.class });
            method.invoke(null, new Object[] { $input });
        }
        catch (Exception ex) {
            return; //TODO
        }
    }

    /**
     * Call delegate for debug printing.
     * @param $input Debug message.
     */
    public static void callPrintDelegate(String $input) {
        if ($printDelegate == null)
            return;
        
        //call_user_func_array($printDelegate, array($input));
        try {
            java.lang.reflect.Method method = Bula.Meta.class.getMethod((String)$printDelegate, new Class[] { String.class });
            method.invoke(null, new Object[] { $input });
        }
        catch (Exception ex) {
            return; //TODO
        }
    }
}
