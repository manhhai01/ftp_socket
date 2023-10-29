package ftp.commands;

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

public class RNTOCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        FtpFileUtils ftpFileUtils = new FtpFileUtils();

        String newFilename = arguments[0];
        String oldFilename = session.getRNFRFilename();
        if (oldFilename == null) {
            try {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "RNFR command is required before this command.",
                        commandSocketWriter
                );
                return;
            } catch (IOException ex) {
                Logger.getLogger(RNTOCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            String oldFilePath = ftpFileUtils.joinPath(session.getWorkingDirAbsolutePath(), oldFilename);
            String newFilePath = ftpFileUtils.joinPath(session.getWorkingDirAbsolutePath(), newFilename);
            File fileWithNewName = new File(newFilePath);
            if (fileWithNewName.exists()) {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "File with %s name already exists.",
                        commandSocketWriter
                );
                return;
            }

            FilePermissionService filePermissionService = new FilePermissionService();
            filePermissionService.changeFilePath(oldFilePath, newFilePath, session.getUsername());
            File fileToRename = new File(oldFilePath);
            fileToRename.renameTo(fileWithNewName);
            SocketUtils.respondCommandSocket(
                    StatusCode.FILE_ACTION_OK,
                    "Requested file action okay, completed.",
                    commandSocketWriter
            );
            session.setRNFRFilename(null);

        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
