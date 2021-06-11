
package Bula.Fetcher.Model;
import Bula.Meta;

import Bula.Model.DOBase;

/**
 * Manipulating with times.
 */
public class DOTime extends DOBase {
    /** Public constructor (overrides base constructor) */
    public DOTime () {
        this.$tableName = "as_of_time";
        this.$idField = "i_Id";
    }
}
