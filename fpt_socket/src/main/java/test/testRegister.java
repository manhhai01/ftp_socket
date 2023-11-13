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
public class testRegister {
    public static void main(String[] args) throws IOException {
        String username= "testuser3";
        String password= "test3";
        String firstname= "Nguyễn";
        String lastname= "Chinh";
        String gender="Nam";
        String birthday = "12/11/2001";
        String data=String.format("{\n"
                + "            \"username\": \"%s\",\n"
                + "            \"password\": \"%s\";\n"
                + "            \"firstName\": \"%s\",\n"
                + "            \"lastName\": \"%s\",\n"
                + "            \"gender\": \"%s\",\n"
                + "            \"birthday\": \"%s\"\n"
                + "        }",username,password,firstname,lastname,gender,birthday);
        DataResponse res = socketManager.getInstance().register(data);
        System.out.println(res.getMessage());
    }
}
