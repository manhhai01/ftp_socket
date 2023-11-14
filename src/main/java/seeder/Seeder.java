package seeder;

import config.AppConfig;
import dao.DirectoryDao;
import dao.FileDao;
import dao.ShareDirectoriesDao;
import dao.ShareFilesDao;
import dao.UserDao;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import model.Directory;
import model.ShareDirectories;
import model.ShareFiles;
import model.User;
import model.ids.ShareDirectoriesId;
import utils.MP5Utils;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author User
 */
public class Seeder {

    private static DirectoryDao directoryDao = new DirectoryDao();
    private static FileDao fileDao = new FileDao();
    private static UserDao userDao = new UserDao();
    private static ShareDirectoriesDao shareDirectoriesDao = new ShareDirectoriesDao();
    private static ShareFilesDao shareFilesDao = new ShareFilesDao();

    private static Directory createDir(String path, User user, List<ShareDirectories> sharePermissions) throws IOException {
        Directory directory = new Directory(0, path, user, sharePermissions);
        directoryDao.save(directory);
        File file = new File(path);
        file.mkdirs();
        return directory;
    }

    private static model.File createFile(String path, String content, User user, List<ShareFiles> sharePermissions) throws IOException {
        model.File fileModel = new model.File(0, path, user, sharePermissions);
        fileDao.save(fileModel);
        File file = new File(path);
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.close();
        return fileModel;
    }

    public static void main(String[] args) throws IOException, ParseException {
        MP5Utils md5Utils = new MP5Utils();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

//        User rootUser = new User();
//        rootUser.setUsername("root");
//        rootUser.setPassword(md5Utils.getMD5Hash("root"));
//        rootUser.setIsActive(1);
//        userDao.save(rootUser);
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setPassword(md5Utils.getMD5Hash("test"));
        user1.setBirthdate(dateFormat.parse("31/6/1998"));
        user1.setFirstName("An");
        user1.setLastName("Tran Van");
        user1.setGender("Nam");
        user1.setIsActive(1);
        user1.setMaxDownloadFileSizeBytes(300000000); // 300 MB
        user1.setMaxUploadFileSizeBytes(300000000); // 300 MB
        user1.setQuotaInBytes(1000000000); // 1 GB
        userDao.save(user1);

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setPassword(md5Utils.getMD5Hash("test2"));
        user2.setBirthdate(dateFormat.parse("22/11/1996"));
        user2.setFirstName("Tuyet");
        user2.setLastName("Le Thi");
        user2.setGender("Nữ");
        user2.setIsActive(1);
        user2.setMaxDownloadFileSizeBytes(300000000); // 300 MB
        user2.setMaxUploadFileSizeBytes(300000000); // 300 MB
        user2.setQuotaInBytes(1000000000); // 1 GB
        userDao.save(user2);

        File file = new File(AppConfig.SERVER_FTP_ANON_PATH);
        file.mkdirs();

//        createDir(AppConfig.SERVER_FTP_USERS_PATH, rootUser, null);
        Directory userHomeDirectory = createDir(AppConfig.SERVER_FTP_USERS_PATH + "/testuser", user1, null);
        createDir(AppConfig.SERVER_FTP_USERS_PATH + "/testuser/aaaa", user1, null);
        createDir(AppConfig.SERVER_FTP_USERS_PATH + "/testuser/abc", user1, null);
        createDir(AppConfig.SERVER_FTP_USERS_PATH + "/testuser/def", user1, null);
        createDir(AppConfig.SERVER_FTP_USERS_PATH + "/testuser2", user2, null);

        model.File sharedFile1 = createFile(AppConfig.SERVER_FTP_USERS_PATH + "/testuser/aaaa/test.txt", "Hello", user1, null);
        model.File sharedFile2 = createFile(AppConfig.SERVER_FTP_USERS_PATH + "/testuser/log-login-successfully.txt", "log 111", user1, null);
        createFile(AppConfig.SERVER_FTP_USERS_PATH + "/testuser/log2.txt", "log 112", user1, null);

        shareDirectoriesDao.save(new ShareDirectories(new ShareDirectoriesId(userHomeDirectory.getId(), user2.getId()), false, false, true, userHomeDirectory, user2));
    }
}
