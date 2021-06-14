
package Bula.Model;
import Bula.Meta;

import Bula.Model.PreparedStatement;

/**
 * Implement operations with connection to the database.
 */
public class Connection extends Meta {
    private Object $link = null;
    private PreparedStatement $stmt; // Prepared statement to import with connection

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
