/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import bus.FileBus;
import payload.GetAnonymousFilesResult;
import bus.UserBus;
import config.AppConfig;
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

/**
 *
 * @author User
 */
public class LSANCommand implements Command {

    private static final FileBus fileBus = new FileBus();

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
            GetAnonymousFilesResult files = fileBus.getAnonymousFiles(session.getUsername());
            SocketUtils.respondCommandSocket(
                    StatusCode.ABOUT_TO_OPEN_DATA_CONNECTION,
                    "About to open data connection.",
                    commandSocketWriter
            );
            Socket dataSocket = session.getDataSocket().accept();
            BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));

            String result = "";
            for (var dir : files.directories) {
                File file = new File(dir.getPath());
                result += formatSingleFile(file, dir.getPath());
            }

            for (var f : files.files) {
                File file = new File(f.getPath());
                result += formatSingleFile(file, f.getPath());
            }

            SocketUtils.writeLineAndFlush(result, dataWriter);
            dataWriter.close();

        } catch (AnonymousDisabledException ex) {
            try {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Anonymous disabled.",
                        commandSocketWriter
                );
            } catch (IOException ex1) {
                Logger.getLogger(LSANCommand.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (IOException ex) {
            Logger.getLogger(LSANCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
