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
import ftp.DirectoryPermission;
import ftp.FilePermission;
import ftp.FtpFileUtils;
import ftp.NormalFilePermission;
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

    public static final String DIRECTORY_TYPE = "directory";
    public static final String NORMAL_FILE_TYPE = "file";

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
        DirectoryPermission parentPermission = (DirectoryPermission) getFilePermission(parentDirPath, username, DIRECTORY_TYPE);
        if (!parentPermission.isUploadable()) {
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
        DirectoryPermission parentPermission = (DirectoryPermission) getFilePermission(parentDirPath, username, DIRECTORY_TYPE);
        if (!parentPermission.isUploadable()) {
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

        NormalFilePermission filePermission = (NormalFilePermission) getFilePermission(fromRootFilePath, username, NORMAL_FILE_TYPE);
        if (!filePermission.isExist()) {
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
        NormalFilePermission filePermission = (NormalFilePermission) getFilePermission(fromRootFilePath, username, NORMAL_FILE_TYPE);
        if (!file.exists()) {
            return false;
        }

        if (!file.isFile()) {
            return false;
        }

        if (!filePermission.isWritable()) {
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

        DirectoryPermission directoryPermission = (DirectoryPermission) getFilePermission(fromRootPath, username, DIRECTORY_TYPE);

        if (!directoryPermission.isDeletable()) {
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

    public boolean changeFilePath(String oldFilePath, String newFilePath, String username, String fileType) {
        File file = new File(oldFilePath);
        if (!file.exists()) {
            return false;
        }

        FilePermission filePermission = getFilePermission(oldFilePath, username, fileType);
        if (!filePermission.isRenamable()) {
            return false;
        }

        // Check if uploadable
        String parentPath = ftpFileUtils.getParentPath(newFilePath);
        DirectoryPermission parentDirPermission = (DirectoryPermission) getFilePermission(parentPath, username, DIRECTORY_TYPE);
        if (!parentDirPermission.isUploadable()) {
            return false;
        }

        File destination = new File(newFilePath);
        reparentFilePathInDb(file, parentPath);
        file.renameTo(destination);

        return true;
    }

    public boolean setShareNormalFilePermission(String fromRootFilePath, String username, String permission) {
        model.File fileInDb = fileDao.getFileByPath(fromRootFilePath);
        if (fileInDb == null) {
            return false;
        }
        User user = userDao.getUserByUsername(username);
        if (!username.equals(fileInDb.getUser().getUsername())) {
            return false;
        }
        boolean success = shareFilesDao.update(
                new ShareFiles(
                        new ShareFilesId(fileInDb.getId(), user.getId()),
                        permission,
                        fileInDb,
                        user)
        );
        return success;

    }

    public boolean setShareDirectoryPermission(String fromRootDirPath, String username, boolean canModify, boolean uploadable, boolean downloadable) {
        Directory directoryInDb = directoryDao.getDirectoryByPath(fromRootDirPath);
        if (directoryInDb == null) {
            return false;
        }
        User user = userDao.getUserByUsername(username);
        if (!username.equals(directoryInDb.getUser().getUsername())) {
            return false;
        }
        boolean success = shareDirectoriesDao.update(
                new ShareDirectories(
                        new ShareDirectoriesId(directoryInDb.getId(), user.getId()),
                        canModify,
                        uploadable,
                        downloadable,
                        directoryInDb,
                        user)
        );
        return success;
    }

    public GetSharedFilesResultDto getSharedFiles(String appliedUsername) {
        return userDao.getSharedFiles(appliedUsername);
    }

    // Todo: Bug
    private FilePermission getSingleFilePermission(String fromRootFilePath, String username, String fileType) {
        // FTP root case
        if (fromRootFilePath.equals(AppConfig.SERVER_FTP_FILE_PATH)) {
            return new DirectoryPermission(false, false, false, true);
        }

        // Directory case
        if (fileType.equals(DIRECTORY_TYPE)) {
            System.out.println("Fetching directory: " + fromRootFilePath);
            Directory directoryFromDb = directoryDao.getDirectoryByPath(fromRootFilePath);
            if (directoryFromDb == null) {
                return new DirectoryPermission(false, false, false, false);
            }

            // Return full permission if the user is directory's owner
            if (directoryFromDb.getUser().getUsername().equals(username)) {
                return new DirectoryPermission(true, true, true, true);
            }

            // Get directory's share permission
            List<ShareDirectories> directoryPermissions = directoryFromDb.getShareDirectories();
            ShareDirectories userPermission = directoryPermissions.stream()
                    .filter(permission -> permission.getUser().getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            // Return if share permission can't be found
            if (userPermission == null) {
                return new DirectoryPermission(false, false, false, true);
            }

            // Set directory share permission
            DirectoryPermission directoryPermission = new DirectoryPermission();
            directoryPermission.setDownloadable(userPermission.isDownloadPermission());
            directoryPermission.setUploadable(userPermission.isUploadPermission());

            return directoryPermission;
        } // Normal file case
        else {
            System.out.println("Fetching file: " + fromRootFilePath);
            // Fetch from file dao
            model.File fileFromDb = fileDao.getFileByPath(fromRootFilePath);
            if (fileFromDb == null) {
                return new NormalFilePermission(NormalFilePermission.NULL_PERMISSION, false);
            }

            // Return full permission if the user is file's owner
            if (fileFromDb.getUser().getUsername().equals(username)) {
                return new NormalFilePermission(NormalFilePermission.FULL_PERMISSION, true);
            }

            // Get file's share permission
            List<ShareFiles> filePermissions = fileFromDb.getShareFiles();
            ShareFiles userPermission = filePermissions.stream()
                    .filter(permission -> permission.getUser().getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            // Return null permission if share permission can't be found
            if (userPermission == null) {
                return new NormalFilePermission(NormalFilePermission.NULL_PERMISSION, true);
            }

            // Sset file read/full permission
            NormalFilePermission normalFilePermission = new NormalFilePermission(NormalFilePermission.NULL_PERMISSION, true);
            if (userPermission.getPermission().equals(NormalFilePermission.READABLE_PERMISSION)) {
                normalFilePermission.setPermission(NormalFilePermission.READABLE_PERMISSION);
            }
            if (userPermission.getPermission().equals(NormalFilePermission.FULL_PERMISSION)) {
                normalFilePermission.setPermission(NormalFilePermission.FULL_PERMISSION);
            }
            return normalFilePermission;
        }

    }

    public FilePermission getFilePermission(String fromRootFilePath, String username, String fileType) {
        FilePermission filePermission;
        filePermission = getSingleFilePermission(fromRootFilePath, username, fileType);

        if (!filePermission.isExist()) {
            return filePermission;
        }

        if (filePermission.isShared()) {
            return filePermission;
        }

        // If the file/directory isn't shared then look up whether one of its parent directories is shared
        String parentPath = fromRootFilePath;
        DirectoryPermission parentDirPermission = new DirectoryPermission(false, false, false, true);
        while (!parentDirPermission.isShared()) {
            if (parentPath.equals(AppConfig.SERVER_FTP_FILE_PATH)) {
                parentDirPermission = new DirectoryPermission(false, false, false, true);
                break;
            }
            parentPath = ftpFileUtils.getParentPath(parentPath);
            parentDirPermission = (DirectoryPermission) getSingleFilePermission(parentPath, username, DIRECTORY_TYPE);
        }

        if (fileType.equals(DIRECTORY_TYPE)) {
            return parentDirPermission;
        } else {
            String permission;
            if (parentDirPermission.isDownloadable() && parentDirPermission.isUploadable()) {
                permission = NormalFilePermission.FULL_PERMISSION;
            } else if (parentDirPermission.isDownloadable()) {
                permission = NormalFilePermission.READABLE_PERMISSION;
            } else {
                permission = NormalFilePermission.NULL_PERMISSION;
            }
            return new NormalFilePermission(permission, true);
        }

    }

    public GetAnonymousFilesResult getAnonymousFiles(String username) throws AnonymousDisabledException {
        GetAnonymousFilesResult result = new GetAnonymousFilesResult();
        result.files = new ArrayList<>();
        result.directories = new ArrayList<>();

        User user = userDao.getUserByUserName(username);
        if (!user.isAnonymous()) {
            throw new AnonymousDisabledException();
        }
        File file = new File(AppConfig.SERVER_FTP_ANON_PATH);
        if (!file.exists()) {
            file.mkdir();
        }

        File[] files = file.listFiles();
        for (File f : files) {
            String filePath = ftpFileUtils.convertJavaPathToFtpPath(f.getPath());
            if (f.isDirectory()) {
                Directory dir = directoryDao.getDirectoryByPath(filePath);
                result.directories.add(dir);
            }
            if (f.isFile()) {
                model.File fileFromDb = fileDao.getFileByPath(filePath);
                result.files.add(fileFromDb);
            }
        }

        return result;
    }
    
    
}
