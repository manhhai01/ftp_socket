package ftp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

record StoredFilePermission(String path, boolean isReadable, boolean isWritable, boolean isDeletable, boolean isRenamable, String owner, String appliedUser) {

}

public class FilePermissionService {

    // Placeholder
    private static List<StoredFilePermission> filePermissions = new ArrayList<>();

    public FilePermissionService() {
        filePermissions.add(new StoredFilePermission("ftp", true, false, false, false, "testuser", "testuser"));
        filePermissions.add(new StoredFilePermission("ftp/testuser", true, true, false, false, "testuser", "testuser"));
        filePermissions.add(new StoredFilePermission("ftp/testuser/aaaa", true, true, true, true, "testuser", "testuser"));
        filePermissions.add(new StoredFilePermission("ftp/testuser/aaaa/test.txt", true, true, true, true, "testuser", "testuser"));
        filePermissions.add(new StoredFilePermission("ftp/testuser/abc", true, true, true, true, "testuser", "testuser"));
        filePermissions.add(new StoredFilePermission("ftp/testuser/def", true, true, true, true, "testuser", "testuser"));
        filePermissions.add(new StoredFilePermission("ftp/testuser2", false, false, false, false, "testuser", "testuser"));
        filePermissions.add(new StoredFilePermission("ftp/testuser/log-login-successfully.txt", true, false, false, false, "testuser", "testuser"));
        filePermissions.add(new StoredFilePermission("ftp/testuser/log2.txt", true, false, false, false, "testuser", "testuser"));

    }
    
    /**
     * Note: This method does not check if file exists or not
    */
    private StoredFilePermission getStoredFilePermission(String fromRootFilePath, String username) {
        return filePermissions.stream()
                .filter(permission -> permission.appliedUser().equals(username) && permission.path().equals(fromRootFilePath))
                .findAny()
                .orElse(null);
    }
    
    /**
     * Note: This method does not check if file exists or not
    */
    private StoredFilePermission getStoredFilePermissionRecursively(String fromRootFilePath, String username) {
        String currentFilePath = fromRootFilePath;
        StoredFilePermission filePermission;
        while((filePermission = getStoredFilePermission(fromRootFilePath, username)) == null) {
            List<String> pathTokens = Arrays.asList(currentFilePath.split("/"));
            pathTokens = pathTokens.subList(0, pathTokens.size() - 2);
            if (pathTokens.size() <= 1) {
                break;
            }
            currentFilePath = "/" + String.join("/", pathTokens);
        }
        return filePermission;
    }

    /**
     * Note: This method does not check if file exists or not
    */
    public FilePermission getFilePermission(String fromRootFilePath, String username) {
        System.out.println("From root path: " + fromRootFilePath);
        StoredFilePermission storedFilePermission = getStoredFilePermissionRecursively(fromRootFilePath, username);
        FilePermission filePermission;
        if (storedFilePermission == null) {
            filePermission = new FilePermission(false, false, false, false, false);
            return filePermission;
        }

        filePermission = new FilePermission(
                storedFilePermission.isReadable(),
                storedFilePermission.isWritable(),
                storedFilePermission.isDeletable(),
                storedFilePermission.isRenamable(),
                username.equals(storedFilePermission.owner())
        );
        return filePermission;
    }
    
    
    // Todo
    public boolean setShareDirectoryPermission(String fromRootFilePath, String username, boolean isReadable, boolean isWritable) {
//        StoredFilePermission storedFilePermission = getStoredFilePermission(fromRootFilePath, username);
//        if (storedFilePermission == null) {
//            return false;
//        }
//
//        StoredFilePermission newStoredFilePermission = new StoredFilePermission(
//                storedFilePermission.path(),
//                isReadable,
//                isWritable,
//                storedFilePermission.isDeletable(),
//                storedFilePermission.isRenamable(),
//                storedFilePermission.owner(),
//                storedFilePermission.appliedUser()
//        );
//        
//        filePermissions.add(filePermissions.indexOf(storedFilePermission), newStoredFilePermission);
//
        return true;
    }

    // Todo
    public boolean setShareFilePermission(String fromRootFilePath, String username, boolean isReadable, boolean isWritable) {
        StoredFilePermission storedFilePermission = getStoredFilePermission(fromRootFilePath, username);
        if (storedFilePermission == null) {
            return false;
        }

        StoredFilePermission newStoredFilePermission = new StoredFilePermission(
                storedFilePermission.path(),
                isReadable,
                isWritable,
                storedFilePermission.isDeletable(),
                storedFilePermission.isRenamable(),
                storedFilePermission.owner(),
                storedFilePermission.appliedUser()
        );

        filePermissions.add(filePermissions.indexOf(storedFilePermission), newStoredFilePermission);

        return true;
    }

    // Todo: owner of parent folder priviledge? owner of the file or folder priviledge?
    public boolean addFileOrDirectoryOwnerPermission(String fromRootFilePath, String username) {
        StoredFilePermission storedFilePermission = new StoredFilePermission(fromRootFilePath, true, true, true, true, username, username);
        filePermissions.add(storedFilePermission);
        return true;
    }

    public boolean changeFilePath(String oldFilePath, String newFilePath, String username) {
        ArrayList<StoredFilePermission> newFilePermissions = new ArrayList<>(
                filePermissions.stream().map(permission -> permission.owner().equals(username) && permission.path().equals(oldFilePath) ? new StoredFilePermission(
                newFilePath,
                permission.isReadable(),
                permission.isWritable(),
                permission.isDeletable(),
                permission.isRenamable(),
                permission.owner(),
                permission.appliedUser()
        ) : permission).toList());
        filePermissions = newFilePermissions;
        return true;
    }
}
