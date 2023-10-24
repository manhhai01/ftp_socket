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
public class PassCommand implements Command {

    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        String password = arguments[0];
        String username = session.getUsername();
        System.out.println("Username: " + username);
        if (username.equals("testuser") && password.equals("test")) {
            try {
                session.changeWorkingDir("/" + session.getUsername());
                SocketUtils.respondCommandSocket("230 User logged in, proceed.", commandSocketWriter);
            } catch (IOException ex) {
                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                SocketUtils.respondCommandSocket("530 Authentication failed.", commandSocketWriter);
            } catch (IOException ex) {
                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
