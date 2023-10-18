/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ltudm.ftp.server;

import java.io.File;
import java.nio.file.Files;

/**
 *
 * @author User
 */
public class MLSDFormatter {

    public String format(File file) {
        String result = "";
        System.out.println(file.getName());
        File[] files = file.listFiles();
        if(files.length == 0) {
            return "";
        }
        for (File f : file.listFiles()) {
            if(f.isDirectory()) {
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
}
