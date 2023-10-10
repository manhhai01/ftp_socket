/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Bum
 */
public class server {
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in, stdin;
    public server(int port){
        try{
            System.out.println("Starting server.....");
            serverSocket = new ServerSocket(port);
            System.out.println("Success!");
            System.out.println("wating for connect.....");
        }catch (IOException e){
            System.err.println();
        }
    }
    public boolean startServer(){
        try {
            socket = serverSocket.accept();
            out = new PrintWriter(socket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("connect success from " + socket.getLocalSocketAddress());
            return true;
        }catch (IOException e){
            return false;
        }
    }
    public void handleClient(){
        try{
            while (true){
                String input = in.readLine();
                if (input.equalsIgnoreCase("bye")){
                    out.println("bye");
                    System.out.println("closing... ");
                    close();
                    System.out.println("success!");
                    break;
                }
            }
        }catch (IOException e){
            System.err.println();
        }
    }
    private void close() throws IOException {
        socket.close();
    }
    public static void main(String[] args) {
        server tcpServer = new server(1234);
        if (tcpServer.startServer()){
            tcpServer.handleClient();
        }
    }
}
