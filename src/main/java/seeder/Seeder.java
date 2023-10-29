package seeder;

import dao.DirectoryDao;
import dao.FileDao;
import dao.ShareDirectoriesDao;
import dao.ShareFilesDao;
import dao.UserDao;
import model.Directory;
import model.File;
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
public class Seeder {

    public static void main(String[] args) {
        FileDao fileDao = new FileDao();
        DirectoryDao directoryDao = new DirectoryDao();
        UserDao userDao = new UserDao();
        ShareDirectoriesDao shareDirectoriesDao = new ShareDirectoriesDao();
        ShareFilesDao shareFilesDao = new ShareFilesDao();
        
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setPassword("test");
        userDao.save(user1);
        
        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setPassword("test2");
        userDao.save(user2);
        
        directoryDao.save(new Directory(0, "ftp", null, null));
        Directory userHomeDirectory = new Directory(0, "ftp/testuser", user1, null);
        directoryDao.save(userHomeDirectory);
        directoryDao.save(new Directory(0, "ftp/testuser/aaaa", user1, null));
        directoryDao.save(new Directory(0, "ftp/testuser/abc", user1, null));
        directoryDao.save(new Directory(0, "ftp/testuser/def", user1, null));
        directoryDao.save(new Directory(0, "ftp/testuser2", user2, null));
        
        
        File sharedFile1 = new File(0, "ftp/testuser/aaaa/test.txt", user1, null);
        File sharedFile2 = new File(0, "ftp/testuser/log-login-successfully.txt", user1, null);
        fileDao.save(sharedFile1);
        fileDao.save(sharedFile2);
        fileDao.save(new File(0, "ftp/testuser/log2.txt", user1, null));
        
        shareDirectoriesDao.save(new ShareDirectories(new ShareDirectoriesId(userHomeDirectory.getId(), user2.getId()), false, true, userHomeDirectory, user2));
        
        shareFilesDao.save(new ShareFiles(new ShareFilesId(sharedFile1.getId(), user2.getId()), true, true, sharedFile1, user2));
        shareFilesDao.save(new ShareFiles(new ShareFilesId(sharedFile2.getId(), user2.getId()), true, false, sharedFile2, user2));
        
    }
}
