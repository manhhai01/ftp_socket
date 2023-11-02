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

    private String format(File file) {
        String result;
        if (file.isDirectory()) {
            result = String.format("Type=%s;Size=%s;Perm=el; %s\n",
                    "dir",
                    file.length(),
                    file.getName());
        } else {
            result = String.format("Type=%s;Size=%s;Perm=%s; %s\n",
                    "file",
                    file.length(),
                    "r",
                    file.getName());
        }
        return result;
    }

    private String listFormatImplementation(File file, FileFilter fileFilter) {
        String result = "";
        System.out.println(file.getName());
        File[] files = fileFilter == null ? file.listFiles() : file.listFiles(fileFilter);
        if (files.length == 0) {
            return "";
        }
        for (File f : files) {
            result += format(f);

        }
        return result;
    }

    public String listFormat(File file) {
        return listFormatImplementation(file, null);
    }

    public String listFormat(File file, FileFilter fileFilter) {
        return listFormatImplementation(file, fileFilter);
    }

    public String formatSingleFile(File file) {
        return format(file);
    }
}
