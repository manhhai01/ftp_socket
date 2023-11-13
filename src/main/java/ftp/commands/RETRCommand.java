/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import config.AppConfig;
import ftp.FilePermission;
import bus.FileBus;
import dao.UserDao;
import ftp.FtpFileUtils;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class RETRCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        FtpFileUtils ftpFileUtils = new FtpFileUtils();
        UserDao userDao = new UserDao();
        User user = userDao.getUserByUserName(session.getUsername());
        FileBus fileService = new FileBus();

        try {
            SocketUtils.respondCommandSocket(
                    StatusCode.FILE_ACTION_OK,
                    "Requested file action okay, completed.",
                    commandSocketWriter
            );
            String inputFilePath = arguments[0];
            Socket socket = session.getDataSocket().accept();
            String filePath = ftpFileUtils.convertPublicPathToFtpPath(
                    session.getWorkingDirAbsolutePath(),
                    inputFilePath
            );

            if (filePath.startsWith(AppConfig.SERVER_FTP_ANON_PATH)) {
                if (!user.isAnonymous()) {
                    SocketUtils.respondCommandSocket(
                            StatusCode.FILE_ACTION_NOT_TAKEN,
                            "Anonymous disabled.",
                            commandSocketWriter
                    );
                    return;
                }
            }
            File file = new File(filePath);

            if (file.length() > user.getMaxDownloadFileSizeBytes()) {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.", commandSocketWriter);
                return;
            }

            fileService = new FileBus();
            FilePermission filePermission = fileService.getFilePermission(
                    filePath,
                    session.getUsername(),
                    file.isFile() ? FileBus.NORMAL_FILE_TYPE : FileBus.DIRECTORY_TYPE
            );
            if (filePermission.isReadable()) {
                BufferedWriter dataSocketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (session.getType().equals("A")) {
                    FileReader fileReader = new FileReader(file);
                    fileReader.transferTo(dataSocketWriter);
                    dataSocketWriter.flush();
                    fileReader.close();
                }
                if (session.getType().equals("I")) {
                    byte[] data = FileUtils.readFileToByteArray(file);
                    IOUtils.write(data, socket.getOutputStream());
                }
                dataSocketWriter.close();
                SocketUtils.respondCommandSocket(
                        StatusCode.CLOSING_DATA_CONNECTION,
                        "Closing data connection.",
                        commandSocketWriter
                );
            } else {
                SocketUtils.respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.",
                        commandSocketWriter
                );
            }

            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
