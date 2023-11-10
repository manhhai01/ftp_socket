/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bus;

import static bus.FileBus.DIRECTORY_TYPE;
import static bus.FileBus.NORMAL_FILE_TYPE;
import config.AppConfig;
import dao.FileDao;
import dao.ShareFilesDao;
import dao.UserDao;
import ftp.DirectoryPermission;
import ftp.FtpFileUtils;
import ftp.NormalFilePermission;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private File createTempFile(String fromRootDirPath) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyhhmmssSSS");
        String fileName = simpleDateFormat.format(new Date());
        File file = new File(fromRootDirPath + "/" + fileName);
        file.createNewFile();
        return file;
    }

    public static void main(String[] args) throws IOException {
        NormalFileBus normalFileBus = new NormalFileBus();
        normalFileBus.createTempFile(new FtpFileUtils().getParentPath(AppConfig.SERVER_FTP_USERS_PATH + "/" + "testuser.txt"));
    }

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

        if (user.getUsedKb() >= user.getQuotaInKb()) {
            return false;
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
            long fileSize = (long) (file.length() / 1000.0);
            file.delete();

            User user = userDao.getUserByUserName(username);
            float usedKb = user.getUsedKb();
            user.setUsedKb(usedKb - fileSize);
            userDao.update(user);
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

        FtpFileUtils ftpFileUtils = new FtpFileUtils();
        User user = userDao.getUserByUserName(username);
        String folderPath = ftpFileUtils.getParentPath(fromRootFilePath);
        File tempFile;
        
        // Create temp file
        try {
            tempFile = createTempFile(folderPath);
        } catch (IOException ex) {
            return false;
        }

        // Write to temp file so we can check the size of the file later
        try {
            if (writeMode.equals("A")) {
                FileWriter fileWriter = new FileWriter(tempFile);
                BufferedReader dataReader = new BufferedReader(new InputStreamReader(data));
                dataReader.transferTo(fileWriter);
                dataReader.close();
                fileWriter.close();
            } else {
                byte[] dataBytes = IOUtils.toByteArray(data);
                FileUtils.writeByteArrayToFile(tempFile, dataBytes);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileBus.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        // Check max upload size
        long oldFileSize = (long) (file.length() / 1000.0);
        long newFileSize = (long) (tempFile.length() / 1000.0);
        if (newFileSize > user.getMaxUploadFileSizeKb()) {
            tempFile.delete();
            return false;
        }
        
        // Check if exceed quota
        long newUsedKb = (long) (newFileSize - oldFileSize + user.getUsedKb());
        if (newUsedKb > user.getQuotaInKb()) {
            tempFile.delete();
            return false;
        }

        // Overwrite to real file
        try {
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(NormalFileBus.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        // Update used kb
        user.setUsedKb(newUsedKb);
        userDao.update(user);
        
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
