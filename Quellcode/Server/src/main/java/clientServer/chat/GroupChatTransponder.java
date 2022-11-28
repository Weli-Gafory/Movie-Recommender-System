package clientServer.chat;

import clientServer.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GroupChatTransponder {
    private Socket clientSocket;
    private PrintWriter outgoing;

    // Started as sidekick of the requester
    public GroupChatTransponder(Socket clientSocket) {
        this.clientSocket = clientSocket;

        try {
            outgoing = new PrintWriter(clientSocket.getOutputStream());
            read();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);
        }
    }

    private void read() {
        new Thread(() -> {
            try {
                BufferedReader incoming = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String msg = incoming.readLine();
                while (msg != null) {
                    // echo the messages back to all in group chat
                    for (GroupChatTransponder transponder: ClientHandler.chattingClients.get(clientSocket.getLocalPort())) {
                        transponder.sendMessage(msg);
                    }
                    msg = incoming.readLine();
                }
                closeConnection();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                closeConnection();
            }
        }).start();
    }

    public void sendMessage(String message) {
        outgoing.println(message);
        outgoing.flush();
    }

    public void closeConnection() {
        try {
            outgoing.close();
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
