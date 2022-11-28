package clientServer.chat;

import clientServer.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GroupChatStarter {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void getRequesterTransponder(Integer port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);
        }
        while (true) {
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ClientHandler.chattingClients.get(port).add(new GroupChatTransponder(clientSocket));
        }
    }
}
