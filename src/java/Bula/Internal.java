// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

// Note: this class is not ported (is fully specific for NET-version).

package Bula;

import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;
import com.apptastic.rssreader.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamReader;

import Bula.Fetcher.Config;
import Bula.Objects.*;

/// <summary>
/// Various operations specific to C# version.
/// </summary>
public class Internal extends Bula.Meta {

    /// <summary>
    /// Remove tags from a string.
    /// </summary>
    /// <param name="input">Input string</param>
    /// <param name="except">Allowed tags</param>
    /// <returns>Resulting string</returns>
    public static String removeTags(String input, String except)
    {
        //Boolean has_open = java.util.regex.isMatch(input, "<[a-z]+[^>]*>");
        //Boolean has_close = Regex.IsMatch(input, "</[a-z]+>");
        //Boolean has_twin = Regex.IsMatch(input, "<[a-z]+/>");
        Boolean has_open = Pattern.compile("<[a-z]+[^>]*>").matcher(input).find();
        Boolean has_close = Pattern.compile("</[a-z]+>").matcher(input).find();
        Boolean has_twin = Pattern.compile("<[a-z]+/>").matcher(input).find();

        if (!has_open && !has_close && !has_twin)
            return input;

        if (except == null)
            return removeTag(input, "[a-z]+");

        String output = input;
        output = decorateTags(output, except);
        output = removeTags(output, null);
        output = undecorateTags(output);
        return output;
    }

    private static String removeTag(String input, String tag)
    {
        //return Regex.Replace(input, CAT("<[/]*", tag, "[^>]*[/]*>"), "");
        return Pattern.compile(CAT("<[/]*", tag, "[^>]*[/]*>")).matcher(input).replaceAll(" ");
    }

    private static String decorateTags(String $input, String $except)
    {
        //String[] $chunks = Regex.Replace(except, "[/]*>", "").Split(new char[] {'<'});
        String[] $chunks = Pattern.compile("[/]*>").matcher($except).replaceAll("").split("<");
        String $output = $input;
        for (String $chunk : $chunks)
        {
            if ($chunk.length() != 0)
                $output = decorateTag($output, $chunk);
        }
        return $output;
    }

    private static String decorateTag(String input, String tag)
    {
        //return Regex.Replace(input, CAT("<([/]*", tag, "[^>]*[/]*)>"), "~{$1}~");
        return Pattern.compile(CAT("<([/]*", tag, "[^>]*[/]*)>")).matcher(input).replaceAll("~{$1}~");
    }

    private static String undecorateTags(String input)
    {
        //return Regex.Replace(input, CAT("~{([/]*[^}]+)}~"), "<$1>");
        return Pattern.compile(CAT("~\\{([/]*[^}]+)\\}~")).matcher(input).replaceAll("<$1>");
    }

    /// <summary>
    /// Call method of given class using provided arguments.
    /// </summary>
    /// <param name="class_name">Class name</param>
    /// <param name="method_name">Method name</param>
    /// <returns>Result of method execution</returns>
    public static Object callStaticMethod(String class_name, String method_name) throws Exception
    {
        return callMethod(class_name, null, method_name, null);
    }

    /// <summary>
    /// Call static method of given class using provided arguments.
    /// </summary>
    /// <param name="class_name">Class name</param>
    /// <param name="method_name">Method name</param>
    /// <param name="args">List of arguments</param>
    /// <returns>Result of method execution</returns>
    public static Object callStaticMethod(String class_name, String method_name, TArrayList args) throws Exception
    {

        Class type = Class.forName(class_name.replace('/', '.'));
        Method methodInfo = type.getMethod(method_name);
        if (args != null && args.size() > 0)
            return methodInfo.invoke(null, args.toArray());
        else
            return methodInfo.invoke(null, new Object[] {});
    }

    private static Class[] getTypes(TArrayList args) {
        Class[] types = args != null && args.size() > 0 ? new Class[args.size()] : new Class[0];
        if (types.length > 0)
        {
            for (int n = 0; n < args.size(); n++)
            {
                types[n] = args.get(n).getClass();
                if (args.get(n) instanceof String)
                {
                    try {
                        int result = Integer.parseInt((String)args.get(n));
                        types[n] = int.class;
                        args.set(n, result);
                    } catch (Exception ex) {
                        // skip
                        int x=1;
                    }
                }
            }
        }
        return types;
    }

    /// <summary>
    /// Call method of given class using provided arguments.
    /// </summary>
    /// <param name="class_name">Class name</param>
    /// <param name="args0">Constructor args</param>
    /// <param name="method_name">Method name</param>
    /// <param name="args">List of arguments</param>
    /// <returns>Result of method execution</returns>
    public static Object callMethod(String class_name, TArrayList args0, String method_name, TArrayList args) {
        try {
            return callMethodPrivate(class_name, args0, method_name, args);
        }
        catch (Exception ex) {
            //TODO -- do nothing for now
            int x=1;
        }
        return null;
    }
    
    private static Object callMethodPrivate(String class_name, TArrayList args0, String method_name, TArrayList args) throws Exception
    {
        String class_name_fixed = 
                Config.FILE_PREFIX.isEmpty() ? class_name : class_name.replaceFirst(Config.FILE_PREFIX, "");
        Class type = Class.forName(class_name_fixed.replace('/', '.'));

        Class[] types0 = getTypes(args0);
        Constructor constructorInfo = type.getConstructor(types0);
        Object doObject = constructorInfo.newInstance(args0.toArray());

        Class[] types = getTypes(args);
        Method methodInfo = type.getMethod(method_name, types);
        if (methodInfo != null)
        {
            if (args != null && args.size() > 0)
                return methodInfo.invoke(doObject, args.toArray());
            else
                return methodInfo.invoke(doObject);
        }
        else
            return null;
    }


    /*
    public static Object[] fetchRssOld(String url)
    {
        DataList items = new DataList();

        XmlDocument rssXmlDoc = new XmlDocument();

        XmlNamespaceManager nsmgr = new XmlNamespaceManager(rssXmlDoc.NameTable);
        nsmgr.AddNamespace("dc", "http://purl.org/dc/elements/1.1/");

        // Load the RSS file from the RSS URL
        rssXmlDoc.Load(url);

        // Parse the Items in the RSS file
        XmlNodeList rssNodes = rssXmlDoc.SelectNodes("rss/channel/item");

        // Iterate through the items in the RSS file
        foreach (XmlNode rssNode in rssNodes)
        {
            var item = new DataRange();

            XmlNode rssSubNode = rssNode.SelectSingleNode("title");
            if (rssSubNode != null)
                item["title"] = rssSubNode.InnerText;

            rssSubNode = rssNode.SelectSingleNode("link");
            if (rssSubNode != null)
                item["link"] = rssSubNode.InnerText;

            rssSubNode = rssNode.SelectSingleNode("description");
            if (rssSubNode != null)
                item["description"] = rssSubNode.InnerText;

            rssSubNode = rssNode.SelectSingleNode("pubDate");
            if (rssSubNode != null)
                item["pubdate"] = rssSubNode.InnerText; //Yes, lower case

            rssSubNode = rssNode.SelectSingleNode("dc:creator", nsmgr);
            if (rssSubNode != null)
            {
                item["dc"] = new DataRange();
                ((DataRange)item["dc"])["creator"] = rssSubNode.InnerText;
            }
            items.Add(item);
        }
        return items.ToArray();
    }
    */

    /// <summary>
    /// Fetch info from RSS-feed.
    /// </summary>
    /// <param name="url">Feed url</param>
    /// <returns>Resulting array of items</returns>
    public static Object[] fetchRss(String url)
    {
        TArrayList items = new TArrayList();

        RssReader rssReader = new com.apptastic.rssreader.RssReader();
        Stream<Item> itemsStream = null;
        try {
            itemsStream = rssReader.read(url);
        }
        catch (Exception ex) {
            return null;
        }
        Iterator<Item> iterator = itemsStream.iterator();

        while (iterator.hasNext()) {
            Item item = iterator.next();
            THashtable hash = new THashtable();
            if (!NUL(item.getTitle()))
                hash.put("title", item.getTitle());
            if (!NUL(item.getLink()))
                hash.put("link", item.getLink());
            if (!NUL(item.getDescription()))
                hash.put("description", item.getDescription());
            if (!NUL(item.getPubDate()))
                hash.put("pubDate", item.getPubDate());
            if (!NUL(item.getSource()))
                hash.put("source", item.getSource());
            if (!NUL(item.getCategory()))
                hash.put("category", item.getCategory());
            
            items.add(hash);
        }
        itemsStream.close();
        return items.toArray();
        
    }
}
