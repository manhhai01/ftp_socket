/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.io.IOException;
import payloads.DataResponse;
import socket.socketManager;

/**
 *
 * @author Son
 */
public class testMLSD {
    public static void main(String[] args) throws IOException {
        socketManager.getInstance().login("testuser", "test");
        DataResponse res = socketManager.getInstance().getCurrentWorkingDirectory();
        String dir = res.getMessage().substring(0,res.getMessage().lastIndexOf("\""));
        System.out.println(dir.replace("\"", ""));
        String fileLists = socketManager.getInstance().getFileList(dir);
        System.out.println(fileLists);
        System.out.println("aaaaa");
    }
}
