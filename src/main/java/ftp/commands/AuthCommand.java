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
public class AuthCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        if (arguments[0].equals("TLS")) {
            try {
                System.out.println("431 Service is unavailable.");
                SocketUtils.writeLineAndFlush("431 Service is unavailable.", commandSocketWriter);
                return;
            } catch (IOException ex) {
                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (arguments[0].equals("SSL")) {
            try {
                System.out.println("431 Service is unavailable.");
                SocketUtils.writeLineAndFlush("431 Service is unavailable.", commandSocketWriter);
                return;
            } catch (IOException ex) {
                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            SocketUtils.writeLineAndFlush("500 Command is not recognised.", commandSocketWriter);
        } catch (IOException ex) {
            Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
