package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller {
    public Socket socket;
    public DataInputStream in;
    public DataOutputStream out;
    @FXML
    TextArea messageWindow;

    @FXML
    TextArea userList;

    @FXML
    TextField textField;

    @FXML
    private void send() {
        String str = textField.getText();
        try {
            out.writeUTF(str);
            textField.clear();
            textField.requestFocus();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void startClient() {
        try {
            socket = new Socket("localhost", 8188);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            String response = in.readUTF();
            messageWindow.appendText("\n" + response);
            Thread responseThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!socket.isClosed()) {
                        String response = null;
                        try {
                            response = in.readUTF();
                            String names = response.substring(1);
                            String[] responseArray = names.split("]##");
                            String[] allUsers = responseArray[0].split(",");

                            StringBuilder sb = new StringBuilder();
                            for (String allUser : allUsers) {
                                String name = allUser.trim();
                                sb.append(name).append("\n");
                            }
                            userList.setText(sb.toString());
                            messageWindow.appendText("\n" + responseArray[1]);
                        } catch (IOException exception) {
                            messageWindow.appendText("\n" + "Сервер недоступен!");
                            System.out.println("Сервер недоступен!");
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            responseThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}