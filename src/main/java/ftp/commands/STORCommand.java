/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

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
            SocketUtils.writeLineAndFlush("250 Requested file action okay, completed.", commandSocketWriter);
            
            Socket socket = session.getDataSocket().accept();
            
            File file = new File(session.getWorkingDirAbsolutePath() + "/" + arguments[0]);
            file.createNewFile();
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
