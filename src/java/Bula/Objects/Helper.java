
package Bula.Objects;
import Bula.Meta;
//SKIP cs
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;

import Bula.Objects.Enumerator;

/**
 * Helper class for manipulation with Files and Directories.
 */
public class Helper extends Meta {
    private static String $lastError = null;

    /**
     * Get last error (if any).
     * @return String Last error message.
     */
    public static String lastError() {
        return $lastError;
    }

    /**
     * Check whether file exists.
     * @param $path File name.
     * @return Boolean
     */
    public static Boolean fileExists(String $path) {
        return Files.exists(Paths.get($path)) && !Files.isDirectory(Paths.get($path));
    }

    /**
     * Check whether file exists.
     * @param $path File name.
     * @return Boolean
     */
    public static Boolean dirExists(String $path) {
        return Files.exists(Paths.get($path)) && Files.isDirectory(Paths.get($path));
    }

    /**
     * Create directory.
     * @param $path Directory path to create.
     * @return Boolean True - created OK, False - error.
     */
    public static Boolean createDir(String $path) {
        try { Files.createDirectory(Paths.get($path)); } catch (Exception ex){ return false; } return true;
    }

    /**
     * Delete file.
     * @param $path File name.
     * @return Boolean True - OK, False - error.
     */
    public static Boolean deleteFile(String $path) {
        try { Files.delete(Paths.get($path)); } catch (Exception ex){ return false; } return true;
    }

    /**
     * Delete directory (recursively).
     * @param $path Directory name.
     * @return Boolean True - OK, False - error.
     */
    public static Boolean deleteDir(String $path) {

        if (!dirExists($path))
            return false;

        Enumerator $entries = listDirEntries($path);
        while ($entries.hasMoreElements()) {
            String $entry = CAT($entries.nextElement());

            if (isFile($entry))
                deleteFile($entry);
            else if (isDir($entry))
                deleteDir($entry);
        }
        return removeDir($path);
    }

    /**
     * Remove directory.
     * @param $path Directory name.
     * @return Boolean True - OK, False - error.
     */
    public static Boolean removeDir(String $path) {
        File $file = new File($path); return $file.isDirectory() && $file.delete();
    }

    /**
     * Read all content of text file.
     * @param $filename File name.
     * @return String Resulting content.
     */
    public static String readAllText(String $filename) {
        return readAllText($filename, null); }

    /**
     * Read all content of text file.
     * @param $filename File name.
     * @param $encoding Encoding name [optional].
     * @return String Resulting content.
     */
    public static String readAllText(String $filename, String $encoding /*= null*/) {
        try {
            if ($encoding == null)
                return new String(Files.readAllBytes(Paths.get($filename)));
            else
                return new String(Files.readAllBytes(Paths.get($filename)), Charset.forName($encoding));
        }
        catch (Exception $ex) {
            $lastError = $ex.getMessage().toString();
            return null;
        }
    }

      /**
     * Read all content of text file as list of lines.
     * @param $filename File name.
     * @return Object[] Resulting content (lines).
     */
    public static Object[] readAllLines(String $filename) {
        return readAllLines($filename, null); }

      /**
     * Read all content of text file as list of lines.
     * @param $filename File name.
     * @param $encoding Encoding name [optional].
     * @return Object[] Resulting content (lines).
     */
    public static Object[] readAllLines(String $filename, String $encoding /*= null*/) {
        try {
            if ($encoding == null)
                return Files.readAllLines(Paths.get($filename)).toArray();
            else
                return Files.readAllLines(Paths.get($filename), Charset.forName($encoding)).toArray();
        }
        catch (Exception $ex) {
            $lastError = $ex.getMessage().toString();
            return null;
        }
    }

    /**
     * Write content to text file.
     * @param $filename File name.
     * @param $text Content to write.
     * @return Boolean Result of operation (true - OK, false - error).
     */
    public static Boolean writeText(String $filename, String $text) {
        try {
            Files.write(Paths.get($filename), $text.getBytes());
            return true;
        }
        catch (Exception $ex) {
            $lastError = $ex.getMessage().toString();
            return false;
        }
    }

    /**
     * Append content to text file.
     * @param $filename File name.
     * @param $text Content to append.
     * @return Boolean Result of operation (true - OK, false - error).
     */
    public static Boolean appendText(String $filename, String $text) {
        try {
            Files.write(Paths.get($filename), $text.getBytes(), StandardOpenOption.APPEND);
            return true;
        }
        catch (Exception $ex) {
            $lastError = $ex.getMessage().toString();
            return false;
        }
    }

    /**
     * Check whether given path is a file.
     * @param $path Path of an object.
     * @return Boolean True - is a file.
     */
    public static Boolean isFile(String $path) {
        return Files.exists(Paths.get($path)) && !Files.isDirectory(Paths.get($path));
    }

    /**
     * Check whether given path is a directory.
     * @param $path Path of an object.
     * @return Boolean True - is a directory.
     */
    public static Boolean isDir(String $path) {
        return Files.exists(Paths.get($path)) && Files.isDirectory(Paths.get($path));
    }

    /**
     * Test the chain of (sub)folder(s), create them if necessary.
     * @param $folder Folder's full path.
     */
    public static void testFolder(String $folder) {
        String[] $chunks = $folder.split("/");
        String $pathname = null;
        for (int $n = 0; $n < SIZE($chunks); $n++) {
            $pathname = CAT($pathname, $chunks[$n]);
            if (!Helper.dirExists($pathname))
                Helper.createDir($pathname);
            $pathname = CAT($pathname, "/");
        }
    }

    /**
     * Test the chain of (sub)folder(s) and file, create if necessary.
     * @param $filename Filename's full path
     */
    public static void testFileFolder(String $filename) {
        String[] $chunks = $filename.split("/");
        String $pathname = null;
        for (int $n = 0; $n < SIZE($chunks) - 1; $n++) {
            $pathname = CAT($pathname, $chunks[$n]);
            if (!Helper.dirExists($pathname))
                Helper.createDir($pathname);
            $pathname = CAT($pathname, "/");
        }
    }

    /**
     * List (enumerate) entries of a given path.
     * @param $path Path of a directory.
     * @return Enumerator Enumerated entries.
     */
    public static Enumerator listDirEntries(String $path) {
        String[] entries = (new File($path)).list();
        return new Enumerator(entries);
    }
}
