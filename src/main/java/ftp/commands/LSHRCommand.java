/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermission;
import ftp.FilePermissionService;
import ftp.FileService;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class LSHRCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            SocketUtils.respondCommandSocket(
                    StatusCode.ABOUT_TO_OPEN_DATA_CONNECTION,
                    "About to open data connection.",
                    commandSocketWriter
            );
            
            Socket dataSocket = session.getDataSocket().accept();
            BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
            
            
        } catch (IOException ex) {
            Logger.getLogger(LSHRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
