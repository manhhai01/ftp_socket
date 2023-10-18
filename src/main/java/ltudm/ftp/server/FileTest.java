/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ltudm.ftp.server;

import java.io.File;

/**
 *
 * @author User
 */
public class FileTest {
    public static void main(String[] args) {
        File file = new File("ftp/testuser");
        System.out.println("Files: "+ file.list()[0]);
    }
}
