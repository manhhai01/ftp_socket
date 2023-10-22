/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermission;
import ftp.FilePermissionService;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
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
                SocketUtils.writeLineAndFlush("250 Command okay.", commandSocketWriter);
            } else {
                SocketUtils.writeLineAndFlush("450 Forbidden.", commandSocketWriter);
            }

        } catch (IOException ex) {
            Logger.getLogger(DELECommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
