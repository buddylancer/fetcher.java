// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller.Testing;
import Bula.Meta;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;
import Bula.Fetcher.Controller.Page;
import Bula.Objects.DataList;
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
        //this.$context.$Request.initialize();
        this.$context.$Request.extractAllVars();

        this.$context.$Response.writeHeader("Content-type", "text/html; charset=UTF-8");

        // Check security code
        if (!this.$context.$Request.contains("code")) {
            this.$context.$Response.end("Code is required!");
            return;
        }
        String $code = this.$context.$Request.get("code");
        if (!EQ($code, Config.SECURITY_CODE)) {
            this.$context.$Response.end("Incorrect code!");
            return;
        }

        // Check package
        if (!this.$context.$Request.contains("package")) {
            this.$context.$Response.end("Package is required!");
            return;
        }
        String $package = this.$context.$Request.get("package");
        if (BLANK($package)) {
            this.$context.$Response.end("Empty package!");
            return;
        }
        String[] $packageChunks = Strings.split("-", $package);
        for (int $n = 0; $n < SIZE($packageChunks); $n++)
            $packageChunks[$n] = Strings.firstCharToUpper($packageChunks[$n]);
        $package = Strings.join("/", $packageChunks);

        // Check class
        if (!this.$context.$Request.contains("class")) {
            this.$context.$Response.end("Class is required!");
            return;
        }
        String $className = this.$context.$Request.get("class");
        if (BLANK($className)) {
            this.$context.$Response.end("Empty class!");
            return;
        }

        // Check method
        if (!this.$context.$Request.contains("method")) {
            this.$context.$Response.end("Method is required!");
            return;
        }
        String $method = this.$context.$Request.get("method");
        if (BLANK($method)) {
            this.$context.$Response.end("Empty method!");
            return;
        }

        // Fill array with parameters
        int $count = 0;
        DataList $pars = new DataList();
        for (int $n = 1; $n <= 6; $n++) {
            String $parName = CAT("par", $n);
            if (!this.$context.$Request.contains($parName))
                break;
            String $parValue = this.$context.$Request.get($parName);
            if (EQ($parValue, "_"))
                $parValue = "";
            //$parsArray[] = $parValue;
            $pars.add($parValue);
            $count++;
        }

        String $buffer = null;
        Object $result = null;

        String $fullClass = CAT($package, "/", $className);

        $fullClass = Strings.replace("/", ".", $fullClass);
        $result = Bula.Internal.callMethod($fullClass, new DataList(), $method, $pars);

        if ($result == null)
            $buffer = "NULL";
        else if ($result instanceof DataSet)
            $buffer = ((DataSet)$result).toXml(EOL);
        else
            $buffer = STR($result);
        this.$context.$Response.write($buffer);
        this.$context.$Response.end();
    }
}
