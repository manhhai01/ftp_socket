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
public class testLSHR {
    public static void main(String[] args) throws IOException {
        socketManager.getInstance().login("testuser2", "test2");
        String res = socketManager.getInstance().getSharedFiles();
        System.out.println(res);
    }
}
