// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2020 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

// Note: this class is not ported (is fully specific for NET-version).

package Bula;

import java.util.*;
import Bula.Objects.*;

/// <summary>
/// Meta functions, that can be replaced when converting to other language (Java, C#).
/// </summary>
public class Meta {
    //public Meta(Object $dummy) {}
    
    public static final String DIV = "|";
    public static final String EOL = "\r\n";
    
    /// <summary>
    /// Stop executiong.
    /// </summary>
    /// <param name="str">Error message</param>
    public static void STOP(Object str) {
        Response.write(str.toString());
        //System.Web.HttpContext.Current.Response.End();
    }

    // Common functions

    /// <summary>
    /// Check whether an object is null.
    /// </summary>
    /// <param name="value">Input object</param>
    /// <returns></returns>
    public static boolean NUL(Object value) {
        return value == null;
    }

    /// <summary>
    /// Get integer value of any object.
    /// </summary>
    /// <param name="value">Input object</param>
    /// <returns>Integer result</returns>
    public static int INT(Object value) {
        if (NUL(value))
            return 0;
        if (value instanceof String)
           return Integer.parseInt((String)value);
        return Integer.parseInt(value.toString());
    }

    /// <summary>
    /// Get float value of any object.
    /// </summary>
    /// <param name="value">Input object</param>
    /// <returns>Float result</returns>
    public static float FLOAT(Object value)
    {
        if (NUL(value))
            return 0;
        if (value instanceof String)
            return Float.parseFloat((String)value);
        return Float.parseFloat(value.toString());
    }

    /// <summary>
    /// Get string value of any object.
    /// </summary>
    /// <param name="value">Input object</param>
    /// <returns>String result</returns>
    public static String STR(Object value)
    {
        if (NUL(value))
            return ""; //null; //TODO
        if (value instanceof String)
            return (String)value;
        return value.toString();
    }

    /// <summary>
    /// Check whether 2 object are equal.
    /// </summary>
    /// <param name="value1">First object</param>
    /// <param name="value2">Second object</param>
    /// <returns></returns>
    public static boolean EQ(Object value1, Object value2) {
        if (value1 == null || value2 == null)
            return false;
        return value1.toString().equals(value2.toString());
    }

    // String functions

    /// <summary>
    /// Check whether an object is empty.
    /// </summary>
    /// <param name="arg">Input object</param>
    /// <returns></returns>
    public static boolean BLANK(Object arg) {
        if (arg == null)
            return true;
        if (arg instanceof String)
            return ((String)arg).length() == 0;
        return arg.toString().isEmpty();
    }

    /// <summary>
    /// Get the length of an object (processed as string).
    /// </summary>
    /// <param name="str">Input object</param>
    /// <returns>Length of resulting string</returns>
    public static int LEN(Object str) {
        return BLANK(str) ? 0 : str.toString().length();
    }

    /// <summary>
    /// Concatenate any number of objects as string.
    /// </summary>
    /// <param name="args">Array of objects</param>
    /// <returns>Resulting string</returns>
    public static String CAT(Object... args) {
        String result = "";
        for (Object arg : args) {
            if (BLANK(arg))
                continue;
            result += STR(arg);
        }
        return result;
    }

    /// <summary>
    /// Get index of first substring occurence.
    /// </summary>
    /// <param name="str">Input string to search in</param>
    /// <param name="what">Substring to search for</param>
    /// <param name="off">Optional offset from input string beginning</param>
    /// <returns>Index of the substring (or -1)</returns>
    public static int IXOF(String str, String what, int off) {
        return str.indexOf(what, off);
    }

    /// <summary>
    /// Instantiate array of objects.
    /// </summary>
    /// <param name="args">Variable length array of parameters</param>
    /// <returns>Resulting array</returns>
    public static Object[] ARR(Object... args) {
        return (Object[])args.clone();
    }

    /// <summary>
    /// Instantiate empty array of required size.
    /// </summary>
    /// <param name="size">Size of array</param>
    /// <returns>Resulting empty array</returns>
    public static Object[] ARR(int size)
    {
        return new Object[size];
    }

    /// <summary>
    /// Merge arrays.
    /// </summary>
    /// <param name="input">Input array</param>
    /// <param name="args">Variable length array of parameters</param>
    /// <returns>Merged array</returns>
    public static Object[] ADD(Object[] input, Object... args) {
        Object[] output = new Object[input.length + args.length];
        //input.copyTo(output, 0);
        for (int n = 0; n < input.length; n++)
            output[n] = input[n];
        for (int n = 0; n < args.length; n++)
            output[input.length + n] = args[n];
        return output;
    }

    /// <summary>
    /// Identify the size of any object.
    /// </summary>
    /// <param name="val">Input object</param>
    /// <returns>Resulting size</returns>
    public static int SIZE(Object val) {
        if (val == null) return 0;
        else if (val instanceof Object[]) return ((Object[])val).length;
        else if (val instanceof ArrayList) return ((ArrayList)val).size();
        else if (val instanceof Hashtable) return ((Hashtable)val).size();
        else if (val instanceof String) return ((String)val).length();
        return 0;
    }

    /// <summary>
    /// Call obj.method(args) and return its result.
    /// </summary>
    /// <param name="obj">Object instance</param>
    /// <param name="method">Method to call</param>
    /// <param name="args">Arguments</param>
    /// <returns>Result of method calling</returns>
    public Object CALL(Object $obj, String $method, Object[] $args) {
        try {
            Class[] $types = new Class[$args.length];
            for (int $n = 0; $n < $args.length; $n++) {
                if ($args[$n] instanceof Integer)
                    $types[$n] = int.class;
                else
                    $types[$n] = $args[$n].getClass();
            }
            java.lang.reflect.Method methodInfo = $obj.getClass().getMethod($method, $types);
            return methodInfo.invoke($obj, $args);
        }
        catch (Exception ex) {
            //TODO 
            return null;
        }
    }
}

