/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ltudm.ftp.server;

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
public class RNFRCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            File file = new File(session.getWorkingDirAbsolutePath() + "/" + arguments[0]);
            if (file.exists()) {
                SocketUtils.writeLineAndFlush("350: RNFR accepted. Please supply new name for RNTO.", commandSocketWriter);
                session.setRNFRFilename(arguments[0]);
            } else {
                SocketUtils.writeLineAndFlush("450 Requested file action not taken.", commandSocketWriter);
            }

        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
