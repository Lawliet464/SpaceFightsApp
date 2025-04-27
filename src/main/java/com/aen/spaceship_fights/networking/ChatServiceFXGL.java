package com.aen.spaceship_fights.networking;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class ChatServiceFXGL extends VBox {

    private PrintWriter out;
    private TextArea messageArea;
    private TextField inputField;

    public ChatServiceFXGL(String serverAddress, int port) {
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPrefHeight(200);

        inputField = new TextField();
        inputField.setPromptText("Tape ton message...");
        inputField.setOnAction(e -> sendMessage());

        this.getChildren().addAll(messageArea, inputField);
        this.setVisible(false); // au début, caché

        connectToServer(serverAddress, port);
    }

    private void connectToServer(String serverAddress, int port) {
        try {
            Socket socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String finalMessage = message;
                        Platform.runLater(() -> messageArea.appendText(finalMessage + "\n"));
                    }
                } catch (IOException ignored) {}
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            inputField.clear();
        }
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }
}
