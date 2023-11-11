/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.commons.io.IOUtils;
import payloads.DataResponse;

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
            // Khởi tạo kết nối TCP socket
            commandSocket = new Socket("localhost", 21);
            // Khởi tạo BufferedReader và BufferedWriter để gửi và nhận dữ liệu
            commandReader = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            commandWriter = new BufferedWriter(new OutputStreamWriter(commandSocket.getOutputStream()));
            commandReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static socketManager getInstance() {
        if (instance == null) {
            instance = new socketManager();
        }
        return instance;
    }
/*--------------------------------Login,register command-----------------------------------------*/    
    public DataResponse login(String user,String password) throws IOException{
        writeLineAndFlush("USER "+user, commandWriter);
        commandReader.readLine();
        writeLineAndFlush("PASS "+password, commandWriter);
        String message = commandReader.readLine();
        
        return new DataResponse(message);
    }
    
    public DataResponse register(String data) throws IOException{
        openNewDataPort();
        writeLineAndFlush("REG", commandWriter);
        System.out.println(commandReader.readLine());
        writeLineAndFlush(data, dataWriter);
        return new DataResponse(commandReader.readLine());
    }
    
    public DataResponse verifyOTP(String username,String password, String otp) throws IOException{
        writeLineAndFlush("SOTP "+username +" "+password+ " "+otp, commandWriter);
        return new DataResponse(commandReader.readLine());
    }
/*------------------------------------------------------------------------------------------*/  
    
    
/*--------------------------------file manager command--------------------------------------*/         
    public String getSharedFiles() throws IOException{
        openNewDataPort();
        writeLineAndFlush("LSHR", commandWriter);
        commandReader.readLine();
        String response=IOUtils.toString(dataReader);
        closeDataPort();
        return response;
    }
    public DataResponse getCurrentWorkingDirectory() throws IOException{
        writeLineAndFlush("PWD", commandWriter);
        return new DataResponse(commandReader.readLine());
    }
    public String getFileList(String path) throws IOException{
        openNewDataPort();
        writeLineAndFlush("MLSD "+path, commandWriter);
        commandReader.readLine();
        String response=IOUtils.toString(dataReader);
        closeDataPort();
        return response;
    }
    public DataResponse changeDirectory(String path) throws IOException{
        writeLineAndFlush("CWD "+path, commandWriter);
        String response=commandReader.readLine();
        return new DataResponse(response);
    }
    public DataResponse createNewFolder(String path) throws IOException{
        writeLineAndFlush("MKD "+path, commandWriter);
        String response=commandReader.readLine();
        System.out.println(response);
        return new DataResponse(response);
    }
    public DataResponse rename(String oldName,String newName) throws IOException{
        writeLineAndFlush("RNFR "+oldName, commandWriter);
        DataResponse res = new DataResponse(commandReader.readLine());
        if(res.getStatus()==StatusCode.FILE_ACTION_REQUIRES_INFO){
            writeLineAndFlush("RNTO "+newName, commandWriter);
            res = new DataResponse(commandReader.readLine());
        }
        return res;
    }
/*------------------------------------------------------------------------------------------*/     
    
/*---------------------------------EPSV command---------------------------------------------*/ 
    public void openNewDataPort() throws IOException{
        commandWriter.write("EPSV");
        commandWriter.newLine();
        commandWriter.flush();
        String epsvResponse = commandReader.readLine();
        int dataPort = Integer.parseInt(epsvResponse
                .replace("229 Entering Extended Passive Mode (|||", "")
                .replace("|)", ""));
        dataSocket = new Socket("localhost", dataPort);
        dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
        dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
    }    
    
    public void closeDataPort() throws IOException{
        dataWriter.close();
        dataReader.close();
        dataSocket.close();
        commandReader.readLine();
    }
/*------------------------------------------------------------------------------------------*/     
    public void disconnect() throws IOException{
        commandReader.close();
        commandWriter.close();
        commandSocket.close();
        instance=null;
    }
    public void writeLineAndFlush(String content, BufferedWriter writer) throws IOException {
        writer.append(content);
        writer.newLine();
        writer.flush();
    }

    
}
