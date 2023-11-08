package ftp.commands;

import bus.DirectoryBus;
import bus.FileBus;
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
public class MKDCommand implements Command {

    private final DirectoryBus directoryBus = new DirectoryBus();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            String dirName = arguments[0];
            String newDirPath = ftpFileUtils.convertPublicPathToFtpPath(session.getWorkingDirAbsolutePath(), dirName);
            boolean success = directoryBus.createDirectory(newDirPath, session.getUsername());
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
