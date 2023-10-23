/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermission;
import ftp.FilePermissionService;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DELECommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            String filename = arguments[0];
            File file = new File(session.getWorkingDirAbsolutePath() + "/" + filename);
            FilePermissionService filePermissionService = new FilePermissionService();
            FilePermission filePermission = filePermissionService.getFilePermission(session.getWorkingDirAbsolutePath() + "/" + filename, session.getUsername());
            if (filePermission.isDeletable()) {
                file.delete();
                // Todo: delete in db
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
