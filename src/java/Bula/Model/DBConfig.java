// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Model;
import Bula.Meta;

/**
 * Set info for database connection here.
 */
public class DBConfig extends Meta {
    /** Database host */
    public static final String DB_HOST = "localhost";
    /** Database name */
    public static final String DB_NAME = "dbusnews";
    /** Database administrator name (if null - DB_NAME will be used) */
    public static final String DB_ADMIN = null;
    /** Database password  (if null - DB_NAME will be used) */
    public static final String DB_PASSWORD = null;
    /** Database character set */
    public static final String DB_CHARSET = "utf8";
    /** Database port */
    public static final int DB_PORT = 3306;
    /** Date/time format used for DB operations */
    public static final String SQL_DTS = "yyyy-MM-dd HH:mm:ss";
}
