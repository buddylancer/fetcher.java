// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Model;
import Bula.Meta;

import Bula.Model.DBConfig;
import Bula.Model.PreparedStatement;

/**
 * Implement operations with connection to the database.
 */
public class Connection extends Meta {
    private Object $link = null;
    private PreparedStatement $stmt; // Prepared statement to import with connection

    // Create connection to the database given parameters from DBConfig.
    public static Connection createConnection() {
        Connection $oConn = new Connection();
        String $dbAdmin = DBConfig.DB_ADMIN != null ? DBConfig.DB_ADMIN : DBConfig.DB_NAME;
        String $dbPassword = DBConfig.DB_PASSWORD != null ? DBConfig.DB_PASSWORD : DBConfig.DB_NAME;
        int $ret = 0;
        if (DBConfig.DB_CHARSET != null)
            $ret = $oConn.open(DBConfig.DB_HOST, DBConfig.DB_PORT, $dbAdmin, $dbPassword, DBConfig.DB_NAME, DBConfig.DB_CHARSET);
        else
            $ret = $oConn.open(DBConfig.DB_HOST, DBConfig.DB_PORT, $dbAdmin, $dbPassword, DBConfig.DB_NAME);
        if ($ret == -1)
            $oConn = null;
        return $oConn;
    }

    /**
     * Open connection to the database.
     * @param $host Host name.
     * @param $port Port number.
     * @param $admin Admin name.
     * @param $password Admin password.
     * @param $db DB name.
     * @return int Result of operation (1 - OK, -1 - error).
     */
    public int open(String $host, int $port, String $admin, String $password, String $db) {
        return open($host, $port, $admin, $password, $db, null); }

    /**
     * Open connection to the database.
     * @param $host Host name.
     * @param $port Port number.
     * @param $admin Admin name.
     * @param $password Admin password.
     * @param $db DB name.
     * @param $charset DB charset.
     * @return int Result of operation (1 - OK, -1 - error).
     */
    public int open(String $host, int $port, String $admin, String $password, String $db, String $charset /*= null*/) {
        this.$link = DataAccess.connect($host, $admin, $password, $db, $port); //TODO PHP
        if (this.$link == null /*|| this.$link == false*/) {
            DataAccess.callErrorDelegate("Can't open DB! Check whether it exists!");
            return -1;
        }
        if ($charset != null)
            DataAccess.nonQuery(this.$link, CAT("set names ", $charset));
        return 1;
    }

    /**
     * Close connection to the database.
     */
    public void close() {
        DataAccess.close(this.$link);
        this.$link = null;
    }

    /**
     * Prepare statement.
     * @param $sql SQL-query.
     * @return Prepared statement.
     */
    public PreparedStatement prepareStatement(String $sql) {
        this.$stmt = new PreparedStatement();
        this.$stmt.setLink(this.$link);
        this.$stmt.setSql($sql);
        return this.$stmt;
    }
}
