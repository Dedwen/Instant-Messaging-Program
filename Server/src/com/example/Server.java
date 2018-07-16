package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {

    private JTextField userText; // Unde scrie userul
    private JTextArea chatWindow; // Unde apare textul scris
    private JPanel mainPanel;
    private ObjectOutputStream output; // Ceea ce trimit altui pc
    private ObjectInputStream input; // Ceea ce primesc de la alt pc
    private ServerSocket server;
    private Socket connection; // Legatura dintre client si server


    //constructor
    public Server() {

        super("Instant Message - Server"); // Titlebar
        setContentPane(mainPanel);
        mainPanel.setBackground(Color.decode("#003B46"));
        //userText
        userText.setBackground(Color.decode("#07575B")); // Culoarea zonei unde se scrie #07575B
        userText.setForeground(Color.decode("#C4DFE6")); // culoarea srisului
        userText.setFont(new Font(null,Font.PLAIN,16)); // marimea textului
        userText.setEditable(false);
        userText.setText("Click and type text here...");
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sendMessage(event.getActionCommand()); // Actiunea trimiterii mesajului ca user "getActionCommand()
                userText.setText("Click and type text here...");
            }
        });
        userText.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                userText.setText("");
            }
        });
        //chatWindow
        chatWindow.setBackground(Color.decode("#66A5AD")); // Culoarea zonei unde se afiseaza #66A5AD
        chatWindow.setForeground(Color.decode("#C4DFE6")); // culoarea srisului
        chatWindow.setFont(new Font(null,Font.PLAIN,16)); // marimea scrisului
        chatWindow.setEditable(false);
        setSize(800, 400);
        setResizable(false);
        setVisible(true);

    }


    //set up and run the server
    public void startRunning() {

        try {
            server = new ServerSocket(6789, 100); // portul- prin care ne conectam si backlog- nr de useri
            while (true) {
                try {

                    //connect to a conversation
                    waitForConnection();
                    setUpStreams();
                    whileChatting();

                } catch (EOFException eofException) {
                    showMessage("\nServerul a terminat conexiunea!");
                } finally {
                    closeCrap();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }


    // asteapta pentru o conexiune si afiseaza un mesaj corespunzator
    public void waitForConnection() throws IOException {

        showMessage("Waiting to someone to connect...\n");
        connection = server.accept(); // Asteapta acceptarea unui client/pc pe server
        showMessage("Now connected to: "+connection.getInetAddress().getHostName()+"\n"); // Afiseaza conexiunea cu clientul

    }


    // ia stream-ul pentru a primi si accepta date
    public void setUpStreams() throws IOException {

        output = new ObjectOutputStream(connection.getOutputStream());// cream conexiunea catre alt pc/client pt a trimite date
        output.flush(); // recomandat dupa ce trimitem un output
        input = new ObjectInputStream(connection.getInputStream()); // cream conexiunea pentru a primi de la alt pc/client date
        showMessage("Streams are now setup!\n");

    }


    // cat timp tine conversatia
    public void whileChatting() throws IOException {

        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);
        do {
            //have a conversation
            try {

                message = (String) input.readObject();// mesajul pe care il trimite celalalt/clientul e salavat ca obiect
                showMessage("\n" + message); // pentru a separa fiecare mesaj trimis

            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\n idk what the user send!");
            }

        } while (!message.equals("CLIENT - END"));// when the user type "END" atunci opreste programul

    }


    // close streams and sockets after you are done chatting
    public void closeCrap() {

        showMessage("\nClosing connections...\n");
        ableToType(false);
        try {
            output.close();     //
            input.close();      //  Inchidem
            connection.close(); //

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }


    //send a message  to client
    private void sendMessage(String message) {

        try {
            output.writeObject("SERVER - " + message);// writeObj() trimite mesajul catre output
            output.flush();
            showMessage("\nSERVER - " + message);
        } catch (IOException ioException) {
            chatWindow.append("\n ERROR:I cant send that message");
        }

    }


    // updates chatWindow messages
    private void showMessage(final String text) {

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(text);
                    }
                }
        );

    }


  // lasa userul sa scrie in casuta lui
    private void ableToType(final boolean tof ){

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );

    }


}
