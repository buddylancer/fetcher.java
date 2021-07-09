// Buddy Fetcher: simple RSS-fetcher/aggregator.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license.

package Bula.Objects;
import Bula.Meta;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;

import Bula.Objects.TEnumerator;

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

        TEnumerator $entries = listDirEntries($path);
        while ($entries.moveNext()) {
            String $entry = CAT($entries.getCurrent());

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
            return $encoding == null ? new String(Files.readAllBytes(Paths.get($filename))) :
                new String(Files.readAllBytes(Paths.get($filename)), Charset.forName($encoding));
        }
        catch (Exception $ex) { $lastError = $ex.getMessage().toString(); return null; }
    }

    /**
     * Read all content of text file as list of lines.
     * @param $filename File name.
     * @return Object[] Resulting content (lines).
     */
    public static String[] readAllLines(String $filename) {
        return readAllLines($filename, null); }

    /**
     * Read all content of text file as list of lines.
     * @param $filename File name.
     * @param $encoding Encoding name [optional].
     * @return Object[] Resulting content (lines).
     */
    public static String[] readAllLines(String $filename, String $encoding /*= null*/) {
        try {
            return $encoding == null ? Files.readAllLines(Paths.get($filename)).toArray(new String[] {}) :
                Files.readAllLines(Paths.get($filename), Charset.forName($encoding)).toArray(new String[] {});
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
        try { Files.write(Paths.get($filename), $text.getBytes()); }
        catch (Exception $ex) { $lastError = $ex.getMessage().toString(); return false; } return true;
    }

    /**
     * Append content to text file.
     * @param $filename File name.
     * @param $text Content to append.
     * @return Boolean Result of operation (true - OK, false - error).
     */
    public static Boolean appendText(String $filename, String $text) {
        try { Files.write(Paths.get($filename), $text.getBytes(), StandardOpenOption.APPEND); }
        catch (Exception $ex) { $lastError = $ex.getMessage().toString(); return false; } return true;
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
     * @return TEnumerator Enumerated entries.
     */
    public static TEnumerator listDirEntries(String $path) {
        TArrayList $entries = new TArrayList();
        /*
        if (($handle = opendir(CAT($path))) == null)
            null;
        while (false !== ($file = readdir($handle))) {
            if ($file == "." || $file == "..")
                continue;
            $path2 = CAT($path, "/", $file);
            $entries.add($path2);
        }
        closedir($handle);
        */
        $entries.addAll((new File($path)).list());

        return new TEnumerator($entries.toArray());
    }
}
