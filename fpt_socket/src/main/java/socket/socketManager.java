/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import cipher.AESCipher;
import cipher.Encrypt;
import cipher.KeyAES;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.IPConfig;
import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import payloads.DataResponse;
import payloads.UserData;
import payloads.UserPermission;
import static thread.Client.executor;
import thread.ReceiveMessage;
import thread.SendMessage;

/**
 *
 * @author Son
 */
public class socketManager {
    private static socketManager instance = null;
    private Socket commandSocket,dataSocket;
    private BufferedWriter commandWriter,dataWriter;
    private BufferedReader commandReader,dataReader;
    
    private socketManager() {
        try {
//            IPConfig ipConfig = new IPConfig();
//            String ipServer = ipConfig.getIPServer();
            // Khởi tạo kết nối TCP socket
            commandSocket = new Socket("localhost", 21);
            // Khởi tạo BufferedReader và BufferedWriter để gửi và nhận dữ liệu
            commandReader = new CustomBufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            commandWriter = new BufferedWriter(new OutputStreamWriter(commandSocket.getOutputStream()));    
            commandReader.readLine();

            // send key aes to server
            String keyAES = Encrypt.encriptKey();
            commandWriter.append("KEY " + keyAES);
            commandWriter.newLine();
            commandWriter.flush();
            commandReader.readLine();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
    public static socketManager getInstance() {
        if (instance == null) {
            instance = new socketManager();
        }
        return instance;
    }
/*--------------------------------Account command-----------------------------------------*/    
    public DataResponse login(String user,String password) throws Exception{
        writeLineAndFlush("USER "+user, commandWriter);
        commandReader.readLine();
        writeLineAndFlush("PASS "+password, commandWriter);
        String message = commandReader.readLine();
        
        return new DataResponse(message);
    }
    
    public DataResponse register(String data) throws Exception{
        openNewDataPort();
        writeLineAndFlush("REG", commandWriter);
        commandReader.readLine();
        writeLineAndFlush(data, dataWriter);
        closeDataPort();
        return new DataResponse(commandReader.readLine());
    }
    
    public DataResponse verifyOTP(String username,String password, String otp) throws Exception{
        writeLineAndFlush("SOTP "+username +" "+password+ " "+otp, commandWriter);
        return new DataResponse(commandReader.readLine());
    }
    public DataResponse regenerateOTP(String username,String password) throws Exception{
        writeLineAndFlush("GOTP "+username +" "+password+ " ", commandWriter);
        return new DataResponse(commandReader.readLine());
    }
    
    public UserData getUserInfo() throws Exception{
        openNewDataPort();
        writeLineAndFlush("PROF", commandWriter);
        commandReader.readLine();
        String response=dataReader.readLine();
        closeDataPort();
        commandReader.readLine();// read close connection message
        Gson gson = new Gson();
        return gson.fromJson(response, UserData.class);
    }
/*------------------------------------------------------------------------------------------*/  
    
    
/*--------------------------------file manager command--------------------------------------*/         
    public String getSharedFiles() throws Exception{
        openNewDataPort();
        writeLineAndFlush("LSHR", commandWriter);
        commandReader.readLine();
        String response=dataReader.readLine();
        closeDataPort();
        commandReader.readLine();// read close connection message
        return response;
    }
    public DataResponse getCurrentWorkingDirectory() throws Exception{
        writeLineAndFlush("PWD", commandWriter);
        return new DataResponse(commandReader.readLine());
    }
    public String getFileList(String path) throws Exception{
        String pathURLEncode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        openNewDataPort();
        writeLineAndFlush("MLSD "+pathURLEncode, commandWriter);
        commandReader.readLine();
        String response=dataReader.readLine();
        closeDataPort();
        commandReader.readLine();// read close connection message
        return response;
    }
    public DataResponse changeDirectory(String path) throws Exception{
        String pathURLEncode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        writeLineAndFlush("CWD "+pathURLEncode, commandWriter);
        String response=commandReader.readLine();
        return new DataResponse(response);
    }
    public DataResponse createNewFolder(String path) throws Exception{
        String pathURLEncode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        writeLineAndFlush("MKD "+pathURLEncode, commandWriter);
        String response=commandReader.readLine();
        return new DataResponse(response);
    }
    public DataResponse rename(String oldName,String newName) throws Exception{
        String oldNameURLEncode = URLEncoder.encode(oldName, StandardCharsets.UTF_8);
        String newNameURLEncode = URLEncoder.encode(newName, StandardCharsets.UTF_8);
        writeLineAndFlush("RNFR "+oldNameURLEncode, commandWriter);
        DataResponse res = new DataResponse(commandReader.readLine());
        if(res.getStatus()==StatusCode.FILE_ACTION_REQUIRES_INFO){
            writeLineAndFlush("RNTO "+newNameURLEncode, commandWriter);
            res = new DataResponse(commandReader.readLine());
        }
        return res;
    }
    public DataResponse delete(String path) throws Exception{
        String pathURLEncode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        writeLineAndFlush("DELE "+pathURLEncode, commandWriter);
        String response=commandReader.readLine();
        return new DataResponse(response);
        
    }
    public DataResponse checkPermissionForMoveCommand(String path) throws Exception{
        String pathURLEncode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        writeLineAndFlush("RNFR "+pathURLEncode, commandWriter);
        return new DataResponse(commandReader.readLine());
    }
    public DataResponse move(String path) throws Exception{
        String pathURLEncode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        writeLineAndFlush("RNTO "+pathURLEncode, commandWriter);
        return new DataResponse(commandReader.readLine());
    }
    public UserPermission getShareUserList(String path) throws Exception{
        String pathURLEncode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        openNewDataPort();
        writeLineAndFlush("LSUR" + pathURLEncode,commandWriter);
        commandReader.readLine();
        String res = dataReader.readLine();
        Gson gson = new Gson();
        return gson.fromJson(res, UserPermission.class);
        
    }
    
/*------------------------------------------------------------------------------------------*/     
    
/*---------------------------------EPSV command---------------------------------------------*/ 
    public void openNewDataPort() throws Exception{
        writeLineAndFlush("EPSV", commandWriter);
        String epsvResponse = commandReader.readLine();
        int dataPort = Integer.parseInt(epsvResponse
                .replace("229 Entering Extended Passive Mode (|||", "")
                .replace("|)", ""));
        dataSocket = new Socket("localhost", dataPort);
        dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
        dataReader = new CustomBufferedReader(new InputStreamReader(dataSocket.getInputStream()));
    }    
    
    public void closeDataPort() throws IOException{
        dataWriter.close();
        dataReader.close();
        dataSocket.close();
    }
/*------------------------------------------------------------------------------------------*/     
    public void disconnect() throws IOException{
        commandReader.close();
        commandWriter.close();
        commandSocket.close();
        instance=null;
    }
    public void writeLineAndFlush(String content, BufferedWriter writer) throws Exception {
        
        byte[] ketAES = KeyAES.getInstance().getKey();
        String contextAES = AESCipher.encrypt(ketAES, content);
        
        writer.append(contextAES);
        writer.newLine();
        writer.flush();
    }
    
    

    
}
