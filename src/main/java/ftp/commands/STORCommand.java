/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermissionService;
import ftp.FileService;
import ftp.FtpFileUtils;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class STORCommand implements Command {

    private final FilePermissionService filePermissionService = new FilePermissionService();
    private final FileService fileService = new FileService();
    private final FtpFileUtils ftpFileUtils = new FtpFileUtils();

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {

        String path = ftpFileUtils.joinPath(session.getWorkingDirAbsolutePath(), arguments[0]);
        try {

            // Create file if it doesn't exist
            boolean fileCreationSuccess = fileService.createNormalFile(path, session.getUsername());
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
            fileService.writeToNormalFile(
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
