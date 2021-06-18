// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Internal;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import Bula.Objects.DataList;
import Bula.Objects.DataRange;

import Bula.Objects.Arrays;
import Bula.Objects.Helper;
import Bula.Objects.Strings;
import Bula.Objects.Response;

/**
 * Engine for processing templates.
 */
public class Engine extends Meta {
    public Context $context = null;
    private Boolean $printFlag = false;
    private String $printString = "";

    /** Public default constructor */
    public Engine (Context $context) {
        this.$context = $context;
        this.$printFlag = false;
        this.$printString = "";
    }

    /**
     * Set print string for current engine instance.
     * @param $val Print string to set.
     */
    public void setPrintString(String $val) {
        this.$printString = $val;
    }

    /**
     * Get print string for current engine instance.
     * @return String Current print string.
     */
    public String getPrintString() {
        return this.$printString;
    }

    /**
     * Set print flag for current engine instance.
     * @param $val Print flag to set.
     */
    public void setPrintFlag(Boolean $val) {
        this.$printFlag = $val;
    }

    /**
     * Get print flag for current engine instance.
     * @return Boolean Current print flag.
     */
    public Boolean getPrintFlag() {
        return this.$printFlag;
    }

    /**
     * Write string.
     * @param $val String to write.
     */
    public void write(String $val) {
        if (this.$printFlag)
            this.$context.$Response.write($val);
        else
            this.$printString += $val;
    }

    /**
     * Include file with class and generate content by calling method execute().
     * @param $className Class name to include.
     */
    public String includeTemplate(String $className) {
        return includeTemplate($className, "execute");
    }

    /**
     * Include file with class and generate content by calling method.
     * @param $className Class name to include.
     * @param $defaultMethod Default method to call.
     * @return String Resulting content.
     */
    public String includeTemplate(String $className, String $defaultMethod/* = "execute"*/) {
        Engine $engine = this.$context.pushEngine(false);
        String $prefix = CAT(Config.FILE_PREFIX, "Bula/Fetcher/Controller/");
        String $fileName =
            CAT($prefix, $className, ".java");

        String $content = null;
        if (Helper.fileExists(CAT(this.$context.$LocalRoot, $fileName))) {
            DataList $args0 = new DataList(); $args0.add(this.$context);
            Internal.callMethod(CAT($prefix, $className), $args0, $defaultMethod, null);
            $content = $engine.getPrintString();
        }
        else
            $content = CAT("No such file: ", $fileName);
        this.$context.popEngine();
        return $content;
    }

    /**
     * Show template content.
     * @param $filename Template file to use.
     * @return String Resulting content.
     */
    public String showTemplate(String $filename) {
        return showTemplate($filename, null); }

    /**
     * Show template content by merging template and data.
     * @param $id Template ID to import for merging.
     * @param $hash Data in the form of DataRange to import for merging.
     * @return String Resulting content.
     */
    public String showTemplate(String $id, DataRange $hash /*= null*/) {
        String $ext = BLANK(this.$context.$Api) ? ".html" : (Config.API_FORMAT == "Xml"? ".xml" : ".txt");
        String $prefix = CAT(Config.FILE_PREFIX, "Bula/Fetcher/View/");

        String $filename =
                CAT($prefix, (BLANK(this.$context.$Api) ? "Html/" : (Config.API_FORMAT == "Xml"? "Xml/" : "Rest/")), $id, $ext);
        DataList $template = this.getTemplate($filename);

        String $content = new String();
        String $short_name = Strings.replace("Bula/Fetcher/View/Html", "View", $filename);
        if (!BLANK(Config.FILE_PREFIX))
            $short_name = Strings.replace(Config.FILE_PREFIX, "", $short_name);
        if (BLANK(this.$context.$Api))
            $content += CAT(EOL, "<!-- BEGIN ", $short_name, " -->", EOL);
        if (!BLANK($template))
            $content += this.processTemplate($template, $hash);
        if (BLANK(this.$context.$Api))
            $content += CAT("<!-- END ", $short_name, " -->", EOL);
        return $content;
    }

    /**
     * Get template as the list of lines.
     * @param $filename File name.
     * @return DataList Resulting array with lines.
     */
    private DataList getTemplate(String $filename) {
        if (Helper.fileExists(CAT(this.$context.$LocalRoot, $filename))) {
            Object[] $lines = Helper.readAllLines(CAT(this.$context.$LocalRoot, $filename));
            return Arrays.createDataList($lines);
        }
        else {
            DataList $temp = new DataList();
            $temp.add(CAT("File not found -- '", $filename, "'<hr/>"));
            return $temp;
        }
    }

    /**
     * Do actual merging of template and data.
     * @param $template Template content.
     * @param $hash Data for merging with template.
     * @return String Resulting content.
     */
    public String formatTemplate(String $template, DataRange $hash) {
        if ($hash == null)
            $hash = new DataRange();
        String $content1 = Strings.replaceInTemplate($template, $hash);
        String $content2 = Strings.replaceInTemplate($content1, this.$context.$GlobalConstants);
        return $content2;
    }

    /**
     * Trim comments from input string.
     * @param $str Input string.
     * @return String Resulting string.
     */
    private static String trimComments(String $str) {
        return trimComments($str, true);
    }

    /**
     * Trim comments from input string.
     * @param $str Input string.
     * @param $trim Whether to trim spaces in resulting string.
     * @return String Resulting string.
     */
    private static String trimComments(String $str, Boolean $trim/* = true*/) {
        String $line = new String($str);
        Boolean $trimmed = false;
        if ($line.indexOf("<!--#") != -1) {
            $line = $line.replace("<!--", "");
            $line = $line.replace("-->", "");
            $trimmed = true;
        }
        else if ($line.indexOf("//#") != -1) {
            $line = $line.replace("//#", "#");
            $trimmed = true;
        }
        if ($trim)
            $line = $line.trim();
        return $line;
    }

    private String processTemplate(DataList $template) { return processTemplate($template, null); }

    /**
     * Execute template processing.
     * @param $template Template in form of the list of lines.
     * @param $hash Data for merging with template.
     * @return String Resulting content.
     */
    private String processTemplate(DataList $template, DataRange $hash /*= null*/) {
        if (this.$context.$IsMobile) {
            if ($hash == null)
                $hash = new DataRange();
            $hash.put("[#Is_Mobile]", 1);
        }
        Boolean $trimLine = true;
        String $trimEnd = EOL;
        int $ifMode = 0;
        int $repeatMode = 0;
        DataList $ifBuf = new DataList();
        DataList $repeatBuf = new DataList();
        String $ifWhat = "";
        String $repeatWhat = "";
        String $content = new String();
        for (int $n = 0; $n < $template.size(); $n++) {
            String $line = (String)$template.get($n);
            String $lineNoComments = trimComments($line); //, BLANK(this.$context.$Api)); //TODO
            if ($ifMode > 0) {
                if ($lineNoComments.indexOf("#if") == 0)
                    $ifMode++;
                if ($lineNoComments.indexOf("#end if") == 0) {
                    if ($ifMode == 1) {
                        Boolean $not = ($ifWhat.indexOf("!") == 0);
                        Boolean $eq = ($ifWhat.indexOf("==") != -1);
                        Boolean $neq = ($ifWhat.indexOf("!=") != -1);
                        Boolean $processFlag = false;
                        if ($not == true) {
                            if (!$hash.containsKey($ifWhat.substring(1))) //TODO
                                $processFlag = true;
                        }
                        else {
                            if ($eq) {
                                String[] $ifWhatArray = Strings.split("==", $ifWhat);
                                String $ifWhat1 = $ifWhatArray[0];
                                String $ifWhat2 = $ifWhatArray[1];
                                if ($hash.containsKey($ifWhat1) && EQ($hash.get($ifWhat1), $ifWhat2))
                                    $processFlag = true;
                            }
                            else if ($neq) {
                                String[] $ifWhatArray = Strings.split("!=", $ifWhat);
                                String $ifWhat1 = $ifWhatArray[0];
                                String $ifWhat2 = $ifWhatArray[1];
                                if ($hash.containsKey($ifWhat1) && !EQ($hash.get($ifWhat1), $ifWhat2))
                                    $processFlag = true;
                            }
                            else if ($hash.containsKey($ifWhat))
                                $processFlag = true;
                        }

                        if ($processFlag)
                            $content += processTemplate($ifBuf, $hash);
                        $ifBuf = new DataList();
                    }
                    else
                        $ifBuf.add($line);
                    $ifMode--;
                }
                else
                    $ifBuf.add($line);
            }
            else if ($repeatMode > 0) {
                if ($lineNoComments.indexOf("#repeat") == 0)
                    $repeatMode++;
                if ($lineNoComments.indexOf("#end repeat") == 0) {
                    if ($repeatMode == 1) {
                        if ($hash.containsKey($repeatWhat)) {
                            DataList $rows = (DataList)$hash.get($repeatWhat);
                            for (int $r = 0; $r < $rows.size(); $r++)
                                $content += processTemplate($repeatBuf, (DataRange)$rows.get($r));
                            $hash.remove($repeatWhat);
                        }
                        $repeatBuf = new DataList();
                    }
                    else
                        $repeatBuf.add($line);
                    $repeatMode--;
                }
                else
                    $repeatBuf.add($line);
            }
            else {
                if ($lineNoComments.indexOf("#if") == 0) {
                    $ifMode = $repeatMode > 0 ? 2 : 1;
                    $ifWhat = $lineNoComments.substring(4).trim();
                }
                else if ($lineNoComments.indexOf("#repeat") == 0) {
                    $repeatMode++;
                    $repeatWhat = $lineNoComments.substring(8).trim();
                    $repeatBuf = new DataList();
                }
                else {
                    if ($trimLine) {
                        $line = $line.trim();
                        $line += $trimEnd;
                    }
                    $content += $line;
                }
            }
        }
        String $result = formatTemplate($content, $hash);
        return $result;
    }
}
