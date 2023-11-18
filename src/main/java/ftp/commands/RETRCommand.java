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
import ftp.SessionSocketUtils;
import ftp.StatusCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class RETRCommand implements Command {

    private FtpFileUtils ftpFileUtils = new FtpFileUtils();
    private UserDao userDao = new UserDao();
    private FileBus fileService = new FileBus();

    private void executeFile(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) throws IOException {
        Socket socket = session.getDataSocket().accept();
        String inputFilePath = arguments[0];
        String filePath = ftpFileUtils.convertPublicPathToFtpPath(
                session.getWorkingDirAbsolutePath(),
                inputFilePath
        );
        File file = new File(filePath);
        User user = userDao.getUserByUserName(session.getUsername());
        if (file.length() > user.getMaxDownloadFileSizeBytes()) {
            session.getSessionSocketUtils().respondCommandSocket(
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
//                FileReader fileReader = new FileReader(file);
//                fileReader.transferTo(dataSocketWriter);
//                dataSocketWriter.flush();
//                fileReader.close();
                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                session.getSessionSocketUtils().writeLineAndFlush(content, dataSocketWriter);

            }
            if (session.getType().equals("I")) {
//                byte[] data = FileUtils.readFileToByteArray(file);
//                IOUtils.write(data, socket.getOutputStream());
                byte[] data = FileUtils.readFileToByteArray(file);
                session.getSessionSocketUtils().writeLineAndFlush(new String(data), dataSocketWriter);
            }
            dataSocketWriter.close();
            session.getSessionSocketUtils().respondCommandSocket(
                    StatusCode.CLOSING_DATA_CONNECTION,
                    "Closing data connection.",
                    commandSocketWriter
            );
        } else {
            session.getSessionSocketUtils().respondCommandSocket(
                    StatusCode.FILE_ACTION_NOT_TAKEN,
                    "Forbidden.",
                    commandSocketWriter
            );
        }
        socket.close();
    }

    private void executeDirectory(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) throws IOException {

    }

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        User user = userDao.getUserByUserName(session.getUsername());
        if (user.isBlockDownload()) {
            try {
                session.getSessionSocketUtils().respondCommandSocket(
                        StatusCode.FILE_ACTION_NOT_TAKEN,
                        "Forbidden.", commandSocketWriter);
            } catch (IOException ex) {
                Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }

        try {
            session.getSessionSocketUtils().respondCommandSocket(
                    StatusCode.FILE_ACTION_OK,
                    "Requested file action okay, completed.",
                    commandSocketWriter
            );
            String inputFilePath = arguments[0];

            String filePath = ftpFileUtils.convertPublicPathToFtpPath(
                    session.getWorkingDirAbsolutePath(),
                    inputFilePath
            );

            // Anonymous check
            if (filePath.startsWith(AppConfig.SERVER_FTP_ANON_PATH)) {
                if (!user.isAnonymous()) {
                    session.getSessionSocketUtils().respondCommandSocket(
                            StatusCode.FILE_ACTION_NOT_TAKEN,
                            "Anonymous disabled.",
                            commandSocketWriter
                    );
                    return;
                }
            }
            File file = new File(filePath);

            if (file.isFile()) {
                executeFile(arguments, session, commandSocketWriter);
            } else {

            }

        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
