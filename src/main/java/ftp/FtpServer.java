package ftp;

import ftp.commands.TYPECommand;
import ftp.commands.USERCommand;
import ftp.commands.RNTOCommand;
import ftp.commands.PWDCommand;
import ftp.commands.RETRCommand;
import ftp.commands.STORCommand;
import ftp.commands.MLSDCommand;
import ftp.commands.PASSCommand;
import ftp.commands.RNFRCommand;
import ftp.commands.MKDCommand;
import ftp.commands.FEATCommand;
import ftp.commands.CWDCommand;
import ftp.commands.Command;
import ftp.commands.DELECommand;
import ftp.commands.EPSVCommand;
import ftp.commands.AUTHCommand;
import ftp.commands.GOTPCommand;
import ftp.commands.REGCommand;
import ftp.commands.RMDCommand;
import ftp.commands.SHRECommand;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FtpServer {

    private Map<String, Command> commands = new HashMap<>();
    private Map<String, FtpServerSession> sessions = new HashMap<>();
    private List<Command> onSocketConnectCommands = new ArrayList<>();
    private List<Command> onSocketDisconnectCommands = new ArrayList<>();
    private ServerSocket server;

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, Command> commands) {
        this.commands = commands;
    }

    public Map<String, FtpServerSession> getSessions() {
        return sessions;
    }

    public List<Command> getOnSocketConnectCommands() {
        return onSocketConnectCommands;
    }

    public void setOnSocketConnectCommands(List<Command> onSocketConnectCommands) {
        this.onSocketConnectCommands = onSocketConnectCommands;
    }

    public List<Command> getOnSocketDisconnectCommands() {
        return onSocketDisconnectCommands;
    }

    public void setOnSocketDisconnectCommands(List<Command> onSocketDisconnectCommands) {
        this.onSocketDisconnectCommands = onSocketDisconnectCommands;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    private InputParseResult parseInput(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input);
        List<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        return new InputParseResult(
                tokens.get(0),
                tokens.subList(1, tokens.size()).toArray(String[]::new)
        );
    }

//    private void executeCommandAndResponse(BufferedWriter writer, Command command, String[] args, FtpServerSession session) {
//        CommandResult result = command.execute(args, session);
//        if (result != null) {
//            try {
//                SocketUtils.writeLineAndFlush(result.commandResponse(), writer);
//            } catch (IOException ex) {
//                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
    private void matchCommand(String input, BufferedWriter writer, FtpServerSession session) {
        System.out.println("Input: " + input);
        InputParseResult parsedInput = parseInput(input);
        Command command = commands.get(parsedInput.commandName());
        if (command != null) {
            command.execute(parsedInput.args(), session, writer);
        } else {
            try {
                SocketUtils.writeLineAndFlush("500 Command is not recognised.", writer);
            } catch (IOException ex) {
                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void start() {
        try {
            server = new ServerSocket(21);
            System.out.println("Server started on port 21");
        } catch (IOException ex) {
            Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            try {
                Socket socket = server.accept();
                sessions.put(socket.getRemoteSocketAddress().toString(), new FtpServerSession(socket.getRemoteSocketAddress().toString()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // Call socket connection listeners
                onSocketConnectCommands.forEach((command) -> {
                    command.execute(null, sessions.get(socket.getRemoteSocketAddress().toString()), writer);
                });

                // Create new thread for listening to command socket
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String input = "";
                        try {
                            // Listen on commands
                            while ((input = reader.readLine()) != null) {
                                matchCommand(input, writer, sessions.get(socket.getRemoteSocketAddress().toString()));
                            }
                            // Call socket disconnection listeners
                            onSocketDisconnectCommands.forEach((command) -> {
                                command.execute(null, sessions.get(socket.getRemoteSocketAddress().toString()), writer);
                            });
                            sessions.remove(socket.getRemoteSocketAddress().toString());
                        } catch (IOException ex) {
                            Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

                thread.start();

            } catch (IOException ex) {
                Logger.getLogger(FtpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        FtpServerBuilder ftpServerBuilder = new FtpServerBuilder();

        ftpServerBuilder.addOnConnectCommand(new Command() {
            @Override
            public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
                try {
                    SocketUtils.writeLineAndFlush("220 Service ready for new user.", commandSocketWriter);

                } catch (IOException ex) {
                    Logger.getLogger(FtpServer.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        ftpServerBuilder.addCommand("AUTH", new AUTHCommand());
        ftpServerBuilder.addCommand("USER", new USERCommand());
        ftpServerBuilder.addCommand("PASS", new PASSCommand());
        ftpServerBuilder.addCommand("PWD", new PWDCommand());
        ftpServerBuilder.addCommand("MLSD", new MLSDCommand());
        ftpServerBuilder.addCommand("FEAT", new FEATCommand());
        ftpServerBuilder.addCommand("TYPE", new TYPECommand());
        ftpServerBuilder.addCommand("EPSV", new EPSVCommand());
        ftpServerBuilder.addCommand("CWD", new CWDCommand());
        ftpServerBuilder.addCommand("RETR", new RETRCommand());
        ftpServerBuilder.addCommand("STOR", new STORCommand());
        ftpServerBuilder.addCommand("MKD", new MKDCommand());
        ftpServerBuilder.addCommand("DELE", new DELECommand());
        ftpServerBuilder.addCommand("RNFR", new RNFRCommand());
        ftpServerBuilder.addCommand("RNTO", new RNTOCommand());
        ftpServerBuilder.addCommand("RMD", new RMDCommand());
        ftpServerBuilder.addCommand("SHRE", new SHRECommand());
        ftpServerBuilder.addCommand("REG", new REGCommand());
        ftpServerBuilder.addCommand("GOTP", new GOTPCommand());

        ftpServerBuilder.addOnDisconnectCommand(new Command() {
            @Override
            public void execute(String[] arguments, FtpServerSession session, BufferedWriter commandSocketWriter) {
                System.out.println("Client disconnected");
            }
        });

        FtpServer ftpServer = ftpServerBuilder.build();
        ftpServer.start();
    }
}
