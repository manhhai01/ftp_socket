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
public class MKDCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            String newDirPath = session.getWorkingDirAbsolutePath() + "/" + arguments[0];
            File file = new File(newDirPath);
            FilePermissionService filePermissionService = new FilePermissionService();
            FilePermission currentDirPerm = filePermissionService.getFilePermission(session.getWorkingDirAbsolutePath(), session.getUsername());
            if(currentDirPerm.isWritable()) {
                file.mkdir();
                filePermissionService.addFileOrDirectoryOwnerPermission(newDirPath, session.getUsername());
                SocketUtils.writeLineAndFlush("257 \"" + arguments[0] + "\" created.", commandSocketWriter);
            } else {
                SocketUtils.writeLineAndFlush("450 Forbidden.", commandSocketWriter);
            }
        } catch (IOException ex) {
            Logger.getLogger(MKDCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
