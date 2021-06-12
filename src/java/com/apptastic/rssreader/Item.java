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


/**
 * Class representing a RSS item. A channel may contain any number of items. An item may represent a "story" -- much
 * like a story in a newspaper or magazine; if so its description is a synopsis of the story, and the link points
 * to the full story.
 */
public class Item {
    private String title;
    private String description;
    private String link;
    private String guid;
    private boolean isPermaLink;
    private String pubDate;
    private Channel channel;

    /**
     * Get the title of the item.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the item.
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the item synopsis.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the item synopsis.
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the URL of the item.
     * @return link
     */
    public String getLink() {
        return link;
    }

    /**
     * Set the URL of the item.
     * @param link link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Get a string that uniquely identifies the item.
     * @return guid
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Set a string that uniquely identifies the item.
     * @param guid guid
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * If the guid element has an attribute named "isPermaLink" with a value of true, the reader may assume that
     * it is a permalink to the item, that is, a url that can be opened in a Web browser, that points to the full
     * item described by the item element.
     * @return permanent link
     */
    public boolean getIsPermaLink() {
        return isPermaLink;
    }

    /**
     * If the guid element has an attribute named "isPermaLink" with a value of true, the reader may assume that
     * it is a permalink to the item, that is, a url that can be opened in a Web browser, that points to the full
     * item described by the item element.
     * @param isPermaLink is perma link
     */
    public void setIsPermaLink(boolean isPermaLink) {
        this.isPermaLink = isPermaLink;
    }

    /**
     * Get a string that indicates when the item was published.
     * @return publication date
     */
    public String getPubDate() {
        return pubDate;
    }

    /**
     * Set a string that indicates when the item was published.
     * @param pubDate publication date
     */
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    /**
     * Get the channel that this item was published in.
     * @return channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Set the channel that this item was published in.
     * @param channel channel
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
