package ltudm.ftp.server;

import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

public class FtpServerSession {

    private String clientId;
    private String username;
    // Working dir starts with "/"
    private String workingDir;
    private ServerSocket dataSocket;

    public FtpServerSession() {

    }

    public FtpServerSession(String clientId) {

    }

    public String getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public String getWorkingDirAbsolutePath() {
        // Working dir starts with "/"
        String pathRelativeToRoot = workingDir.replaceFirst("/", "");
        return "ftp/" + pathRelativeToRoot;
    }

    public boolean changeWorkingDir(String workingDir) {
        if (workingDir.equals("..")) {
            System.out.println("Change working dir: " + workingDir);
            List<String> pathTokens = Arrays.asList(this.workingDir.replaceFirst("/", "").split("/"));
            pathTokens.subList(0, pathTokens.size() - 1);
            if(pathTokens.isEmpty()) {
                this.workingDir = "/";
            } else {
                this.workingDir = "/" + String.join("/", pathTokens);
            }            
        } else {
            this.workingDir = workingDir;
        }
        if(workingDir.startsWith("/")) {
            this.workingDir = workingDir;   
        } else {
            this.workingDir = this.workingDir + "/" + workingDir;
        }
        return true;
    }

    public ServerSocket getDataSocket() {
        return dataSocket;
    }

    public void setDataSocket(ServerSocket dataSocket) {
        this.dataSocket = dataSocket;
    }

}
