/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp;

/**
 *
 * @author User
 */
public class FtpFileUtils {
    public String joinPath(String workingDir, String... tokens) {
        if(workingDir.equals("ftp/")) {
            return "ftp/" + String.join("/", tokens);
        }
        return workingDir + "/" + String.join("/", tokens);
    }
}
