/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ftp;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author User
 */
public class SocketUtils {
    public static void writeLineAndFlush(String content, BufferedWriter writer) throws IOException {
        writer.append(content);
        writer.newLine();
        writer.flush();
    }
    
    public static void respondCommandSocket(String message, BufferedWriter socketWriter) throws IOException {
        SocketUtils.writeLineAndFlush(message, socketWriter);
    }
}
