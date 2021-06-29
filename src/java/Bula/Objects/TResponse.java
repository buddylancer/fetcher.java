// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;

import Bula.Objects.Translator;

/**
 * Helper class for processing server response.
 */
public class TResponse extends Meta {
    /** Current response */
    private javax.servlet.http.HttpServletResponse $httpResponse = null;

    /**
     * Default constructor.
     * @param $currentResponse Current http response object.
     */
    public TResponse (Object $currentResponse) {
        $httpResponse = (javax.servlet.http.HttpServletResponse)$currentResponse;
    }

    /**
     * Write text to current response.
     * @param $input Text to write.
     */
    public void write(String $input) {
        this.write($input, null);
    }

    /**
     * Write text to current response.
     * @param $input Text to write.
     * @param $lang Language to tranlsate to (default - none).
     */
    public void write(String $input, String $langFile/* = null*/) {
        if ($langFile != null) {
            if (!Translator.isInitialized())
                Translator.initialize($langFile);
            $input = Translator.translate($input);
        }
        try { $httpResponse.getWriter().append($input); } catch (Exception ex) {}
    }

    /**
     * Write header to current response.
     * @param $name Header name.
     * @param $value Header value.
     */
    public void writeHeader(String $name, String $value) {
        $httpResponse.addHeader($name, $value);
    }

    /**
     * End current response.
     */
    public void end() {
        end(null);
    }

    /**
     * End current response.
     * @param $input Text to write before ending response.
     */
    public void end(String $input/* = null*/) {
        if (!NUL($input))
            write($input);
         try { $httpResponse.flushBuffer(); } catch (Exception ex) {}
    }
}

