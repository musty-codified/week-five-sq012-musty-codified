package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static model.ChatColor.*;

public class Client {
    public static void main(String[] args) {
        System.out.println(ANSI_BLUE + "Client just entered the chat room.");
        new Client();
    }

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    public Client(){
        try{
            socket = new Socket("127.0.0.1", 7777);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            startReading();
            startWriting();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startReading(){
        Runnable r1 = () -> {
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("End")) {
                        System.out.println(ANSI_RED + "Server has terminated the chat!");
                        socket.close();
                        break;
                    }
                    System.out.println(ANSI_YELLOW + "Server: " + msg);
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
                System.out.println(ANSI_BLUE + "Connection closed.");
            }catch (Exception e){
                System.exit(0);
            }
        };
        new Thread(r2).start();
    }


}
