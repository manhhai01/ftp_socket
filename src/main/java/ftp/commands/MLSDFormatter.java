/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author User
 */
public class MLSDFormatter {

    private String formatImplementation(File file, FileFilter fileFilter) {
        String result = "";
        System.out.println(file.getName());
        File[] files = fileFilter == null ? file.listFiles() : file.listFiles(fileFilter);
        if (files.length == 0) {
            return "";
        }
        for (File f : files) {
            if (f.isDirectory()) {
                result += String.format("Type=%s;Size=%s;Perm=el; %s\n",
                        f.isFile() ? "file" : "dir",
                        f.length(),
                        f.getName());
            } else {
                result += String.format("Type=%s;Size=%s;Perm=%s; %s\n",
                        f.isFile() ? "file" : "dir",
                        f.length(),
                        "r",
                        f.getName());
            }

        }
        return result;
    }
    
    public String format(File file) {
        return formatImplementation(file, null);
    }
    
    public String format(File file, FileFilter fileFilter) {
        return formatImplementation(file, fileFilter);
    }
}
