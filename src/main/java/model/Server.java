package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static model.ChatColor.*;

public class Server {

    public static void main(String[] args) {
        System.out.println(ANSI_BLUE + "Starting up the Chat Server...");
        new Server();
    }

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    public Server(){
        try {
            server = new ServerSocket(7777);
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            startReading();
            startWriting();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startReading(){
        Runnable r1 = () -> {
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("End")) {
                        System.out.println(ANSI_RED + "Client has terminated the chat!");
                        socket.close();
                        break;
                    }
                    System.out.println(ANSI_YELLOW + "Client: " + msg);
                }
            }catch (Exception e){
                System.exit(0);
            }
        };
        new Thread(r1).start();
    }

    public void startWriting(){
        Runnable r2 = () -> {
            try {
                while (!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("End")){
                        socket.close();
                        break;
                    }
                }
            }catch (Exception e){
                System.exit(0);
            }
        };
        new Thread(r2).start();
    }
}
