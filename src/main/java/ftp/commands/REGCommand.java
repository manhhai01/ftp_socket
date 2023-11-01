/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import bus.UserBus;
import config.AppConfig;
import dao.UserDao;
import ftp.FileService;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author User
 */
public class REGCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            SocketUtils.respondCommandSocket(
                    StatusCode.ABOUT_TO_OPEN_DATA_CONNECTION,
                    "Waiting for register info",
                    commandSocketWriter
            );

            Socket dataSocket = session.getDataSocket().accept();
            UserBus userBus = new UserBus();
            BufferedReader reader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

            String jsonData = IOUtils.toString(dataSocket.getInputStream(), StandardCharsets.UTF_8);
            boolean success = userBus.registerUser(jsonData);
            if (success) {
                FileService fileService = new FileService();
                SocketUtils.respondCommandSocket(
                        StatusCode.CLOSING_DATA_CONNECTION,
                        "Account created successfully.",
                        commandSocketWriter
                );
            } else {
                SocketUtils.respondCommandSocket(
                        StatusCode.ACTION_FAILED,
                        "Registration failed.",
                        commandSocketWriter
                );
            }
        } catch (IOException ex) {
            Logger.getLogger(REGCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
