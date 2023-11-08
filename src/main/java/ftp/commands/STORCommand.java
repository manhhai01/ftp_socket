/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import bus.FileBus;
import bus.NormalFileBus;
import config.AppConfig;
import dao.UserDao;
import ftp.FtpFileUtils;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

public class STORCommand implements Command {

    private final NormalFileBus normalFileBus = new NormalFileBus();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();
    private final UserDao userDao = new UserDao();

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {

        String inputFilePath = arguments[0];
        String path = ftpFileUtils.convertPublicPathToFtpPath(session.getWorkingDirAbsolutePath(), inputFilePath);
        if (path.startsWith(AppConfig.SERVER_FTP_ANON_PATH)) {
            User user = userDao.getUserByUserName(session.getUsername());
            if (!user.isAnonymous()) {
                try {
                    SocketUtils.respondCommandSocket(
                            StatusCode.FILE_ACTION_NOT_TAKEN,
                            "Anonymous disabled.",
                            commandSocketWriter
                    );
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(STORCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        try {
            // Create file if it doesn't exist
            boolean fileCreationSuccess = normalFileBus.createNormalFile(path, session.getUsername());
            if (!fileCreationSuccess) {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.",
                        commandSocketWriter
                );
                return;
            }
            SocketUtils.respondCommandSocket(
                    StatusCode.FILE_ACTION_OK,
                    "Requested file action okay.",
                    commandSocketWriter
            );

            // Write to file
            Socket socket = session.getDataSocket().accept();
            normalFileBus.writeToNormalFile(
                    path,
                    session.getUsername(),
                    socket.getInputStream(),
                    session.getType()
            );
            SocketUtils.respondCommandSocket(
                    StatusCode.CLOSING_DATA_CONNECTION,
                    "Closing data connection.",
                    commandSocketWriter
            );

        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
