// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Model;
import Bula.Meta;

import Bula.Model.Connection;
import Bula.Model.DOBase;

/**
 * Manipulating with times.
 */
public class DOTime extends DOBase {
    /** Public constructor (overrides base constructor) */
    public DOTime (Connection $connection) {
        super($connection);
        this.$tableName = "as_of_time";
        this.$idField = "i_Id";
    }
}
