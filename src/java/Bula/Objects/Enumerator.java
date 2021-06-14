// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;
//SKIP cs

import java.util.Enumeration;

/**
 * Very simple implementation of Enumerator.
 */
public class Enumerator extends Meta {
    private Object[] $collection = null;
    private int $pointer = -1;

    private Enumeration $enumeration = null;
    private Object $current = null;

    public Enumerator  (Object[] $elements) { this.$collection = $elements; }

    public Enumerator (Enumeration $enumeration) { this.$enumeration = $enumeration; }

    public Boolean hasMoreElements() {
        if (this.$enumeration != null) return this.$enumeration.hasMoreElements();
        return (this.$pointer < SIZE(this.$collection) - 1);
    }

    public Object nextElement() {
        if (this.$enumeration != null) return this.$current = this.$enumeration.nextElement();
        return this.$current = (this.$pointer < SIZE(this.$collection) - 1) ? this.$collection[++this.$pointer] : null;
    }

    public Boolean moveNext() {
        return this.nextElement() != null;
    }

    public Object current() {
        return this.$current;
    }
}
