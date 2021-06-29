// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

/**
 * Implementation of DB NULL object.
 */
public class TNull extends Meta {
    private static TNull $value;

    private TNull () {
        $value = null;
    }

    /**
     * Get NULL value.
     * @return TNull NULL value.
     */
    public static TNull getValue() {
        if ($value == null)
            $value = new TNull();
        return $value;
    }
}
