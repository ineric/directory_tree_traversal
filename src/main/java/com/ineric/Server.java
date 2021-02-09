package com.ineric;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public Server(int port, String rootDir) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            DirectoryTreeTraversal directoryTraversal = new DirectoryTreeTraversal(rootDir);
            new Thread(directoryTraversal).start();
            while (true) {
                ClientHandler client = new ClientHandler(serverSocket.accept(), this, directoryTraversal);
                new Thread(client).start();
            }
        } catch (IOException exception) {
            LOGGER.error(exception.getMessage());
        } finally {
            try {
                serverSocket.close();
            } catch (IOException exception) {
                LOGGER.error(exception.getMessage());
            }
        }
    }

}
