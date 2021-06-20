// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import Bula.Objects.TEnumerator;

/**
 * Straight-forward implementation of Java THashtable object.
 */
public class THashtable extends THashtableBase {
    public THashtable () {
    }

    /**
     * Create new hash table.
     * @return THashtable New hash table.
     */
    public static THashtable create() {
        return new THashtable();
    }

    /**
     * Merge hash tables.
     * @param $extra Hash table to merge with original one.
     * @return THashtable Merged hash table.
     */
    public THashtable merge(THashtable $extra) {
        if ($extra == null)
            return this;

        THashtable $output = create();

        TEnumerator $keys1 = new TEnumerator(this.keys());
        while ($keys1.moveNext()) {
            String $key1 = (String)$keys1.getCurrent();
            $output.put($key1, this.get($key1));
        }

        TEnumerator $keys2 = new TEnumerator($extra.keys());
        while ($keys2.moveNext()) {
            String $key2 = (String)$keys2.getCurrent();
            $output.put($key2, $extra.get($key2));
        }
        return $output;
    }

}
