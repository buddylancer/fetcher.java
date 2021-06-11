
package Bula.Objects;
import Bula.Meta;

/**
 * Helper class for processing server response.
 */
public class Response extends Meta {

    /**
     * Write text to current response.
     * @param $input Text to write.
     */
    public static void write(String $input) {
    }

    /**
     * Write header to current response.
     * @param $name Header name.
     * @param $value Header value.
     */
    public static void writeHeader(String $name, String $value) {
    }

    /**
     * End current response.
     * @param $input Text to write before ending response.
     */
    public static void end(String $input) {
        write($input);
    }
}

