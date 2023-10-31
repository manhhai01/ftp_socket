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
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class RETRCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        FtpFileUtils ftpFileUtils = new FtpFileUtils();

        try {
            SocketUtils.respondCommandSocket(
                    StatusCode.FILE_ACTION_OK,
                    "Requested file action okay, completed.",
                    commandSocketWriter
            );
            Socket socket = session.getDataSocket().accept();
            String filePath = ftpFileUtils.joinPath(session.getWorkingDirAbsolutePath(), arguments[0]);
            File file = new File(filePath);
            FilePermissionService filePermissionService = new FilePermissionService();
            FilePermission filePermission = filePermissionService.getFilePermission(file.getPath().replace("\\", "/"), session.getUsername());
            if (filePermission.isReadable()) {
                BufferedWriter dataSocketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (session.getType().equals("A")) {
                    FileReader fileReader = new FileReader(file);
                    fileReader.transferTo(dataSocketWriter);
                    dataSocketWriter.flush();
                    fileReader.close();
                }
                if (session.getType().equals("I")) {
                    byte[] data = FileUtils.readFileToByteArray(file);
                    IOUtils.write(data, socket.getOutputStream());
                }
                dataSocketWriter.close();
                SocketUtils.respondCommandSocket(
                        StatusCode.CLOSING_DATA_CONNECTION,
                        "Closing data connection.",
                        commandSocketWriter
                );
            } else {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.",
                        commandSocketWriter
                );
            }

            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
