/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermission;
import ftp.FilePermissionService;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class STORCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        String path = session.getWorkingDirAbsolutePath() + "/" + arguments[0];
        try {
            File file = new File(path);
            FilePermissionService filePermissionService = new FilePermissionService();
            // File update case
            if (file.exists()) {
                FilePermission filePermission = filePermissionService.getFilePermission(file.getPath().replace("\\", "/"), session.getUsername());
                if (!filePermission.isWritable()) {
                    SocketUtils.respondCommandSocket(
                            StatusCode.FILE_ACTION_NOT_TAKEN,
                            "Forbidden.",
                            commandSocketWriter
                    );
                    return;
                }
            } // Uploading case
            else {
                FilePermission currentDirPerm = filePermissionService.getFilePermission(session.getWorkingDirAbsolutePath(), session.getUsername());
                // Reject if uploading to current directory is not allowed
                if (!currentDirPerm.isWritable()) {
                    SocketUtils.respondCommandSocket(
                            StatusCode.FILE_ACTION_NOT_TAKEN,
                            "Forbidden.",
                            commandSocketWriter
                    );
                    return;
                }
                file.createNewFile();
                filePermissionService.addFileOrDirectoryOwnerPermission(path, session.getUsername());
            }

            SocketUtils.respondCommandSocket(
                    StatusCode.FILE_ACTION_OK,
                    "Requested file action okay.",
                    commandSocketWriter
            );
            Socket socket = session.getDataSocket().accept();
            BufferedReader dataSocketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            FileWriter fileWriter = new FileWriter(file);
            dataSocketReader.transferTo(fileWriter);
            fileWriter.close();
            dataSocketReader.close();
            socket.close();
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