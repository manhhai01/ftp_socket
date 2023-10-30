/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author User
 */
public class FtpFileUtils {

    public String joinPath(String workingDir, String... tokens) {
        if (workingDir.equals("ftp/")) {
            return "ftp/" + String.join("/", tokens);
        }
        return workingDir + "/" + String.join("/", tokens);
    }

    public String getParentPath(String path) {
        List<String> pathTokens = Arrays.asList(path.split("/"));
        pathTokens = pathTokens.subList(0, pathTokens.size() - 1);
        if (pathTokens.isEmpty()) {
            return "/";
        }
        return String.join("/", pathTokens);
    }
}
