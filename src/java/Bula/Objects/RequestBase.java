
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

    private static javax.servlet.http.HttpServletRequest CurrentRequest() {
        return null;
    }

    /**
     * Get all variables of given type.
     * @param $type Required type.
     * @return Hashtable Requested variables.
     */
    /*
    public static getVars($type) {
        $output = Arrays.newHashtable();
        $vars = filter_input_array($type);
        if ($vars === false || $vars == null)
            return $output;
        foreach ($vars as $key => $value)
            $output.put($key, $value == null ? "" : $value);
        return $output;
    }
    */
    public static Hashtable getVars(Integer $type) {
        String $method = CurrentRequest().getMethod();
        switch ($type) {
            case INPUT_GET:
                if (EQ($method, "GET")) return createHashtable(CurrentRequest(), "getParameterNames", "getParameter");
                break;
            case INPUT_POST:
                if (EQ($method, "POST")) return createHashtable(CurrentRequest(), "getParameterNames", "getParameter");
                break;
            case INPUT_SERVER:
                return createHashtable(CurrentRequest(), "getHeaderNames", "getHeader");
            default:
                break;
        }
        return new Hashtable();
    }

    private static Hashtable createHashtable(Object $from, String $getNames, String $getValue) {
        Hashtable $hash = new Hashtable();
        try {
            Method $getNamesMethod = $from.getClass().getMethod($getNames, new Class[] {});
            Method $getValueMethod = $from.getClass().getMethod($getValue, new Class[] {});
            Enumerator $names = new Enumerator((Enumeration)$getNamesMethod.invoke($from, (Object[])null));
            while ($names.hasMoreElements()) {
                String $postName = (String)$names.nextElement();
                String $parameter = (String)$getValueMethod.invoke($postName, new Object[] {});
                $hash.put($postName, $parameter);
            }
        }
        catch (Exception ex) {
            Meta.STOP(ex.getMessage());
        }
        return $hash;
    }	

    /**
     * Get a single variable of given type.
     * @param $type Required type.
     * @param $name Variable name.
     * @return String Requested variable.
     */
    /*
    public static String getVar(int $type, String $name) {
        $var = filter_input($type, $name);
        return $var == null ? null : new String($var);
    }
    */
    public static String getVar(Integer $type, String $name) {
        Hashtable $vars = getVars($type);
        return (String)$vars.get($name);
    }
}

