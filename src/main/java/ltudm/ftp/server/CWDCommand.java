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
public class CWDCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            String newDir = arguments[0];
            session.changeWorkingDir(newDir);
            SocketUtils.writeLineAndFlush("250 Okay.", commandSocketWriter);
        } catch (IOException ex) {
            Logger.getLogger(CWDCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
