package modell;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
        public static List<ClientHandler> clientHandlers = new ArrayList<>();
        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private String clientUsername;

        public ClientHandler(Socket socket) {
            try{
                this.socket = socket;
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientUsername = bufferedReader.readLine();
                clientHandlers.add(this);
                broadcastMessage("SERVER: " + clientUsername + " has joined the chat room!");
            } catch (IOException e){
                removeClientHandler();
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }


        @Override
        public void run() {
            String messageFromClient;

            while (true){
                try {
                    messageFromClient = bufferedReader.readLine();
                    if (messageFromClient == null){
                        throw new IOException();
                    }
                    broadcastMessage(messageFromClient);
                } catch (IOException e){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }

        public void broadcastMessage(String messageToSend){
            for (ClientHandler clientHandler : clientHandlers){
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername)){
                        clientHandler.bufferedWriter.write(messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                }catch (IOException e){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
        public void removeClientHandler(){
            clientHandlers.remove(this);
            broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
        }
        public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
            removeClientHandler();
            try {
                if(bufferedReader != null){
                    bufferedReader.close();
                }
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
                if(socket != null){
                    socket.close();
                }
            }catch (IOException e){
                System.exit(0);
            }
        }

    }
