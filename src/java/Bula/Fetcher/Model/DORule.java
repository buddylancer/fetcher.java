// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Model;
import Bula.Meta;

import Bula.Model.DOBase;

/**
 * Manipulating with rules.
 */
public class DORule extends DOBase {
    /** Public constructor (overrides base constructor) */
    public DORule () {
        this.$tableName = "rules";
        this.$idField = "i_RuleId";
    }
}
