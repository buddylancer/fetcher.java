
package Bula.Objects;
import Bula.Meta;

/**
 * Helper class for processing server response.
 */
public class Response extends Meta {
    public static javax.servlet.http.HttpServletResponse CurrentResponse = null;

    /**
     * Write text to current response.
     * @param $input Text to write.
     */
    public static void write(String $input) {
		try { CurrentResponse.getWriter().append($input); } catch (Exception ex) {}

    }

    /**
     * Write header to current response.
     * @param $name Header name.
     * @param $value Header value.
     */
    public static void writeHeader(String $name, String $value) {
		CurrentResponse.addHeader($name, $value);

    }

    /**
     * End current response.
     * @param $input Text to write before ending response.
     */
    public static void end(String $input) {
        write($input);
		 try { CurrentResponse.flushBuffer(); } catch (Exception ex) {}
    }
}

