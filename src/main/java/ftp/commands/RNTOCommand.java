package ftp.commands;

import config.AppConfig;
import bus.FileBus;
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

    private final FileBus fileService = new FileBus();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {

        String inputNewFilePath = arguments[0];
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
            String oldFilePath = ftpFileUtils.convertPublicPathToFtpPath(
                    session.getWorkingDirAbsolutePath(),
                    oldFilename
            );

            String newFilePath = ftpFileUtils.convertPublicPathToFtpPath(
                    session.getWorkingDirAbsolutePath(),
                    inputNewFilePath
            );
//            // Absolute path
//            if (inputNewFilePath.startsWith("/")) {
//                newFilePath = inputNewFilePath.replaceFirst("/", AppConfig.SERVER_FTP_FILE_PATH);
//            }// File name only
//            else {
//                newFilePath = ftpFileUtils.joinPath(session.getWorkingDirAbsolutePath(), inputNewFilePath);
//            }
            File fileWithNewName = new File(newFilePath);
            if (fileWithNewName.exists()) {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "File with %s name already exists.",
                        commandSocketWriter
                );
                return;
            }

            fileService.changeFilePath(oldFilePath, newFilePath, session.getUsername());
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
