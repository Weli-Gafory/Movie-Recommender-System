package masterblaster.chat;

import masterblaster.controller.ChatController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatRequester {
    private ChatController chatWindow;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public ChatRequester(ChatController chatWindow) {
        this.chatWindow = chatWindow;
    }

    public ChatTransponder getRequesterTransponder(Integer port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            serverSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);
        }
        return new ChatTransponder(clientSocket, this.chatWindow);
    }
}
