/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ltudm.ftp.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class DELECommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            String filename = arguments[0];
            File file = new File(session.getWorkingDirAbsolutePath() + "/" + filename);
            file.delete();
            SocketUtils.writeLineAndFlush("250 Command okay.", commandSocketWriter);
        } catch (IOException ex) {
            Logger.getLogger(DELECommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
