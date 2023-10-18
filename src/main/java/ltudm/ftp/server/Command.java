package ltudm.ftp.server;

import java.io.BufferedWriter;

public interface Command {

    public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter);
}
