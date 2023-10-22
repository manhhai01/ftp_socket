/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp.commands;

import ftp.FilePermission;
import ftp.FilePermissionService;
import ftp.FtpServer;
import ftp.FtpServerSession;
import ftp.SocketUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class MLSDCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        try {
            System.out.println("User working directory: " + session.getWorkingDirAbsolutePath());
            SocketUtils.writeLineAndFlush("150 File status okay; about to open data connection.", commandSocketWriter);
            
            
            Socket socket = session.getDataSocket().accept();
            System.out.println("Client connected to data socket: " + socket.getRemoteSocketAddress());
            BufferedWriter dataSocketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            File file = new File(session.getWorkingDirAbsolutePath());
            MLSDFormatter formatter = new MLSDFormatter();
            String fileData = formatter.format(file, (File pathname) -> {
                FilePermissionService filePermissionService = new FilePermissionService();
                FilePermission filePermission = filePermissionService.getFilePermission(pathname.getPath().replace("\\", "/"), session.getUsername());
                return filePermission.isReadable();
            });
            System.out.println("FileData: " + fileData);
//                    dataSocketWriter.write("Type=cdir;Modify=19981107085215;Perm=el; tmp\n" +
//"Type=cdir;Modify=19981107085215;Perm=el; /tmp\n" +
//"Type=pdir;Modify=19990112030508;Perm=el; ..\n" +
//"Type=file;Size=25730;Modify=19940728095854;Perm=; capmux.tar.z\n" +
//"Type=file;Size=1830;Modify=19940916055648;Perm=r; hatch.c\n" +
//"Type=file;Size=25624;Modify=19951003165342;Perm=r; MacIP-02.txt\n" +
//"Type=file;Size=2154;Modify=19950501105033;Perm=r; uar.netbsd.patch\n" +
//"Type=file;Size=54757;Modify=19951105101754;Perm=r; iptnnladev.1.0.sit.hqx\n" +
//"Type=file;Size=226546;Modify=19970515023901;Perm=r; melbcs.tif\n" +
//"Type=file;Size=12927;Modify=19961025135602;Perm=r; tardis.1.6.sit.hqx\n" +
//"Type=file;Size=17867;Modify=19961025135602;Perm=r; timelord.1.4.sit.hqx\n" +
//"Type=file;Size=224907;Modify=19980615100045;Perm=r; uar.1.2.3.sit.hqx\n" +
//"Type=file;Size=1024990;Modify=19980130010322;Perm=r; cap60.pl198.tar.gz\n");
            dataSocketWriter.write(fileData);
            dataSocketWriter.flush();
            dataSocketWriter.close();
            socket.close();
            session.getDataSocket().close();

            SocketUtils.writeLineAndFlush("226 Closing data connection.", commandSocketWriter);
        } catch (IOException ex) {
            Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
