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
import java.util.List;
import model.Directory;
import model.ShareDirectories;
import model.ShareFiles;
import model.User;
import model.ids.ShareDirectoriesId;
import model.ids.ShareFilesId;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author User
 */

// REMEMBER TO CREATE ROOT FOLDER IN FILE SYSTEM FIRST! (for example: ftp)
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
        file.mkdir();
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

    public static void main(String[] args) throws IOException {
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setPassword("test");
        userDao.save(user1);

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setPassword("test2");
        userDao.save(user2);

        createDir(AppConfig.SERVER_FTP_FILE_PATH, null, null);
        Directory userHomeDirectory = createDir(AppConfig.SERVER_FTP_FILE_PATH + "testuser", user1, null);
        createDir(AppConfig.SERVER_FTP_FILE_PATH + "testuser/aaaa", user1, null);
        createDir(AppConfig.SERVER_FTP_FILE_PATH + "testuser/abc", user1, null);
        createDir(AppConfig.SERVER_FTP_FILE_PATH + "testuser/def", user1, null);
        createDir(AppConfig.SERVER_FTP_FILE_PATH + "testuser2", user2, null);

        model.File sharedFile1 = createFile(AppConfig.SERVER_FTP_FILE_PATH + "testuser/aaaa/test.txt", "Hello", user1, null);
        model.File sharedFile2 = createFile(AppConfig.SERVER_FTP_FILE_PATH + "testuser/log-login-successfully.txt", "log 111", user1, null);
        createFile(AppConfig.SERVER_FTP_FILE_PATH + "testuser/log2.txt", "log 112", user1, null);

        shareDirectoriesDao.save(new ShareDirectories(new ShareDirectoriesId(userHomeDirectory.getId(), user2.getId()), false, true, userHomeDirectory, user2));

        shareFilesDao.save(new ShareFiles(new ShareFilesId(sharedFile1.getId(), user2.getId()), true, true, sharedFile1, user2));
        shareFilesDao.save(new ShareFiles(new ShareFilesId(sharedFile2.getId(), user2.getId()), true, false, sharedFile2, user2));

//        directoryDao.save(new Directory(0, AppConfig.SERVER_FTP_FILE_PATH, null, null));
//        Directory userHomeDirectory = new Directory(0, AppConfig.SERVER_FTP_FILE_PATH + "testuser", user1, null);
//        directoryDao.save(userHomeDirectory);
//        File file = new File(AppConfig.SERVER_FTP_FILE_PATH + "testuser");
//        directoryDao.save(new Directory(0, AppConfig.SERVER_FTP_FILE_PATH + "testuser/aaaa", user1, null));
//        directoryDao.save(new Directory(0, AppConfig.SERVER_FTP_FILE_PATH + "testuser/abc", user1, null));
//        directoryDao.save(new Directory(0, AppConfig.SERVER_FTP_FILE_PATH + "testuser/def", user1, null));
//        directoryDao.save(new Directory(0, AppConfig.SERVER_FTP_FILE_PATH + "testuser2", user2, null));
//
//        model.File sharedFile1 = new model.File(0, AppConfig.SERVER_FTP_FILE_PATH + "testuser/aaaa/test.txt", user1, null);
//        model.File sharedFile2 = new model.File(0, AppConfig.SERVER_FTP_FILE_PATH + "testuser/log-login-successfully.txt", user1, null);
//        fileDao.save(sharedFile1);
//        fileDao.save(sharedFile2);
//        fileDao.save(new model.File(0, AppConfig.SERVER_FTP_FILE_PATH + "testuser/log2.txt", user1, null));
//
//        shareDirectoriesDao.save(new ShareDirectories(new ShareDirectoriesId(userHomeDirectory.getId(), user2.getId()), false, true, userHomeDirectory, user2));
//
//        shareFilesDao.save(new ShareFiles(new ShareFilesId(sharedFile1.getId(), user2.getId()), true, true, sharedFile1, user2));
//        shareFilesDao.save(new ShareFiles(new ShareFilesId(sharedFile2.getId(), user2.getId()), true, false, sharedFile2, user2));
    }
}
