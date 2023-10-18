package ltudm.ftp.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RNTOCommand implements Command {

    @Override
    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
        String newFilename = arguments[0];
        String oldFilename = session.getRNFRFilename();
        if(oldFilename == null) {
            try {
                SocketUtils.writeLineAndFlush("450 RNFR command is required before this command.", commandSocketWriter);
                return;
            } catch (IOException ex) {
                Logger.getLogger(RNTOCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            File fileWithNewName = new File(session.getWorkingDirAbsolutePath() + "/" + newFilename);
            if (fileWithNewName.exists()) {
                SocketUtils.writeLineAndFlush(String.format("450 File with %s name already exists.", newFilename), commandSocketWriter);
                return;
            }
            
            File fileToRename = new File(session.getWorkingDirAbsolutePath() + "/" + oldFilename);
            fileToRename.renameTo(fileWithNewName);
            SocketUtils.writeLineAndFlush("250 Requested file action okay, completed.", commandSocketWriter);
            session.setRNFRFilename(null);

        } catch (IOException ex) {
            Logger.getLogger(RETRCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
}
