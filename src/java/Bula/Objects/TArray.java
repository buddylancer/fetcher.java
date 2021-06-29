// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

/**
 * Straight-forward implementation of Array.
 */
public class TArray extends Meta {
    Object[] $content;

    /** Default constructor. */
    public TArray (int $size) {
        this.instantiate($size);
    }

    private void instantiate(int $size) {
        $content = new Object[$size];
    }

    public int size() {
        return $content.length;
    }

    public Boolean set(int $pos, Object $value) {
        if ($pos >= this.size())
            return false;
        $content[$pos] = $value;
        return true;
    }

    public Object get(int $pos) {
        if ($pos >= this.size())
            return false;
        return $content[$pos];
    }

    public void add(Object $value) {
        TArray $cloned = this.cloneMe();
        this.instantiate(this.size() + 1);
        for (int $n = 0; $n < $cloned.size(); $n++)
            this.set($n, $cloned.get($n));
        this.set($cloned.size() + 1, $value);
    }

    public TArray cloneMe() {
        TArray $cloned = new TArray(this.size());
        for (int $n = 0; $n < this.size(); $n++)
            $cloned.set($n, this.get($n));
        return $cloned;
    }

    public Object[] toArray() {
        return $content;
    }
}
