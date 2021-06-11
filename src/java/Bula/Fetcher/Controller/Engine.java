
package Bula.Fetcher.Controller;
import Bula.Meta;

import Bula.Internal;

import Bula.Fetcher.Config;
import Bula.Fetcher.Context;

import java.util.ArrayList;
import java.util.Hashtable;

import Bula.Objects.Arrays;
import Bula.Objects.Helper;
import Bula.Objects.Strings;
import Bula.Objects.Response;

/**
 * Engine for processing templates.
 */
public class Engine extends Meta {
    private Context $context = null;
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
            Response.write($val);
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
        String $prefix = "src/java/Bula/Fetcher/Controller/";
        String $fileName =
            CAT($prefix, $className, ".java");

        String $content = null;
        if (Helper.fileExists(CAT(this.$context.$LocalRoot, $fileName))) {
            ArrayList $args0 = new ArrayList(); $args0.add(this.$context);
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
     * @param $hash Data in the form of Hashtable to import for merging.
     * @return String Resulting content.
     */
    public String showTemplate(String $id, Hashtable $hash /*= null*/) {
        String $ext = BLANK(this.$context.$Api) ? ".html" : (Config.API_FORMAT == "Xml"? ".xml" : ".txt");
        String $filename = 
                CAT("Bula/Fetcher/View/", (BLANK(this.$context.$Api) ? "Html/" : (Config.API_FORMAT == "Xml"? "Xml/" : "Rest/")), $id, $ext);
        ArrayList $template = this.getTemplate($filename);

        String $content = new String();
        if (BLANK(this.$context.$Api))
            $content.concat(CAT(EOL, "<!-- BEGIN ", Strings.replace("Bula/Fetcher/View/Html", "View", $filename), " -.", EOL));
        if (!BLANK($template))
            $content.concat(this.processTemplate($template, $hash));
        if (BLANK(this.$context.$Api))
            $content.concat(CAT("<!-- END ", Strings.replace("Bula/Fetcher/View/Html", "View", $filename), " -.", EOL));
        return $content;
    }

    /**
     * Get template as the list of lines.
     * @param $filename File name.
     * @return ArrayList Resulting array with lines.
     */
    private ArrayList getTemplate(String $filename) {
        if (Helper.fileExists(CAT(this.$context.$LocalRoot, $filename))) {
            Object[] $lines = Helper.readAllLines(CAT(this.$context.$LocalRoot, $filename));
            return Arrays.createArrayList($lines);
        }
        else {
            ArrayList $temp = new ArrayList();
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
    public String formatTemplate(String $template, Hashtable $hash) {
        if ($hash == null)
            $hash = new Hashtable();
        String $content = Strings.replaceInTemplate($template, $hash);
        return Strings.replaceInTemplate($content, this.$context.$GlobalConstants);
    }

    private static String trimComments(String $str) {
        return trimComments($str, true);
    }

    /**
     * Trim comments from input string.
     * @param $str Input string.
     * @return String Resulting string.
     */
    private static String trimComments(String $str, Boolean $trim/* = true*/) {
        String $line = new String($str);
        Boolean $trimmed = false;
        if ($line.indexOf("<!--#") != -1) {
            $line = $line.replace("<!--", "");
            $line = $line.replace("-.", "");
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

    private String processTemplate(ArrayList $template) { return processTemplate($template, null); }

    /**
     * Execute template processing.
     * @param $template Template in form of the list of lines.
     * @param $hash Data for merging with template.
     * @return String Resulting content.
     */
    private String processTemplate(ArrayList $template, Hashtable $hash /*= null*/) {
        if (this.$context.$IsMobile) {
            if ($hash == null)
                $hash = new Hashtable();
            $hash.put("[#Is_Mobile]", 1);
        }
        Boolean $trimLine = true;
        String $trimEnd = EOL;
        int $ifMode = 0;
        int $repeatMode = 0;
        ArrayList $ifBuf = new ArrayList();
        ArrayList $repeatBuf = new ArrayList();
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
                            $content.concat(processTemplate($ifBuf, $hash));
                        $ifBuf = new ArrayList();
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
                            ArrayList $rows = (ArrayList)$hash.get($repeatWhat);
                            for (int $r = 0; $r < $rows.size(); $r++)
                                $content.concat(processTemplate($repeatBuf, (Hashtable)$rows.get($r)));
                            $hash.remove($repeatWhat);
                        }
                        $repeatBuf = new ArrayList();
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
                    $repeatBuf = new ArrayList();
                }
                else {
                    if ($trimLine) {
                        $line = $line.trim();
                        $line.concat($trimEnd);
                    }
                    $content.concat($line);
                }
            }
        }
        String $result = formatTemplate($content, $hash);
        return $result;
    }
}
