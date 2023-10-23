package ftp;

import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

public class FtpServerSession {

    private String clientId;
    private String username;
    // Working dir starts with "/"
    private String workingDir;
    private ServerSocket dataSocket;
    private String RNFRFilename;

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
        // Go up 1 level
        if (workingDir.equals("..")) {
            System.out.println("Change working dir: " + workingDir);
            List<String> pathTokens = Arrays.asList(this.workingDir.replaceFirst("/", "").split("/"));
            pathTokens = pathTokens.subList(0, pathTokens.size() - 1);
            if (pathTokens.isEmpty()) {
                this.workingDir = "/";
                return true;
            }
            this.workingDir = "/" + String.join("/", pathTokens);
            return true;
        }
        // Change working dir relative to root
        if (workingDir.startsWith("/")) {
            this.workingDir = workingDir;
            return true;
        }
        // Change working dir relative to current working dir
        this.workingDir = this.workingDir + "/" + workingDir;
        return true;
    }

    public ServerSocket getDataSocket() {
        return dataSocket;
    }

    public void setDataSocket(ServerSocket dataSocket) {
        this.dataSocket = dataSocket;
    }

    public String getRNFRFilename() {
        return RNFRFilename;
    }

    public void setRNFRFilename(String RNFRFilename) {
        this.RNFRFilename = RNFRFilename;
    }
}