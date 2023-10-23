/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CWDCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            String newDir = arguments[0];
            session.changeWorkingDir(newDir);
            SocketUtils.respondCommandSocket(StatusCode.FILE_ACTION_OK,
                    "Okay.",
                    commandSocketWriter
            );
        } catch (IOException ex) {
            Logger.getLogger(CWDCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
