
package Bula.Objects;
import Bula.Meta;

import Bula.Objects.Arrays;
import Bula.Objects.Enumerator;
import java.util.Hashtable;

import java.lang.reflect.*;
import java.util.Enumeration;

/**
 * Base helper class for processing query/form request.
 */
public class RequestBase extends Meta {
    /** Current Http request */
    public javax.servlet.http.HttpServletRequest $HttpRequest = null;
    /** Current response */
    public Response $response = null;

    /** Enum value (type) for getting POST parameters */
    public static final int INPUT_POST = 0;
    /** Enum value (type) for getting GET parameters */
    public static final int INPUT_GET = 1;
    /** Enum value (type) for getting COOKIE parameters */
    public static final int INPUT_COOKIE = 2;
    /** Enum value (type) for getting ENV parameters */
    public static final int INPUT_ENV = 4;
    /** Enum value (type) for getting SERVER parameters */
    public static final int INPUT_SERVER = 5;

    public RequestBase () { }

    public RequestBase (Object $currentRequest/* = null*/) {
        if (NUL($currentRequest))
            return;
        $HttpRequest = (javax.servlet.http.HttpServletRequest)$currentRequest;
    }

    public Hashtable getVars(Integer $type) {
        String $method = $HttpRequest.getMethod();
        switch ($type) {
            case INPUT_GET:
                if (!EQ($method, "GET"))
                    break;
                return createHashtable($HttpRequest, "getParameterNames", "getParameter");
            case INPUT_POST:
                if (!EQ($method, "POST"))
                    break;
                return createHashtable($HttpRequest, "getParameterNames", "getParameter");
            case INPUT_SERVER:
                return createHashtable($HttpRequest, "getHeaderNames", "getHeader");
            default:
                break;
        }
        return new Hashtable();
    }

    private Hashtable createHashtable(Object $from, String $getNames, String $getValue) {
        Hashtable $hash = new Hashtable();
        try {
            Method $getNamesMethod = $from.getClass().getMethod($getNames, new Class[] {});
            Method $getValueMethod = $from.getClass().getMethod($getValue, new Class[] { String.class});
            Enumerator $names = new Enumerator((Enumeration)$getNamesMethod.invoke($from, (Object[])null));
            while ($names.hasMoreElements()) {
                String $name = (String)$names.nextElement();
                String $mappedName = $mapHeaders.containsKey($name) ? $mapHeaders.get($name) : $name;
                String $parameter = (String)$getValueMethod.invoke($from, new Object[] { $name });
                $hash.put($mappedName, $parameter);
            }
        }
        catch (Exception $ex) {
            $response.end($ex.getMessage());
        }
        return $hash;
    }

    /**
     * Get a single variable of given type.
     * @param $type Required type.
     * @param $name Variable name.
     * @return String Requested variable.
     */
    public String getVar(Integer $type, String $name) {
        Hashtable $vars = getVars($type);
        return (String)$vars.get($name);
    }
    
    private static final Hashtable<String, String> $mapHeaders = new Hashtable<String, String>() {
        { put("user-agent", "HTTP_USER_AGENT"); }
        { put("host", "HTTP_HOST"); }
        { put("query", "QUERY_STRING"); }
        { put("referer", "HTTP_REFERER"); }
    };
    
}

