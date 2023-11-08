/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bus;

import static bus.FileBus.DIRECTORY_TYPE;
import static bus.FileBus.NORMAL_FILE_TYPE;
import dao.FileDao;
import dao.ShareFilesDao;
import dao.UserDao;
import ftp.DirectoryPermission;
import ftp.FtpFileUtils;
import ftp.NormalFilePermission;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ShareFiles;
import model.User;
import model.ids.ShareFilesId;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author User
 */
public class NormalFileBus {

    private static final FileDao fileDao = new FileDao();
    private static final ShareFilesDao shareFilesDao = new ShareFilesDao();
    private static final UserDao userDao = new UserDao();
    private static final FileBus fileBus = new FileBus();

    public boolean createNormalFile(String fromRootFilePath, String username) {
        User user = userDao.getUserByUsername(username);

        // Check if upload is allowed
        String parentDirPath = new FtpFileUtils().getParentPath(fromRootFilePath);
        DirectoryPermission parentPermission = (DirectoryPermission) fileBus.getFilePermission(
                parentDirPath,
                username,
                DIRECTORY_TYPE
        );
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

    public boolean removeNormalFile(String fromRootFilePath, String username) {
        File file = new File(fromRootFilePath);
        if (!file.exists()) {
            return true;
        }

        if (!file.isFile()) {
            return false;
        }

        NormalFilePermission filePermission = (NormalFilePermission) fileBus.getFilePermission(
                fromRootFilePath,
                username,
                NORMAL_FILE_TYPE
        );
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
        NormalFilePermission filePermission = (NormalFilePermission) fileBus.getFilePermission(
                fromRootFilePath,
                username,
                NORMAL_FILE_TYPE
        );
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

    public boolean setShareNormalFilePermission(String fromRootFilePath, String ownerUsername, String appliedUsername, String permission) {
        model.File fileInDb = fileDao.getFileByPath(fromRootFilePath);
        if (fileInDb == null) {
            return false;
        }

        if (!ownerUsername.equals(fileInDb.getUser().getUsername())) {
            return false;
        }

        User appliedUser = userDao.getUserByUserName(appliedUsername);

        boolean success = shareFilesDao.update(
                new ShareFiles(
                        new ShareFilesId(fileInDb.getId(), appliedUser.getId()),
                        permission,
                        fileInDb,
                        appliedUser)
        );

        // Todo: Send mail
        return success;

    }

    public boolean unshareNormalFile(String fromRootFilePath, String ownerUsername, String appliedUsername) {
        model.File fileInDb = fileDao.getFileByPath(fromRootFilePath);
        if (fileInDb == null) {
            return false;
        }

        if (!ownerUsername.equals(fileInDb.getUser().getUsername())) {
            return false;
        }

        User appliedUser = userDao.getUserByUserName(appliedUsername);

        ShareFiles shareFile = new ShareFiles();
        shareFile.setIds(new ShareFilesId(fileInDb.getId(), appliedUser.getId()));

        boolean success = shareFilesDao.remove(shareFile);
        return success;
    }
}
