package masterblaster.chat;

import javafx.application.Platform;
import masterblaster.controller.ChatController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatTransponder {
    private Socket clientSocket;
    private PrintWriter outgoing;
    private ChatController chatWindow;

    // Started as sidekick of the requester
    public ChatTransponder(Socket clientSocket, ChatController chatWindow) {
        this.chatWindow = chatWindow;
        this.clientSocket = clientSocket;

        try {
            outgoing = new PrintWriter(clientSocket.getOutputStream());
            read();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);
        }
    }

    // Started as transponder when not requester
    public ChatTransponder(Integer port, ChatController chatWindow) {
        this.chatWindow = chatWindow;
        try {
            this.clientSocket = new Socket("localhost", port);
            this.outgoing = new PrintWriter(clientSocket.getOutputStream());
            chatWindow.disableInput(false);
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read() {
        new Thread(() -> {
            try {
                BufferedReader incoming = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String msg = incoming.readLine();
                while (msg != null) {
                    String finalMsg = msg;
                    Platform.runLater(() -> chatWindow.createIncomingLabel(finalMsg));
                    msg = incoming.readLine();
                }
                chatWindow.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                chatWindow.close();
            }
        }).start();
    }

    public void sendMessage(String message) {
        System.out.println("Sendmessage is here with message: " + message);
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
