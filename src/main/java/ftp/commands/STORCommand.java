/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermission;
import ftp.FilePermissionService;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.commands.Command;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class STORCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            File file = new File(session.getWorkingDirAbsolutePath() + "/" + arguments[0]);

            // If user want to write to existed file
            // Then check writable permission
            if (file.exists()) {
                FilePermissionService filePermissionService = new FilePermissionService();
                FilePermission filePermission = filePermissionService.getFilePermission(file.getPath().replace("\\", "/"), session.getUsername());
                if (!filePermission.isWritable()) {
                    SocketUtils.writeLineAndFlush("450 Forbidden.", commandSocketWriter);
                    return;
                }
            } 
            // File is new = uploading
            // Todo: Check uploadable permission
            else {
                file.createNewFile();
            }
            SocketUtils.writeLineAndFlush("250 Requested file action okay, completed.", commandSocketWriter);
            Socket socket = session.getDataSocket().accept();
            BufferedReader dataSocketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            FileWriter fileWriter = new FileWriter(file);
            dataSocketReader.transferTo(fileWriter);
            fileWriter.close();
            dataSocketReader.close();
            socket.close();

            SocketUtils.writeLineAndFlush("226 Closing data connection.", commandSocketWriter);
        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
