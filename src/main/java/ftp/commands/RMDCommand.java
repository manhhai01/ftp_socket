package ftp.commands;

import ftp.FilePermission;
import ftp.FilePermissionService;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMDCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        String dirName = arguments[0];
        String filePath = session.getWorkingDirAbsolutePath() + "/" + dirName;
        File file = new File(filePath);
        
        // Check if path is a directory
        if (!file.isDirectory()) {
            try {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Not a directory.",
                        commandSocketWriter
                );
            } catch (IOException ex) {
                Logger.getLogger(RMDCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        // Check if directory is deletable
        FilePermissionService filePermissionService = new FilePermissionService();
        FilePermission filePermission = filePermissionService.getFilePermission(filePath, session.getUsername());
        if (!filePermission.isDeletable()) {
            try {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.",
                        commandSocketWriter
                );
            } catch (IOException ex) {
                Logger.getLogger(RMDCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        // Check if directory is empty
        try {
            if (Files.list(file.toPath()).findFirst().isPresent()) {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Directory must be empty.",
                        commandSocketWriter
                );
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(RMDCommand.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        
        // Proceed to delete the directory
        file.delete();
        try {
            SocketUtils.respondCommandSocket(
                    StatusCode.FILE_ACTION_OK,
                    "Command okay.",
                    commandSocketWriter
            );
        } catch (IOException ex) {
            Logger.getLogger(RMDCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}