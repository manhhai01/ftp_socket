/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bus;

import static bus.FileBus.DIRECTORY_TYPE;
import config.AppConfig;
import dao.DirectoryDao;
import dao.FileDao;
import dao.ShareDirectoriesDao;
import dao.ShareFilesDao;
import dao.UserDao;
import ftp.DirectoryPermission;
import ftp.FtpFileUtils;
import java.io.File;
import model.Directory;
import model.ShareDirectories;
import model.User;
import model.ids.ShareDirectoriesId;

/**
 *
 * @author User
 */
public class DirectoryBus {

    private final FileBus fileBus = new FileBus();
    private final FileDao fileDao = new FileDao();
    private final DirectoryDao directoryDao = new DirectoryDao();
    private final ShareDirectoriesDao shareDirectoriesDao = new ShareDirectoriesDao();
    private final UserDao userDao = new UserDao();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();

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
        DirectoryPermission parentPermission = (DirectoryPermission) fileBus.getFilePermission(
                parentDirPath,
                username,
                DIRECTORY_TYPE
        );
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

        DirectoryPermission directoryPermission = (DirectoryPermission) fileBus.getFilePermission(
                fromRootPath,
                username,
                DIRECTORY_TYPE
        );

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

    public boolean setShareDirectoryPermission(String fromRootDirPath, String ownerUsername, String appliedUsername, boolean canModify, boolean uploadable, boolean downloadable) {
        Directory directoryInDb = directoryDao.getDirectoryByPath(fromRootDirPath);
        if (directoryInDb == null) {
            return false;
        }

        if (!ownerUsername.equals(directoryInDb.getUser().getUsername())) {
            return false;
        }

        User appliedUser = userDao.getUserByUserName(appliedUsername);

        boolean success = shareDirectoriesDao.update(
                new ShareDirectories(
                        new ShareDirectoriesId(directoryInDb.getId(), appliedUser.getId()),
                        canModify,
                        uploadable,
                        downloadable,
                        directoryInDb,
                        appliedUser)
        );
        return success;
    }

    public boolean unshareDirectory(String fromRootDirPath, String ownerUsername, String appliedUsername) {
        Directory directoryInDb = directoryDao.getDirectoryByPath(fromRootDirPath);
        if (directoryInDb == null) {
            return false;
        }

        if (!ownerUsername.equals(directoryInDb.getUser().getUsername())) {
            return false;
        }

        User appliedUser = userDao.getUserByUserName(appliedUsername);

        ShareDirectories shareDirectory = new ShareDirectories();
        shareDirectory.setIds(new ShareDirectoriesId(directoryInDb.getId(), appliedUser.getId()));

        boolean success = shareDirectoriesDao.remove(shareDirectory);
        return success;
    }
}