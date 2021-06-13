
package Bula.Objects;
import Bula.Meta;

import Bula.Objects.Arrays;
import Bula.Objects.Enumerator;
import java.util.Hashtable;
import Bula.Objects.Regex;

/**
 * Helper class for processing query/form request.
 */
public class Request extends RequestBase {
    /** Internal storage for GET/POST variables */
    private Hashtable $Vars = null;
    /** Internal storage for SERVER variables */
    private Hashtable $ServerVars = null;

    public Request(Object $currentRequest) { super($currentRequest); initialize(); }

    /** Initialize internal variables for new request. */
    private void initialize() {
        this.$Vars = Arrays.newHashtable();
        this.$ServerVars = Arrays.newHashtable();
    }

    /**
     * Get private variables.
     * @return Hashtable
     */
    public Hashtable getPrivateVars() {
        return this.$Vars;
    }

    /**
     * Check whether request contains variable.
     * @param $name Variable name.
     * @return Boolean True - variable exists, False - not exists.
     */
    public Boolean contains(String $name) {
        return this.$Vars.containsKey($name);
    }

    /**
     * Get variable from internal storage.
     * @param $name Variable name.
     * @return String Variable value.
     */
    public String get(String $name) {
        //return (String)($Vars.containsKey($name) ? $Vars.get($name) : null);
        if (!this.$Vars.containsKey($name))
            return null;
        String $value = (String)this.$Vars.get($name);
        if (NUL($value))
            $value = "";
        return $value;
    }

    /**
     * Set variable into internal storage.
     * @param $name Variable name.
     * @param $value Variable value.
     */
    public void set(String $name, String $value) {
        this.$Vars.put($name, $value);
    }

    /**
     * Get all variable keys from request.
     * @return Enumeration All keys enumeration.
     */
    public Enumerator getKeys() {
        return new Enumerator(this.$Vars.keys());
    }

    /** Extract all POST variables into internal variables. */
    public void extractPostVars() {
        Hashtable $vars = this.getVars(INPUT_POST);
        this.$Vars = Arrays.mergeHashtable(this.$Vars, $vars);
    }

    /** Extract all SERVER variables into internal storage. */
    public void extractServerVars() {
        Hashtable $vars = this.getVars(INPUT_SERVER);
        this.$Vars = Arrays.mergeHashtable(this.$ServerVars, $vars);
    }

    /** Extract all GET and POST variables into internal storage. */
    public void extractAllVars() {
        Hashtable $vars = this.getVars(INPUT_GET);
        this.$Vars = Arrays.mergeHashtable(this.$Vars, $vars);
        this.extractPostVars();
    }

    /**
     * Check that referer contains text.
     * @param $text Text to check.
     * @return Boolean True - referer contains provided text, False - not contains.
     */
    public Boolean checkReferer(String $text) {
        //return true; //TODO
        String $httpReferer = this.getVar(INPUT_SERVER, "HTTP_REFERER");
        if ($httpReferer == null)
            return false;
        return $httpReferer.indexOf($text) != -1;
    }

    /**
     * Check that request was originated from test script.
     * @return Boolean True - from test script, False - from ordinary user agent.
     */
    public Boolean checkTester() {
        String $httpTester = this.getVar(INPUT_SERVER, "HTTP_USER_AGENT");
        if ($httpTester == null)
            return false;
        return $httpTester.indexOf("Wget") != -1;
    }

    /**
     * Get required parameter by name (or stop execution).
     * @param $name Parameter name.
     * @return String Resulting value.
     */
    public String getRequiredParameter(String $name) {
        String $val = null;
        if (this.contains($name))
            $val = this.get($name);
        else {
            String $error = CAT("Parameter '", $name, "' is required!");
            this.$response.end($error);
        }
        return $val;
    }

    /**
     * Get optional parameter by name.
     * @param $name Parameter name.
     * @return String Resulting value or null.
     */
    public String getOptionalParameter(String $name) {
        String $val = null;
        if (this.contains($name))
            $val = this.get($name);
        return $val;
    }

    /**
     * Get required integer parameter by name (or stop execution).
     * @param $name Parameter name.
     * @return Integer Resulting value.
     */
    public int getRequiredInteger(String $name) {
        String $str = this.getRequiredParameter($name);
        if ($str == "" || !isInteger($str)) {
            String $error = CAT("Error in parameter '", $name, "'!");
            this.$response.end($error);
        }
        return INT($str);
    }

    /**
     * Get optional integer parameter by name.
     * @param $name Parameter name.
     * @return Integer Resulting value or null.
     */
    public int getOptionalInteger(String $name) {
        String $val = this.getOptionalParameter($name);
        if ($val == null)
            return -99999; //TODO

        String $str = STR($val);
        if ($str == "" || !isInteger($str)) {
            String $error = CAT("Error in parameter '", $name, "'!");
            this.$response.end($error);
        }
        return INT($val);
    }

    /**
     * Get required string parameter by name (or stop execution).
     * @param $name Parameter name.
     * @return String Resulting value.
     */
    public String getRequiredString(String $name) {
        String $val = this.getRequiredParameter($name);
        return $val;
    }

    /**
     * Get optional string parameter by name.
     * @param $name Parameter name.
     * @return String Resulting value or null.
     */
    public String getOptionalString(String $name) {
        String $val = this.getOptionalParameter($name);
        return $val;
    }

    /**
     * Test (match) a page request with array of allowed pages.
     * @param[] $pages Array of allowed pages (and their parameters).
     * @return Hashtable Resulting page parameters.
     */
    public Hashtable testPage(Object[] $pages) {
        return testPage($pages, null); }

    /**
     * Test (match) a page request with array of allowed pages.
     * @param[] $pages Array of allowed pages (and their parameters).
     * @param $defaultPage Default page to import for testing.
     * @return Hashtable Resulting page parameters.
     */
    public Hashtable testPage(Object[] $pages, String $defaultPage /*= null*/) {
        Hashtable $pageInfo = new Hashtable();

        // Get page name
        String $page = null;
        $pageInfo.put("from_get", 0);
        $pageInfo.put("from_post", 0);

        String $apiValue = this.getVar(INPUT_GET, "api");
        if ($apiValue != null) {
            if (EQ($apiValue, "rest")) // Only Rest for now
                $pageInfo.put("api", $apiValue);
        }

        String $pValue = this.getVar(INPUT_GET, "p");
        if ($pValue != null) {
            $page = $pValue;
            $pageInfo.put("from_get", 1);
        }
        $pValue = this.getVar(INPUT_POST, "p");
        if ($pValue != null) {
            $page = $pValue;
            $pageInfo.put("from_post", 1);
        }
        if ($page == null)
            $page = $defaultPage;

        $pageInfo.remove("page");
        for (int $n = 0; $n < SIZE($pages); $n += 4) {
            if (EQ($pages[$n], $page)) {
                $pageInfo.put("page", $pages[$n + 0]);
                $pageInfo.put("class", $pages[$n + 1]);
                $pageInfo.put("post_required", $pages[$n + 2]);
                $pageInfo.put("code_required", $pages[$n + 3]);
                break;
            }
        }
        return $pageInfo;
    }

    /**
     * Check whether text is ordinary name.
     * @param $input Input text.
     * @return Boolean True - text matches name, False - not matches.
     */
    public static Boolean isName(String $input) {
        return Regex.isMatch($input, "^[A-Za-z_]+[A-Za-z0-9_]*$");
    }

    /**
     * Check whether text is domain name.
     * @param $input Input text.
     * @return Boolean True - text matches domain name, False - not matches.
     */
    public static Boolean isDomainName(String $input) {
        return Regex.isMatch($input, "^[A-Za-z]+[A-Za-z0-9\\.]*$");
    }

    /**
     * Check whether text is positive integer.
     * @param $input Input text.
     * @return Boolean True - text matches, False - not matches.
     */
    public static Boolean isInteger(String $input) {
        return Regex.isMatch($input, "^[1-9]+[0-9]*$");
    }
}
