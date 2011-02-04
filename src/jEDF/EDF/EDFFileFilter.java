package jEDF.EDF;

import java.io.*;

import javax.swing.filechooser.FileFilter;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version : 2.0</p>
 */

public class EDFFileFilter extends FileFilter {
    private String[] allowedExtensions = null; //{"rec","edf","hyp"};
    private String description = "";

    /**
     *
     * @param allowedExtensions a String array describing all the allowed extensions
     * i.e. {"rec","edf","hyp"}
     * @param description the description to display in the JFileChooser
     */
    public EDFFileFilter(String[] allowedExtensions, String description) {
        this.allowedExtensions = allowedExtensions;
        this.description = description;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
        boolean result = false;

        String fileName = f.getName().toLowerCase();

        result = result || f.isDirectory();

        for (int i = 0; i < allowedExtensions.length && !result; i++) {
            result = result ||
                     fileName.endsWith("." + allowedExtensions[i].toLowerCase());
        }

        return result;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param file the file whose extension is to be modified
     * @param newExtension the new extension to set (".hyp")
     * @return a file name and path ending with the new extension
     */
    public static String changeFileExtension(File file, String newExtension) {
        String newFileName = file.getAbsolutePath();

        int dotIndex = newFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            newFileName = newFileName.substring(0, dotIndex);
        }
        newFileName += newExtension;

        return newFileName;
    }
}
