
package Bula.Fetcher.Controller.Testing;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Fetcher.Controller.Page;
import java.util.ArrayList;
import Bula.Objects.Strings;
import Bula.Objects.Request;
import Bula.Objects.Response;
import Bula.Model.DataSet;

/**
 * Logic for remote method invocation.
 */
public class CallMethod extends Page {
    /**
     * Public default constructor.
     * @param $context Context instance.
     */
    public CallMethod(Context $context) { super($context); }

    /** Execute method using parameters from request. */
    public void execute() {
        Request.initialize();
        Request.extractAllVars();

        // Check security code
        if (!Request.contains("code")) {
            Response.end("Code is required!");
            return;
        }
        String $code = Request.get("code");
        if (!EQ($code, Config.SECURITY_CODE)) {
            Response.end("Incorrect code!");
            return;
        }

        // Check package
        if (!Request.contains("package")) {
            Response.end("Package is required!");
            return;
        }
        String $package = Request.get("package");
        if (BLANK($package)) {
            Response.end("Empty package!");
            return;
        }
        String[] $packageChunks = Strings.split("-", $package);
        for (int $n = 0; $n < SIZE($packageChunks); $n++)
            $packageChunks[$n] = Strings.firstCharToUpper($packageChunks[$n]);
        $package = Strings.join("/", $packageChunks);

        // Check class
        if (!Request.contains("class")) {
            Response.end("Class is required!");
            return;
        }
        String $className = Request.get("class");
        if (BLANK($className)) {
            Response.end("Empty class!");
            return;
        }

        // Check method
        if (!Request.contains("method")) {
            Response.end("Method is required!");
            return;
        }
        String $method = Request.get("method");
        if (BLANK($method)) {
            Response.end("Empty method!");
            return;
        }

        // Fill array with parameters
        int $count = 0;
        ArrayList $pars = new ArrayList();
        for (int $n = 1; $n <= 6; $n++) {
            String $parName = CAT("par", $n);
            if (!Request.contains($parName))
                break;
            String $parValue = Request.get($parName);
            if (EQ($parValue, "_"))
                $parValue = "";
            //$parsArray[] = $parValue;
            $pars.add($parValue);
            $count++;
        }

        String $buffer = null;
        Object $result = null;

        String $fullClass = CAT($package, "/", $className);

        if ($result == null)
            $buffer = "NULL";
        else if ($result instanceof DataSet)
            $buffer = ((DataSet)$result).toXml();
        else
            $buffer = STR($result);
        Response.write($buffer);
    }
}
