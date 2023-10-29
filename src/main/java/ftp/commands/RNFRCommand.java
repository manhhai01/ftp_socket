/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermission;
import ftp.FilePermissionService;
import ftp.FtpFileUtils;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RNFRCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        FtpFileUtils ftpFileUtils = new FtpFileUtils();
        try {
            String fileName = arguments[0];
            String filePath = ftpFileUtils.joinPath(session.getWorkingDirAbsolutePath(), fileName);
            File file = new File(filePath);
            if (!file.exists()) {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "File doesn't exist.",
                        commandSocketWriter
                );
                return;
            }
            FilePermissionService filePermissionService = new FilePermissionService();
            FilePermission filePermission = filePermissionService.getFilePermission(filePath, session.getUsername());
            if (!filePermission.isRenamable()) {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.",
                        commandSocketWriter
                );
                return;
            }

            SocketUtils.respondCommandSocket(
                    StatusCode.FILE_ACTION_REQUIRES_INFO,
                    "RNFR accepted. Please supply new name for RNTO.",
                    commandSocketWriter
            );
//            SocketUtils.writeLineAndFlush("350: RNFR accepted. Please supply new name for RNTO.", commandSocketWriter);
            session.setRNFRFilename(fileName);
        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
