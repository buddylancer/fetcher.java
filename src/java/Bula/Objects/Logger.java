
package Bula.Objects;
import Bula.Meta;

import Bula.Objects.Response;
import Bula.Objects.DateTimes;
import Bula.Objects.Helper;

/**
 * Simple logger.
 */
public class Logger extends Meta {
    private String $fileName = null;
    private Response $response = null;

    /**
     * Initialize logging into file.
     * @param $filename Log file name.
     */
    public void initFile(String $filename) {
        this.$response = null;
        this.$fileName = $filename;
        if (!$filename.isEmpty()) {
            if (Helper.fileExists($filename))
                Helper.deleteFile($filename);
        }
    }

    /**
     * Initialize logging into file.
     * @param $filename Log file name.
     */
    public void initResponse(Response $response) {
        this.$fileName = null;
        if (!NUL($response))
            this.$response = $response;
    }

    /**
     * Log text string.
     * @param $text Content to log.
     */
    public void output(String $text) {
        if (this.$fileName == null) {
            this.$response.write($text);
            return;
        }
        if (Helper.fileExists(this.$fileName))
            Helper.appendText(this.$fileName, $text);
        else {
            Helper.testFileFolder(this.$fileName);
            Helper.writeText(this.$fileName, $text);
        }

    }

    /**
     * Log text string + current time.
     * @param $text Content to log.
     */
    public void time(String $text) {
        this.output(CAT($text, " -- ", DateTimes.format("H:i:s"), "<br/>", EOL));
    }
}
