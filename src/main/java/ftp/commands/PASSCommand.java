/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import bus.UserBus;
import config.AppConfig;
import bus.FileBus;
import ftp.FtpFileUtils;
import ftp.FtpServer;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class PASSCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        String password = arguments[0];
        String username = session.getUsername();
        System.out.println("Username: " + username);
        UserBus userBus = new UserBus();
        boolean loggedIn = userBus.checkLogin(username, password);
        if (loggedIn) {
            try {
                FileBus fileService = new FileBus();
                fileService.createHomeDirectoryIfNotExist(username);
                session.changeWorkingDir("/" + session.getUsername());
                SocketUtils.respondCommandSocket(StatusCode.LOGGED_IN, "User logged in, proceed.", commandSocketWriter);
            } catch (IOException ex) {
                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                SocketUtils.respondCommandSocket(StatusCode.NOT_LOGGED_IN, "Authentication failed.", commandSocketWriter);
            } catch (IOException ex) {
                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
