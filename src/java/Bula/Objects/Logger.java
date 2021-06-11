
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

    /**
     * Initialize logging into file.
     * @param $filename Log file name.
     */
    public void init(String $filename) {
        this.$fileName = $filename;
        if (!$filename.isEmpty()) {
            if (Helper.fileExists($filename))
                Helper.deleteFile($filename);
        }
    }

    /**
     * Log text string.
     * @param $text Content to log.
     */
    public void output(String $text) {
        if (this.$fileName == null) {
            Response.write($text);
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
