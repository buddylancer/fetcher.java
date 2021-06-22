// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import Bula.Objects.TArrayList;
import Bula.Objects.TEnumerator;
import Bula.Objects.THashtable;
import Bula.Objects.TNull;

/**
 * Helper class for manipulating with arrays.
 */
public class Arrays extends Meta {
    /**
     * Create new empty array.
     */
    public static Object[] newArray() {
        return newArray(0); }

    /**
     * Create new array of objects.
     * @param $size Size of array.
     * @return Object[] Resulting array.
     */
    public static Object[] newArray(int $size) {
        return new Object[$size];
    }

    /**
     * Extend array with additional element.
     * @param $input Original array.
     * @param $element Object to add to original array.
     * @return Array Resulting array.
     */
    public static Object[] extendArray(Object[] $input, Object $element) {
        if ($input == null)
            return null;
        if ($element == null)
            return $input;

        int $inputSize = SIZE($input);
        int $newSize = $inputSize + 1;
        Object[] $output = newArray($newSize);
        for (int $n = 0; $n < $inputSize; $n++)
            $output[$n] = $input[$n];
        $output[$inputSize] = $element;
        return $output;
    }

}
