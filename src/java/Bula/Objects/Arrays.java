// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import Bula.Objects.DataList;
import Bula.Objects.Enumerator;
import Bula.Objects.DataRange;

/**
 * Helper class for manipulating with arrays.
 */
public class Arrays extends Meta {
    /** Create new array list. */
    public static DataList newDataList() {
        return new DataList();
    }

    /**
     * Create new hash table.
     * @return DataRange New hash table.
     */
    public static DataRange newDataRange() {
        return new DataRange();
    }

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
     * Merge hash tables.
     * @param $input Original hash table.
     * @param $extra Hash table to merge with original one.
     * @return DataRange Merged hash table.
     */
    public static DataRange mergeDataRange(DataRange $input, DataRange $extra) {
        if ($input == null)
            return null;
        if ($extra == null)
            return $input;

        DataRange $output = (DataRange)$input.clone();
        Enumerator $keys =
            new Enumerator($extra.keys());
        while ($keys.hasMoreElements()) {
            String $key = (String)$keys.nextElement();
            $output.put($key, $extra.get($key));
        }
        return $output;
    }

    /**
     * Merge array lists.
     * @param $input Original array list.
     * @param $extra Array list to merge with original one.
     * @return DataList Resulting array list.
     */
    public static DataList mergeDataList(DataList $input, DataList $extra) {
        if ($input == null)
            return null;
        if ($extra == null)
            return $input;

        DataList $output = newDataList();
        for (int $n = 0; $n < SIZE($input); $n++)
            $output.add($input.get($n));
        for (int $n = 0; $n < SIZE($extra); $n++)
            $output.add($extra.get($n));
        return $output;
    }

    /**
     * Merge arrays.
     * @param $input Original array.
     * @param $extra Array to merge with original one.
     * @return Array Resulting array.
     */
    public static Object[] mergeArray(Object[] $input, Object[] $extra) {
        if ($input == null)
            return null;
        if ($extra == null)
            return $input;

        int $inputSize = SIZE($input);
        int $extraSize = SIZE($extra);
        int $newSize = $inputSize + $extraSize;
        Object[] $output = newArray($newSize);
        for (int $n = 0; $n < $inputSize; $n++)
            $output[$n] = $input[$n];
        for (int $n = 0; $n < $extraSize; $n++)
            $output[$inputSize + $n] = $extra[$n];
        return $output;
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

    /**
     * Create array list from array of objects.
     * @param[] $input Array of objects.
     * @return DataList Resulting array list.
     */
    public static DataList createDataList(Object[] $input) {
        if ($input == null)
            return null;
        DataList $output = new DataList();
        if (SIZE($input) == 0)
            return $output;
        for (Object $obj : $input)
            $output.add($obj);
        return $output;
    }

}
