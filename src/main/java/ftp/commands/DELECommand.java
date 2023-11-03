/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import bus.FileBus;
import ftp.FtpFileUtils;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DELECommand implements Command {

    private final FileBus fileService = new FileBus();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            String filename = arguments[0];
            String filePath = ftpFileUtils.joinPath(session.getWorkingDirAbsolutePath(), filename);
            if (fileService.removeFile(filePath, session.getUsername())) {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_OK,
                        "Command okay.",
                        commandSocketWriter
                );
            } else {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.",
                        commandSocketWriter
                );
            }

        } catch (IOException ex) {
            Logger.getLogger(DELECommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
