/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import config.AppConfig;
import dao.UserDao;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import payload.GetSharedFilesResultDto;

/**
 *
 * @author User
 */
public class LSHRCommand implements Command {

    private String formatSingleFile(File file, String pathStartWithFtpRoot) {
        MLSDFormatter formatter = new MLSDFormatter();

        // Remove new line
        return formatter.formatSingleFile(file).replace("\n", "")
                // Add file path and remove ftp root path so string will be in the form "/path/to/file.txt"
                // instead of "path/to-ftp-root/path/to/file.txt"
                + " " + pathStartWithFtpRoot.replaceFirst(AppConfig.SERVER_FTP_FILE_PATH, "") 
                // Add new line back
                + "\n";
    }

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            SocketUtils.respondCommandSocket(
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
                result += formatSingleFile(file, dir.getPath());
            }

            for (var f : sharedFiles.files) {
                File file = new File(f.getPath());
                result += formatSingleFile(file, f.getPath());
            }

            SocketUtils.writeLineAndFlush(result, dataWriter);
            dataWriter.close();

            SocketUtils.respondCommandSocket(
                    StatusCode.CLOSING_DATA_CONNECTION,
                    "Data connection closed.",
                    commandSocketWriter
            );

        } catch (IOException ex) {
            Logger.getLogger(LSHRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
