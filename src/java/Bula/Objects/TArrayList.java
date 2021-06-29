// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

/**
 * Straight-forward implementation of ArrayList.
 */
public class TArrayList extends TArrayListBase {
    /**
     * Public constructors.
     */
    public TArrayList() { super(); }
    public TArrayList(Object[] $items) { super($items); }

    /** Create new array list. */
    public static TArrayList create() {
        return new TArrayList();
    }

    /**
     * Add multiple objects.
     * @param[] $inputs Array of objects.
     * @return int Number of added objects,
     */
    public int addAll(Object[] $inputs) {
        int $counter = 0;
        for (Object $input : $inputs) {
            this.add($input);
            $counter++;
        }
        return $counter;
    }

    /**
     * Create array list from array of objects.
     * @param[] $input Array of objects.
     * @return TArrayList Resulting array list.
     */
    public static TArrayList createFrom(Object[] $input) {
        if ($input == null)
            return null;
        TArrayList $output = create();
        if (SIZE($input) == 0)
            return $output;
        for (Object $obj : $input)
            $output.add($obj);
        return $output;
    }

    /**
     * Merge array lists.
     * @param $input Original array list.
     * @param $extra Array list to merge with original one.
     * @return TArrayList Resulting array list.
     */
    public TArrayList merge(TArrayList $extra) {
        TArrayList $output = create();
        for (int $n1 = 0; $n1 < this.size(); $n1++)
            $output.add(this.get($n1));
        if ($extra == null)
            return $output;
        for (int $n2 = 0; $n2 < $extra.size(); $n2++)
            $output.add($extra.get($n2));
        return $output;
    }

}
