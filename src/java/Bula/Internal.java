// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

// Note: this class is not ported (is fully specific for NET-version).

package Bula;

import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;
//import com.apptastic.rssreader.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.*;

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
        return Pattern.compile(CAT("<[/]*", tag, "[^>]*[/]*>")).matcher(input).replaceAll("");
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

    private static String allowedChars = "€₹₽₴—•–‘’—№…"; //TODO!!! Hardcode Russian Ruble, Ukranian Hryvnia etc for now

    /// <summary>
    /// Clean out UTF-8 chars which are not accepted by MySQL.
    /// </summary>
    /// <param name="input">Input string</param>
    /// <returns>Resulting string</returns>
    public static String cleanChars(String input)
    {
        char[] inputChars = input.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < inputChars.length; n++) {
            if (inputChars[n] < 2048 || allowedChars.indexOf(inputChars[n]) != -1)
                sb.append(inputChars[n]);
        }
        return sb.toString();
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

    /// <summary>
    /// Fetch info from RSS-feed.
    /// </summary>
    /// <param name="url">Feed url</param>
    /// <returns>Resulting array of items</returns>
    public static Object[] fetchRss(String url)
    {
        TArrayList items = new TArrayList();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //XmlNamespaceManager nsmgr = new XmlNamespaceManager(rssXmlDoc.NameTable);
        //nsmgr.AddNamespace("dc", "http://purl.org/dc/elements/1.1/");

        // Load the RSS file from the RSS URL
        DocumentBuilder builder = null;
        Document rssXmlDoc = null;
        Object response = null;
        String content = "";
        try {
            URL urlObject = new URL(url);
            URLConnection connection = urlObject.openConnection();
            String contentType = connection.getContentType();
            int charsetIndex = contentType.indexOf("charset=");
            String encoding = charsetIndex != -1 ? contentType.substring(charsetIndex + "charset=".length()) : null;
            BufferedReader in = new BufferedReader(new InputStreamReader(urlObject.openStream(), encoding));
            String line;
            while ((line = in.readLine()) != null)
                content += line + EOL;
            in.close();            
            
            builder = factory.newDocumentBuilder();
            rssXmlDoc = builder.parse(new ByteArrayInputStream(content.getBytes(encoding)));
        }
        catch (Exception ex1) {
            String[] matches = Regex.matches(ex1.getMessage(), "'([^']+)' is an undeclared prefix. Line [0-9]+, position [0-9]+.");
            if (matches.length > 0) {
                try {
                    String pattern = CAT("<", matches[1], ":[^>]+>[^<]+</", matches[1], ":[^>]+>");
                    content = Regex.replace(content, pattern, "");
                    rssXmlDoc = builder.parse(new ByteArrayInputStream(content.getBytes()));
                }
                catch (Exception ex2) {
                    return null;
                }
            }
            else
                return null;
        }

        // Parse the Items in the RSS file
        NodeList rssNodes = rssXmlDoc.getElementsByTagName("item");

        // Iterate through the items in the RSS file
        for (int n1 = 0; n1 < rssNodes.getLength(); n1++) {
            Node rssNode = rssNodes.item(n1);
            THashtable item = new THashtable();

            NodeList itemNodes = rssNode.getChildNodes();
            for (int n2 = 0; n2 < itemNodes.getLength(); n2++) {
                Node itemNode = itemNodes.item(n2);
                String itemNodeName = itemNode.getNodeName();
                if (itemNodeName.startsWith("#"))
                    continue;

                String text = "";
                if (itemNodeName == "description") {
                    if (itemNode.getTextContent().contains("10-15")) {
                        int x=1;
                    }
                }
                if (itemNode.hasChildNodes()) {
                    NodeList internalNodes = itemNode.getChildNodes();
                    if (internalNodes.getLength() > 1) {
                        for (int n3 = 0; n3 < internalNodes.getLength(); n3++) {
                            Node internalNode = internalNodes.item(n3);
                            String internalNodeName = internalNode.getNodeName();
                            if (!internalNodeName.startsWith("#"))
                                text += "<" + internalNodeName + ">" + 
                                    internalNode.getTextContent() + "</" + internalNodeName + ">"; 
                            else {
                                if (!text.isEmpty())
                                    text += " ";
                                text += internalNode.getTextContent();
                            }
                        }
                    }
                    else 
                        text = itemNode.getTextContent();
                }
                else 
                    text = itemNode.getTextContent();
                
                if (itemNodeName == "category") {
                    if (!item.containsKey(itemNodeName))
                        item.put(itemNodeName, text);
                    else
                        item.put(itemNodeName, CAT(item.get(itemNodeName), ", ", text));
                }
                else if (itemNodeName == "dc:date") {
                    THashtable $dc = item.containsKey("dc") ? (THashtable)item.get("dc") : new THashtable();
                    $dc.put("date", text); item.put("dc", $dc);
                }
                else if (itemNodeName == "dc:creator") {
                    THashtable $dc = item.containsKey("dc") ? (THashtable)item.get("dc") : new THashtable();
                    $dc.put("creator", text); item.put("dc", $dc);
                }
                else
                    item.put(itemNodeName, text);
            }
            items.add(item);
        }
        return items.toArray();
    }    
}
