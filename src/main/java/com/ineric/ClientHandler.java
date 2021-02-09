package com.ineric;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private static final String COMMAND_DELIMITER = " ";
    private static final String PARAM_DEPTH = "-depth";
    private static final String PARAM_MASK = "-mask";
    private static final int HANDLER_DELAY = 10;
    private static final String LOGGER_REQUEST_PATTERN = "request: {}";

    private final Server server;
    private final DirectoryTreeTraversal directoryTraversal;

    private PrintWriter responseStream;
    private Scanner requestStream;

    public ClientHandler(Socket socket, Server server, DirectoryTreeTraversal directoryTraversal) {
        this.directoryTraversal = directoryTraversal;
        this.server = server;
        try {
            this.responseStream = new PrintWriter(socket.getOutputStream());
            this.requestStream = new Scanner(socket.getInputStream());
        } catch (IOException exception) {
            LOGGER.error(exception.toString());
        }
    }

    @Override
    public void run() {
        runHandler();
    }

    private void runHandler() {
        while (true) {
            if (requestStream.hasNext()) {
                try {
                    String request =requestStream.nextLine();
                    LOGGER.info(LOGGER_REQUEST_PATTERN, request);
                    prepareResponse(request);
                } catch (RuntimeException exception) {
                    LOGGER.error(exception.getMessage());
                    sendResponse(exception.getMessage());
                }
            }
            try {
                Thread.sleep(HANDLER_DELAY);
            } catch (InterruptedException ignored) {
            }
        }

    }

    private void prepareResponse(String request) {
        String[] args = request.split(COMMAND_DELIMITER);
        PassageOptions passageOptions = new PassageOptions();
        passageOptions.setResults(this::prepareResult);
        try {
            readParamsToOptions(args, passageOptions);

            if (passageOptions.getDepth() != null && passageOptions.getMask() != null) {
                directoryTraversal.addPassageOptions(passageOptions);
            }

        } catch (NumberFormatException exception) {
            LOGGER.error("Error read parameters. {}", exception.getMessage());
        }
    }

    private void readParamsToOptions(String[] args, PassageOptions passageOptions) {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals(PARAM_DEPTH)) {
                passageOptions.setDepth(Integer.parseInt(args[i + 1]));
            }
            if (args[i].equals(PARAM_MASK)) {
                passageOptions.setMask(args[i + 1]);
            }
        }
    }

    private void prepareResult(List<String> results) {
        results.forEach(this::sendResponse);
    }

    private void sendResponse(String response) {
        try {
            responseStream.println(response);
            responseStream.flush();
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage());
        }
    }

}
