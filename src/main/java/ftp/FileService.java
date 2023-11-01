/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp;

import dao.DirectoryDao;
import dao.FileDao;
import dao.ShareDirectoriesDao;
import dao.ShareFilesDao;
import dao.UserDao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class FileService {

    private final FileDao fileDao = new FileDao();
    private final ShareFilesDao shareFilesDao = new ShareFilesDao();
    private final DirectoryDao directoryDao = new DirectoryDao();
    private final ShareDirectoriesDao shareDirectoriesDao = new ShareDirectoriesDao();
    private final UserDao userDao = new UserDao();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();
    private final FilePermissionService filePermissionService = new FilePermissionService();

    public boolean createNormalFile(String fromRootFilePath, String username) {
        User user = userDao.getUserByUsername(username);

        // Check if upload is allowed
        String parentDirPath = new FtpFileUtils().getParentPath(fromRootFilePath);
        FilePermission parentPermission = filePermissionService.getFilePermission(parentDirPath, username);
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
                Logger.getLogger(FilePermissionService.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return success;
    }

    public boolean createDirectory(String fromRootPath, String username) {
        User user = userDao.getUserByUsername(username);

        // Check if upload is allowed
        String parentDirPath = new FtpFileUtils().getParentPath(fromRootPath);
        FilePermission parentPermission = filePermissionService.getFilePermission(parentDirPath, username);
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

        FilePermission filePermission = filePermissionService.getFilePermission(fromRootFilePath, username);
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
            Logger.getLogger(FilePermissionService.class.getName()).log(Level.SEVERE, null, ex);
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

        FilePermission filePermission = filePermissionService.getFilePermission(fromRootPath, username);
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

        FilePermission filePermission = filePermissionService.getFilePermission(oldFilePath, username);
        if (!filePermission.isRenamable()) {
            return false;
        }

        // Check if uploadable
        String parentPath = ftpFileUtils.getParentPath(newFilePath);
        FilePermission parentDirPermission = filePermissionService.getFilePermission(parentPath, username);
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

        FilePermission filePermission = filePermissionService.getFilePermission(fromRootFilePath, username);
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
}
