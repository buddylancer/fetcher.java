/*
 * MIT License
 *
 * Copyright (c) 2018, Apptastic Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.apptastic.rssreader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;


/**
 * Class for reading  RSS (Rich Site Summary) type of web feeds.
 */
public class RssReader {
    private static final String LOG_GROUP = "com.apptastic.rssreader";
    private static final String HTTP_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    /**
     * Read RSS feed with the given URL.
     * @param url URL to RSS feed.
     * @return Stream of items
     * @throws IOException Fail to read url or its content
     */
    public Stream<Item> read(String url) throws IOException {
        String[] encoding = new String[] { null };
        InputStream inputStream = sendRequest(url, encoding);
        removeBadDate(inputStream);

        RssItemIterator itemIterator = new RssItemIterator(inputStream, encoding[0]);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(itemIterator, Spliterator.ORDERED), false);
    }

    private void removeBadDate(InputStream inputStream) throws IOException {
        inputStream.mark(2);
        int firstChar = inputStream.read();

        if (firstChar != 65279 && firstChar != 13)
            inputStream.reset();

        if (firstChar == 13) {
            int secondChar = inputStream.read();

            if (secondChar != 10) {
                inputStream.reset();
                inputStream.read();
            }
        }
    }

    /**
     * Internal method for sending the http request.
     *
     * @param url URL to send the request
     * @param encoding Encoding of response stream
     * @return The response for the request
     * @throws IOException exception
     */
    protected InputStream sendRequest(String url, String[] encoding) throws IOException {
        URLConnection connection = new URL(url).openConnection();

        connection.setConnectTimeout(15 * 1000);
        connection.setReadTimeout(15 * 1000);
        connection.setRequestProperty("Accept-Encoding", "gzip");
        connection.setRequestProperty("User-Agent", HTTP_USER_AGENT);
        connection.connect();
        InputStream inputStream = connection.getInputStream();

        String contentType = connection.getHeaderField("Content-type");
        int charsetIndex = contentType.indexOf("charset=");
        String charset = charsetIndex != -1 ? contentType.substring(charsetIndex + "charset=".length()) : null;
        if (charset != null)
            encoding[0] = charset;
        
        if (connection.getContentEncoding() != null) {
            if ("gzip".equals(connection.getContentEncoding()))
                inputStream = new GZIPInputStream(inputStream);
        }

        return new BufferedInputStream(inputStream);
    }

    static class RssItemIterator implements Iterator<Item> {
        private InputStream is;
        private XMLStreamReader reader;
        private Channel channel;
        private Item item = null;
        private Item nextItem;
        private boolean isChannelPart = true;
        private String elementName = null;
        private StringBuilder textBuilder;

        public RssItemIterator(InputStream is, String encoding) {
            this.is = is;
            nextItem = null;
            textBuilder = new StringBuilder();

            try {
                XMLInputFactory xmlInFact = XMLInputFactory.newInstance();
                reader = xmlInFact.createXMLStreamReader(is, encoding == null ? "UTF-8" : encoding);
            }
            catch (XMLStreamException e) {
                Logger logger = Logger.getLogger(LOG_GROUP);

                if (logger.isLoggable(Level.WARNING))
                    logger.log(Level.WARNING, "Failed to process XML. ", e);
             }
        }

        void peekNext() {
            if (nextItem == null) {
                try {
                    nextItem = next();
                }
                catch (NoSuchElementException e) {
                    nextItem = null;
                }
            }
        }

        @Override
        public boolean hasNext() {
            peekNext();
            return nextItem != null;
        }

        @Override
        public Item next() {
            if (nextItem != null) {
                Item next = nextItem;
                nextItem = null;

                return next;
            }

            try {
                while (reader.hasNext()) {
                    int type = reader.next(); // do something here

                    if (type == XMLEvent.CHARACTERS) {
                        parseCharacters();
                    }
                    else if (type == XMLEvent.START_ELEMENT) {
                        parseStartElement();
                        parseAttributes();
                    }
                    else if (type == XMLEvent.END_ELEMENT) {
                        boolean itemParsed = parseEndElement();

                        if (itemParsed)
                            return item;
                    }
                }
            } catch (XMLStreamException e) {
                Logger logger = Logger.getLogger(LOG_GROUP);

                if (logger.isLoggable(Level.WARNING))
                    logger.log(Level.WARNING, "Failed to parse XML. ", e);
            }

            try {
                reader.close();
                is.close();
            }
            //catch (XMLStreamException | IOException e) {
            catch (Exception e) {
                Logger logger = Logger.getLogger(LOG_GROUP);

                if (logger.isLoggable(Level.WARNING))
                    logger.log(Level.WARNING, "Failed to close XML stream. ", e);
            }

            throw new NoSuchElementException();
        }

        void parseStartElement() {
            textBuilder.setLength(0);
            elementName = reader.getLocalName();

            if ("channel".equals(elementName) || "feed".equals(elementName)) {
                channel = new Channel();
                isChannelPart = true;
            }
            else if ("item".equals(elementName) || "entry".equals(elementName)) {
                item = new Item();
                item.setChannel(channel);
                isChannelPart = false;
            }
            else if ("guid".equals(elementName)) {
                String value = reader.getAttributeValue(null, "isPermaLink");
                if (item != null)
                    item.setIsPermaLink(Boolean.valueOf(value));
            }
        }

        void parseAttributes() {
            if (reader.getLocalName().equals("link")) {
                String rel = reader.getAttributeValue(null, "rel");
                String link = reader.getAttributeValue(null, "href");
                boolean isAlternate = "alternate".equals(rel);

                if (link != null && isAlternate) {
                    if (isChannelPart)
                        channel.setLink(link);
                    else
                        item.setLink(link);
                }
            }
        }

        boolean parseEndElement() {
            String name = reader.getLocalName();
            String text = textBuilder.toString().trim();

            if (isChannelPart)
                parseChannelCharacters(elementName, text);
            else
                parseItemCharacters(elementName, item, text);

            textBuilder.setLength(0);

            return "item".equals(name) || "entry".equals(name);
        }

        void parseCharacters() {
            String text = reader.getText();

            if (text.trim().isEmpty())
                return;

            textBuilder.append(text);
        }

        void parseChannelCharacters(String elementName, String text) {
            if (text.isEmpty())
                return;

            if ("title".equals(elementName))
                channel.setTitle(text);
            else if ("description".equals(elementName) || "subtitle".equals(elementName))
                channel.setDescription(text);
            else if ("language".equals(elementName))
                channel.setLanguage(text);
            else if ("link".equals(elementName))
                channel.setLink(text);
            else if ("copyright".equals(elementName))
                channel.setCopyright(text);
            else if ("generator".equals(elementName))
                channel.setGenerator(text);
            else if ("lastBuildDate".equals(elementName) || "updated".equals(elementName))
                channel.setLastBuildDate(text);
        }

        void parseItemCharacters(String elementName, Item item, String text) {
            if (text.isEmpty())
                return;

            if ("guid".equals(elementName) || "id".equals(elementName))
                item.setGuid(text);
            else if ("title".equals(elementName)) {
                //if (text.contains(" Los ")) {
                //    try { text = new String(text.getBytes(), "UTF-8"); } catch (Exception ex) { }
                //    int x=1;
                //}
                item.setTitle(text);
            }
            else if ("description".equals(elementName) || "content".equals(elementName))
                item.setDescription(text);
            else if ("pubDate".equals(elementName) || "published".equals(elementName))
                item.setPubDate(text);
            else if ("link".equals(elementName))
                item.setLink(text);
            //else if ("{http://purl.org/dc/elements/1.1/}creator".equals(elementName))
            else if ("creator".equals(elementName) || "author".equals(elementName))
                item.setSource(text);
            else if ("source".equals(elementName))
                item.setSource(text);
            else if ("company".equals(elementName))
                item.setSource(text);
            else if ("category".equals(elementName))
                item.setCategory(text);
            else {
                int x=1;
            }
        }

    }
}