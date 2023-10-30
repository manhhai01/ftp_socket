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

/**
 *
 * @author User
 */
public class MKDCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        FtpFileUtils ftpFileUtils = new FtpFileUtils();
        try {
            String dirName = arguments[0];
            String newDirPath = ftpFileUtils.joinPath(session.getWorkingDirAbsolutePath(), dirName);
            FilePermissionService filePermissionService = new FilePermissionService();
            boolean success = filePermissionService.createDirectory(newDirPath, session.getUsername());
            if (success) {
                SocketUtils.respondCommandSocket(
                        StatusCode.DIRECTORY_CREATED,
                        String.format("\"%s\" Created.", dirName),
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
            Logger.getLogger(MKDCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
