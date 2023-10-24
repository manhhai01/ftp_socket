/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ltudm.ftp.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class UserCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            String username = arguments[0];
            session.setUsername(username);
            SocketUtils.writeLineAndFlush("331 User name okay, need password for " + username + ".", commandSocketWriter);
        } catch (IOException ex) {
            Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
