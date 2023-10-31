package ftp;

import dao.DirectoryDao;
import dao.FileDao;
import dao.ShareDirectoriesDao;
import dao.ShareFilesDao;
import dao.UserDao;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import model.Directory;
import model.ShareDirectories;
import model.ShareFiles;
import model.User;
import model.ids.ShareDirectoriesId;
import model.ids.ShareFilesId;

public class FilePermissionService {

    private final FileDao fileDao = new FileDao();
    private final ShareFilesDao shareFilesDao = new ShareFilesDao();
    private final DirectoryDao directoryDao = new DirectoryDao();
    private final ShareDirectoriesDao shareDirectoriesDao = new ShareDirectoriesDao();
    private final UserDao userDao = new UserDao();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();

    private FilePermission getDetailedFilePermission(String fromRootFilePath, String username) {
        File file = new File(fromRootFilePath);

        if (file.isDirectory()) {
            System.out.println("Fetching directory: " + fromRootFilePath);
            Directory directoryFromDb = directoryDao.getDirectoryByPath(fromRootFilePath);
            if (directoryFromDb == null) {
                return null;
            }

            // Return full permission if the user is directory's owner
            if (directoryFromDb.getUser().getUsername().equals(username)) {
                return new FilePermission(
                        directoryFromDb.getPath(),
                        true,
                        true,
                        // Always renamable and deletable to owner unless it's the home folder
                        !directoryFromDb.getPath().equals("ftp/" + username),
                        !directoryFromDb.getPath().equals("ftp/" + username),
                        username,
                        username
                );
            }

            // Setup path and owner for file permission
            FilePermission detailedFilePermission = new FilePermission();
            detailedFilePermission.setPath(directoryFromDb.getPath());
            detailedFilePermission.setOwner(directoryFromDb.getUser().getUsername());

            // Get directory's share permission
            List<ShareDirectories> directoryPermissions = directoryFromDb.getShareDirectories();
            ShareDirectories userPermission = directoryPermissions.stream()
                    .filter(permission -> permission.getUser().getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            // Return null if share permission can't be found
            if (userPermission == null) {
                return null;
            }

            // Get file read/write permission and set applied user
            detailedFilePermission.setReadable(userPermission.isDownloadPermission());
            detailedFilePermission.setWritable(userPermission.isUploadPermission());
            detailedFilePermission.setAppliedUser(username);

            return detailedFilePermission;
        }

        if (file.isFile()) {
            System.out.println("Fetching file: " + fromRootFilePath);
            // Fetch from file dao
            model.File fileFromDb = fileDao.getFileByPath(fromRootFilePath);
            if (fileFromDb == null) {
                return null;
            }

            // Return full permission if the user is file's owner
            if (fileFromDb.getUser().getUsername().equals(username)) {
                return new FilePermission(
                        fileFromDb.getPath(),
                        true,
                        true,
                        true,
                        true,
                        username,
                        username
                );
            }

            // Setup path and owner for file permission
            FilePermission detailedFilePermission = new FilePermission();
            detailedFilePermission.setPath(fileFromDb.getPath());
            detailedFilePermission.setOwner(fileFromDb.getUser().getUsername());

            // Get file's share permission
            List<ShareFiles> filePermissions = fileFromDb.getShareFiles();
            ShareFiles userPermission = filePermissions.stream()
                    .filter(permission -> permission.getUser().getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            // Return empty permission if share permission can't be found
            if (userPermission == null) {
                return null;
            }

            // Get file read/write permission and set applied user
            detailedFilePermission.setReadable(userPermission.isReadPermission());
            detailedFilePermission.setWritable(userPermission.isWritePermission());
            detailedFilePermission.setAppliedUser(username);

            return detailedFilePermission;
        }

        return null;
    }

    private FilePermission getDetailedFilePermissionRecursively(String fromRootFilePath, String username) {
        FtpFileUtils ftpFileUtils = new FtpFileUtils();
        FilePermission filePermission;
        filePermission = getDetailedFilePermission(fromRootFilePath, username);
        boolean isDeletable = false;

        if (filePermission != null) {
            // Owner has full permission
            if (username.equals(filePermission.getOwner())) {
                return filePermission;
            }

            String currentFilePath = fromRootFilePath;

            // Deletable if the user is the owner of one of the folders containing the file
            while (true) {
                if (currentFilePath.equals("/")) {
                    break;
                }
                currentFilePath = ftpFileUtils.getParentPath(fromRootFilePath);

                // Get parent directory permission
                FilePermission parentFilePermission = getDetailedFilePermission(currentFilePath, username);

                // If user is owner, set deletable to true
                if (parentFilePermission.getOwner().equals(username)) {
                    isDeletable = true;
                    break;
                }
            }
            filePermission.setDeletable(isDeletable);
            return filePermission;
        }

        // If not found then the closest parent directory's permission is also the file permission
        // Go to parent directory
        List<String> pathTokens = Arrays.asList(fromRootFilePath.split("/"));
        if (pathTokens.isEmpty()) {
            return null;
        }
        if (pathTokens.size() == 1) {
            return getDetailedFilePermissionRecursively("/", username);
        }
        pathTokens = pathTokens.subList(0, pathTokens.size() - 2);
        return getDetailedFilePermissionRecursively("/" + String.join("/", pathTokens), username);
    }

    public FilePermission getFilePermission(String fromRootFilePath, String username) {
        System.out.println("From root path: " + fromRootFilePath);
        FilePermission storedFilePermission = getDetailedFilePermissionRecursively(fromRootFilePath, username);
        return storedFilePermission;
    }
}
