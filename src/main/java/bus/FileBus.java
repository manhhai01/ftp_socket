/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bus;

import payload.GetAnonymousFilesResult;
import config.AppConfig;
import dao.DirectoryDao;
import dao.FileDao;
import payload.GetSharedFilesResultDto;
import dao.ShareDirectoriesDao;
import dao.ShareFilesDao;
import dao.UserDao;
import ftp.FilePermission;
import ftp.FtpFileUtils;
import ftp.commands.AnonymousDisabledException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Directory;
import model.ShareDirectories;
import model.ShareFiles;
import model.User;
import model.ids.ShareDirectoriesId;
import model.ids.ShareFilesId;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author User
 */
public class FileBus {

    private final FileDao fileDao = new FileDao();
    private final ShareFilesDao shareFilesDao = new ShareFilesDao();
    private final DirectoryDao directoryDao = new DirectoryDao();
    private final ShareDirectoriesDao shareDirectoriesDao = new ShareDirectoriesDao();
    private final UserDao userDao = new UserDao();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();

    public boolean createNormalFile(String fromRootFilePath, String username) {
        User user = userDao.getUserByUsername(username);

        // Check if upload is allowed
        String parentDirPath = new FtpFileUtils().getParentPath(fromRootFilePath);
        FilePermission parentPermission = getFilePermission(parentDirPath, username);
        if (!parentPermission.isWritable()) {
            return false;
        }

        File file = new File(fromRootFilePath);
        if (file.exists()) {
            return true;
        }

        boolean success = fileDao.save(new model.File(0, fromRootFilePath, user, null));

        if (success) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileBus.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return success;
    }

    public boolean createHomeDirectoryIfNotExist(String username) {
        String fromRootPath = ftpFileUtils.joinPath(AppConfig.SERVER_FTP_FILE_PATH, username);
        User user = userDao.getUserByUsername(username);
        Directory homeDir = directoryDao.getDirectoryByPath(fromRootPath);
        if (homeDir != null) {
            return true;
        }

        boolean success = directoryDao.save(new Directory(0, fromRootPath, user, null));
        File file = new File(fromRootPath);
        if (success) {
            file.mkdir();
        }
        return success;
    }

    public boolean createDirectory(String fromRootPath, String username) {
        User user = userDao.getUserByUsername(username);

        // Check if upload is allowed
        String parentDirPath = new FtpFileUtils().getParentPath(fromRootPath);
        FilePermission parentPermission = getFilePermission(parentDirPath, username);
        if (!parentPermission.isWritable()) {
            return false;
        }
        File file = new File(fromRootPath);
        if (file.exists()) {
            return true;
        }

        boolean success = directoryDao.save(new Directory(0, fromRootPath, user, null));
        if (success) {
            file.mkdir();
        }
        return success;
    }

    public boolean removeNormalFile(String fromRootFilePath, String username) {
        File file = new File(fromRootFilePath);
        if (!file.exists()) {
            return true;
        }

        if (!file.isFile()) {
            return false;
        }

        FilePermission filePermission = getFilePermission(fromRootFilePath, username);
        if (filePermission == null) {
            return true;
        }

        if (!filePermission.isDeletable()) {
            return false;
        }

        model.File fileFromDb = fileDao.getFileByPath(fromRootFilePath);
        boolean success = fileDao.remove(fileFromDb.getId());
        if (success) {
            file.delete();
        }
        return success;
    }

    public boolean writeToNormalFile(String fromRootFilePath, String username, InputStream data, String writeMode) {
        File file = new File(fromRootFilePath);
        if (!file.exists()) {
            return false;
        }

        if (!file.isFile()) {
            return false;
        }

        try {
            if (writeMode.equals("A")) {
                FileWriter fileWriter = new FileWriter(file);
                BufferedReader dataReader = new BufferedReader(new InputStreamReader(data));
                dataReader.transferTo(fileWriter);
                dataReader.close();
                fileWriter.close();
            } else {
                byte[] dataBytes = IOUtils.toByteArray(data);
                FileUtils.writeByteArrayToFile(file, dataBytes);
            }

        } catch (IOException ex) {
            Logger.getLogger(FileBus.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    public boolean removeDirectory(String fromRootPath, String username) {
        File file = new File(fromRootPath);
        if (!file.exists()) {
            return true;
        }

        if (!file.isDirectory()) {
            return false;
        }

        if (file.list().length > 0) {
            return false;
        }

        FilePermission filePermission = getFilePermission(fromRootPath, username);
        if (filePermission == null) {
            return true;
        }

        if (!filePermission.isDeletable()) {
            return false;
        }

        Directory directoryFromDb = directoryDao.getDirectoryByPath(fromRootPath);
        boolean success = directoryDao.remove(directoryFromDb.getId());
        if (success) {
            file.delete();
        }
        return success;
    }

    public boolean removeFile(String fromRootFilePath, String username) {
        File file = new File(fromRootFilePath);
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            return removeDirectory(fromRootFilePath, username);
        }

        return removeNormalFile(fromRootFilePath, username);
    }

    private void reparentFilePathInDb(File file, String newParentPath) {
        String filePath = file.getPath().replace("\\", "/");
        String newFilePath = ftpFileUtils.joinPath(newParentPath, file.getName());

        if (file.isFile()) {
            model.File fileInDb = fileDao.getFileByPath(filePath);
            fileInDb.setPath(newFilePath);
            fileDao.update(fileInDb);
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            Directory directory = directoryDao.getDirectoryByPath(filePath);
            directory.setPath(newFilePath);
            directoryDao.update(directory);

            if (childFiles.length == 0) {
                return;
            }

            for (File child : childFiles) {
                reparentFilePathInDb(child, newFilePath);
            }
        }
    }

    public boolean changeFilePath(String oldFilePath, String newFilePath, String username) {
        File file = new File(oldFilePath);
        if (!file.exists()) {
            return false;
        }

        FilePermission filePermission = getFilePermission(oldFilePath, username);
        if (!filePermission.isRenamable()) {
            return false;
        }

        // Check if uploadable
        String parentPath = ftpFileUtils.getParentPath(newFilePath);
        FilePermission parentDirPermission = getFilePermission(parentPath, username);
        if (!parentDirPermission.isWritable()) {
            return false;
        }

        File destination = new File(newFilePath);
        reparentFilePathInDb(file, parentPath);
        file.renameTo(destination);

        return true;
    }

    public boolean setShareFilePermission(String fromRootFilePath, String username, boolean isReadable, boolean isWritable) {
        File file = new File(fromRootFilePath);
        if (!file.exists()) {
            return false;
        }

        FilePermission filePermission = getFilePermission(fromRootFilePath, username);
        if (filePermission == null) {
            return false;
        }

        if (!filePermission.isOwner()) {
            return false;
        }

        if (file.isFile()) {
            model.File fileInDb = fileDao.getFileByPath(fromRootFilePath);
            User user = userDao.getUserByUsername(username);
            shareFilesDao.update(
                    new ShareFiles(
                            new ShareFilesId(fileInDb.getId(), user.getId()),
                            isReadable,
                            isWritable,
                            fileInDb,
                            user)
            );
            return true;
        }

        if (file.isDirectory()) {
            Directory directoryInDb = directoryDao.getDirectoryByPath(fromRootFilePath);
            User user = userDao.getUserByUsername(username);
            shareDirectoriesDao.update(
                    new ShareDirectories(
                            new ShareDirectoriesId(directoryInDb.getId(), user.getId()),
                            isWritable,
                            isReadable,
                            directoryInDb,
                            user)
            );
            return true;
        }

        return false;
    }

    public GetSharedFilesResultDto getSharedFiles(String appliedUsername) {
        return userDao.getSharedFiles(appliedUsername);
    }

    // Todo: Bug
    private FilePermission getDetailedFilePermission(String fromRootFilePath, String username) {
        File file = new File(fromRootFilePath);

        if (fromRootFilePath.equals(AppConfig.SERVER_FTP_FILE_PATH)) {
            return new FilePermission(fromRootFilePath, true, false, false, false, "", username);
        }

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
                        !directoryFromDb.getPath().equals(AppConfig.SERVER_FTP_FILE_PATH + username),
                        !directoryFromDb.getPath().equals(AppConfig.SERVER_FTP_FILE_PATH + username),
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

            // Return if share permission can't be found
            if (userPermission == null) {
                return detailedFilePermission;
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
                if (currentFilePath.equals(AppConfig.SERVER_FTP_FILE_PATH)) {
                    break;
                }
                currentFilePath = ftpFileUtils.getParentPath(currentFilePath);

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

        if (pathTokens.size()
                == 1) {
            return getDetailedFilePermissionRecursively("/", username);
        }
        pathTokens = pathTokens.subList(0, pathTokens.size() - 2);

        return getDetailedFilePermissionRecursively(
                "/" + String.join("/", pathTokens), username);
    }

    public FilePermission getFilePermission(String fromRootFilePath, String username) {
        System.out.println("From root path: " + fromRootFilePath);
        FilePermission storedFilePermission = getDetailedFilePermissionRecursively(fromRootFilePath, username);
        return storedFilePermission;
    }

    public GetAnonymousFilesResult getAnonymousFiles(String username) throws AnonymousDisabledException {
        GetAnonymousFilesResult result = new GetAnonymousFilesResult();
        result.files = new ArrayList<>();
        result.directories = new ArrayList<>();
        
        User user = userDao.getUserByUserName(username);
        if(!user.isAnonymous()) {
            throw new AnonymousDisabledException();
        }
        File file = new File(AppConfig.SERVER_FTP_ANON_PATH);
        if(!file.exists()) {
            file.mkdir();
        }
        
        File[] files = file.listFiles();
        for(File f: files) {
            String filePath = ftpFileUtils.convertJavaPathToFtpPath(f.getPath());
            if(f.isDirectory()) {
                Directory dir = directoryDao.getDirectoryByPath(filePath);
                result.directories.add(dir);
            }
            if(f.isFile()) {
                model.File fileFromDb = fileDao.getFileByPath(filePath);
                result.files.add(fileFromDb);
            }
        }
        
        return result;
    }
}
