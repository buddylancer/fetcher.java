// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import java.util.ArrayList;
import Bula.Objects.Enumerator;
import java.util.Hashtable;

/**
 * Helper class for manipulating with arrays.
 */
public class Arrays extends Meta {
    /** Create new array list. */
    public static ArrayList newArrayList() {
        return new ArrayList();
    }

    /**
     * Create new hash table.
     * @return Hashtable New hash table.
     */
    public static Hashtable newHashtable() {
        return new Hashtable();
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
     * @return Hashtable Merged hash table.
     */
    public static Hashtable mergeHashtable(Hashtable $input, Hashtable $extra) {
        if ($input == null)
            return null;
        if ($extra == null)
            return $input;

        Hashtable $output = (Hashtable)$input.clone();
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
     * @return ArrayList Resulting array list.
     */
    public static ArrayList mergeArrayList(ArrayList $input, ArrayList $extra) {
        if ($input == null)
            return null;
        if ($extra == null)
            return $input;

        ArrayList $output = newArrayList();
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
     * @return ArrayList Resulting array list.
     */
    public static ArrayList createArrayList(Object[] $input) {
        if ($input == null)
            return null;
        ArrayList $output = new ArrayList();
        if (SIZE($input) == 0)
            return $output;
        for (Object $obj : $input)
            $output.add($obj);
        return $output;
    }

}
