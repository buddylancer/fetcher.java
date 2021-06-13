
package Bula.Objects;
import Bula.Meta;

/**
 * Helper class for processing server response.
 */
public class Response extends Meta {
    /** Current response */
    private javax.servlet.http.HttpServletResponse $httpResponse = null;

    public Response (Object $response) {
        $httpResponse = (javax.servlet.http.HttpServletResponse)$response;
    }

    /**
     * Write text to current response.
     * @param $input Text to write.
     */
    public void write(String $input) {
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

