// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import java.util.*;

/**
 * Very simple implementation of TEnumerator.
 */
public class TEnumerator extends Meta {
    private Object[] $collection = null;
    private int $pointer = -1;

    private Enumeration $enumeration = null;
    private Object $current = null;

    public TEnumerator  (Object[] $elements) {
		this.$collection = $elements;
	}

    public TEnumerator (Enumeration $enumeration) {
		this.$enumeration = $enumeration;
	}
	
    public TEnumerator (Set $set) {
		ArrayList $list = new ArrayList();
		$list.addAll($set);
		Collections.sort($list);
		this.$collection = $list.toArray();
	}

    public Boolean moveNext() {
        if (this.$enumeration != null) {
            if (this.$enumeration.hasMoreElements()) {
                this.$current = this.$enumeration.nextElement();
                return true;
            }
            return false;
        }
        if (this.$pointer < SIZE(this.$collection) - 1) {
           this.$current = this.$collection[++this.$pointer];
           return true;
        }
        return false;
    }

    public Object getCurrent() {
        return this.$current;
    }
}
