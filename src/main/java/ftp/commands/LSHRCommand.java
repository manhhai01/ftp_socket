/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import config.AppConfig;
import dao.UserDao;
import ftp.FtpServerSession;
import ftp.SessionSocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import payload.GetSharedFilesResultDto;

/**
 *
 * @author User
 */
public class LSHRCommand implements Command {

    private String formatSingleFile(File file, String pathStartWithFtpRoot, String username) {
        MLSDFormatter formatter = new MLSDFormatter();
        FilePermissionGetter filePermissionGetter = new DefaultFilePermissionGetter(username);
        // Remove new line
        return formatter.formatSingleFile(file, filePermissionGetter.getFilePermission(file)).replace("\n", "")
                // Add file path and remove ftp root path so string will be in the form "/path/to/file.txt"
                // instead of "path/to-ftp-root/path/to/file.txt"
                + " " + URLEncoder.encode(pathStartWithFtpRoot.replaceFirst(AppConfig.SERVER_FTP_FILE_PATH, ""), StandardCharsets.UTF_8)
                // Add new line back
                + "\n";
    }

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            session.getSessionSocketUtils().respondCommandSocket(
                    StatusCode.ABOUT_TO_OPEN_DATA_CONNECTION,
                    "About to open data connection.",
                    commandSocketWriter
            );

            Socket dataSocket = session.getDataSocket().accept();
            BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));

            UserDao userDao = new UserDao();
            GetSharedFilesResultDto sharedFiles = userDao.getSharedFiles(session.getUsername());
            String result = "";

            for (var dir : sharedFiles.directories) {
                File file = new File(dir.getPath());
                result += formatSingleFile(file, dir.getPath(), session.getUsername());
            }

            for (var f : sharedFiles.files) {
                File file = new File(f.getPath());
                result += formatSingleFile(file, f.getPath(), session.getUsername());
            }

            session.getSessionSocketUtils().writeLineAndFlush(result, dataWriter);
            dataWriter.close();

            session.getSessionSocketUtils().respondCommandSocket(
                    StatusCode.CLOSING_DATA_CONNECTION,
                    "Data connection closed.",
                    commandSocketWriter
            );

        } catch (IOException ex) {
            Logger.getLogger(LSHRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}