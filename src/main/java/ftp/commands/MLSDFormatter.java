/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import dao.DirectoryDao;
import dao.FileDao;
import ftp.DirectoryPermission;
import ftp.FilePermission;
import ftp.FtpFileUtils;
import ftp.NormalFilePermission;
import java.io.File;
import java.io.FileFilter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author User
 */
public class MLSDFormatter {

    private static FileDao fileDao = new FileDao();
    private static DirectoryDao directoryDao = new DirectoryDao();
    private static FtpFileUtils ftpFileUtils = new FtpFileUtils();

    private String getNormalFilePermissionString(NormalFilePermission filePermission) {
        if (filePermission.getPermission().equals(NormalFilePermission.FULL_PERMISSION)) {
            return "rwdf";
        } else if (filePermission.getPermission().equals(NormalFilePermission.READABLE_PERMISSION)) {
            return "r";
        }
        return "";
    }

    private String getDirPermissionString(DirectoryPermission dirPermission) {
        String perm = "el";
        if (dirPermission.isDownloadable()) {
            perm += "r";
        }
        if (dirPermission.isUploadable()) {
            perm += "w";
        }
        if (dirPermission.isDeletable()) {
            perm += "d";
        }
        if (dirPermission.isRenamable()) {
            perm += "f";
        }

        return perm;
    }

    private String format(File file, FilePermission filePermission) {
        String result;
        String filePath = ftpFileUtils.convertJavaPathToFtpPath(file.getPath());
        if (file.isDirectory()) {
            result = String.format("Type=%s;Owner=%s;Modify=%s;Size=%s;Perm=%s; %s\n",
                    "dir",
                    directoryDao.getDirectoryByPath(filePath).getUser().getFirstName(),
                    file.lastModified(),//
                    file.length(),
                    getDirPermissionString((DirectoryPermission) filePermission),
                    URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
        } else {
            result = String.format("Type=%s;Owner=%s;Modify=%s;Size=%s;Perm=%s; %s\n",
                    "file",
                    fileDao.getFileByPath(filePath).getUser().getFirstName(),
                    file.lastModified(),//
                    file.length(),
                    getNormalFilePermissionString((NormalFilePermission) filePermission),
                    URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
        }
        return result;
    }

    private String listFormatImplementation(File file, FileFilter fileFilter, FilePermissionGetter filePermissionGetter) {
        String result = "";
        System.out.println(file.getName());
        File[] files = fileFilter == null ? file.listFiles() : file.listFiles(fileFilter);
        if (files == null) {
            return "";
        }
        for (File f : files) {
            result += format(f, filePermissionGetter.getFilePermission(f));

        }
        return result;
    }

    public String listFormat(File file, FilePermissionGetter filePermissionGetter) {
        return listFormatImplementation(file, null, filePermissionGetter);
    }

    public String listFormat(File file, FileFilter fileFilter, FilePermissionGetter filePermissionGetter) {
        return listFormatImplementation(file, fileFilter, filePermissionGetter);
    }

    public String formatSingleFile(File file, FilePermission filePermission) {
        return format(file, filePermission);
    }
}