/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.util.List;
import payloads.UserPermission;
import socket.socketManager;

/**
 *
 * @author Bum
 */
public class testLSUR {
    public static void main(String[] args) throws Exception {
        socketManager.getInstance().login("testuser","test");
        List<UserPermission> userPermission = socketManager.getInstance().getShareUserList("/users/testuser");
        System.out.println(userPermission.get(0).getProcessedPermission().get("downloadable"));
    }
}
