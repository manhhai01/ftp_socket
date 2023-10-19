/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class FilePermissionService {
    // Placeholder
    private Map<String, FilePermission> filePermissions = new HashMap<>();
    
    public FilePermissionService() {
        filePermissions.put("ftp", new FilePermission(true, false, false, false));
        filePermissions.put("ftp/testuser", new FilePermission(true, false, false, false));
        filePermissions.put("ftp/testuser/aaaa", new FilePermission(true, false, true, true));
        filePermissions.put("ftp/testuser/aaaa/test.txt", new FilePermission(true, true, true, true));
        filePermissions.put("ftp/testuser/abc", new FilePermission(true, false, true, true));
        filePermissions.put("ftp/testuser/def", new FilePermission(true, false, true, true));
        filePermissions.put("ftp/testuser2", new FilePermission(true, false, false, false));
        filePermissions.put("ftp/testuser/log-login-successfully.txt", new FilePermission(true, true, true, false));
        filePermissions.put("ftp/testuser/log2.txt", new FilePermission(true, false, true, false));
        
    }
    
    public FilePermission getFilePermission(String fromRootFilePath, String username) {
        System.out.println("From root path: " + fromRootFilePath);
        FilePermission filePermission = filePermissions.get(fromRootFilePath);
        if(filePermission == null) {
            filePermission = new FilePermission(false, false, false, false);
        }
        return filePermission;
    }
}
