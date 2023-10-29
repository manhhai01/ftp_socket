/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermissionService;
import ftp.FtpFileUtils;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class SHRECommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        if (arguments.length != 3) {
            try {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Syntax error.",
                        commandSocketWriter
                );
                return;
            } catch (IOException ex) {
                Logger.getLogger(SHRECommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        FilePermissionService filePermissionService = new FilePermissionService();
        FtpFileUtils fileUtils = new FtpFileUtils();
        String fileName = arguments[0];
        boolean isReadable = Boolean.parseBoolean(arguments[1]);
        boolean isWritable = Boolean.parseBoolean(arguments[2]);
        String filePath = fileUtils.joinPath(session.getWorkingDirAbsolutePath(), fileName);
        boolean isSuccess = filePermissionService.setShareFilePermission(filePath, session.getUsername(), isReadable, isWritable);
        if (isSuccess) {
            try {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_OK,
                        String.format("Shared file %s successfully.", fileName),
                        commandSocketWriter
                );
            } catch (IOException ex) {
                Logger.getLogger(SHRECommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.",
                        commandSocketWriter
                );
            } catch (IOException ex) {
                Logger.getLogger(SHRECommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
