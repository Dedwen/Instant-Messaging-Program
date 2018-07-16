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
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message; // Textul initial
    private String serverIP;
    private Socket connection; //  conectarea a doua pc-uri
    private String nameClient = "CLIENT"; // Numele clientului
    private JButton buttonChangeName; // pt schimbarea numelui
    private JButton buttonEndSession; // pt a incheia sesiunea de chating
    private JPanel mainPanel; // Panel-ul principal

    // constructor
    public Client(String host) {

        super("Instant Message - Client"); // titlebar
        serverIP = host; // the example "127.0.0.1"- local host
        setContentPane(mainPanel); // Tot continutul trebuie pus intr-un Panel
        mainPanel.setBackground(Color.decode("#003B46"));
        //userText
        userText.setBackground(Color.decode("#07575B")); // Culoarea zonei unde se scrie #07575B
        userText.setForeground(Color.decode("#C4DFE6")); // culoarea srisului
        userText.setFont(new Font(null,Font.PLAIN,16)); // marimea textului
        userText.setEditable(false);
        userText.setText("Click and type text here...");
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        sendMessage(event.getActionCommand(),nameClient); // ia ce se introduce din tastatura
                        userText.setText("Click and type text here...");
                    }
                }
        );
        userText.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                userText.setText("");
            }
        });
        //buttonChangeName
        buttonChangeName.setBackground(Color.decode("#07575B"));
        buttonChangeName.setForeground(Color.decode("#C4DFE6"));
        buttonChangeName.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        String oldNameClient = nameClient;
                        nameClient = JOptionPane.showInputDialog("Enter your name:");
                        if ( !nameClient.equalsIgnoreCase("server") ) {
                            chatWindow.append("\nTi-ai schimbat numele din " + oldNameClient + " in " + nameClient + "."); //  mesajul pe care il vad eu
                            try {
                                output.writeObject("\n" + oldNameClient + " si-a schimbat numele in " + nameClient + ".");//mesajul pe care il vede celalalt
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }else {
                            JOptionPane.showMessageDialog(null,"Nu poti utiliza acest nume!","WARNING!",JOptionPane.WARNING_MESSAGE);
                            nameClient = oldNameClient;
                        }
                    }
                }
        );
        //buttonEndSession
        buttonEndSession.setBackground(Color.decode("#07575B"));
        buttonEndSession.setForeground(Color.decode("#C4DFE6"));
        buttonEndSession.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        try {
                            output.writeObject("CLIENT - END");  // Actiunea butonului de iesire(afiseaza mesajul celuilalt => iesire)
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
        );
        //chatWindow
        chatWindow.setBackground(Color.decode("#66A5AD")); // Culoarea zonei unde se afiseaza #66A5AD
        chatWindow.setForeground(Color.decode("#C4DFE6")); // culoarea srisului
        chatWindow.setFont(new Font(null,Font.PLAIN,16)); // marimea scrisului
        chatWindow.setEditable(false);
        setSize(800, 400);
        setResizable(false);
        setVisible(true);

    }


    // connect to server
    public void startRunning() {

        try {
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException eofException) {
            showMessage("\nClient terminated connection.");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeCrap();
        }

    }


    // connect to a server
    private void connectToServer() throws IOException {

        showMessage("Attempting connection...\n");
        connection = new Socket(InetAddress.getByName(serverIP), 6789); // se realizeaza conexiunea cu serverul
        showMessage("Connected to: " + connection.getInetAddress().getHostName()); // afiseaza conexiunea cu serverul

    }


    //set up streams to send and receive message
    private void setupStreams() throws IOException {

        output = new ObjectOutputStream(connection.getOutputStream()); // cream conexiunea cu serverul pt a trimite date
        output.flush();
        input = new ObjectInputStream(connection.getInputStream()); // cream conexiunea cu serverul pt a primi date
        showMessage("\nStreams are good to go!\n");

    }


    //while chatting with server
    private void whileChatting() throws IOException {

        ableToType(true);
        do {
            try {

                message = (String) input.readObject(); // mesajul primit se salveaza ca obiect
                showMessage("\n" + message);

            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\nI dont know that object type");
            }

        } while (!message.equals("SERVER - END"));

    }


    // close the streams and sockets
    private void closeCrap() {

        showMessage("\nClosing crap down..");
        ableToType(false);
        try {

            output.close();      //
            input.close();       // Inchidem
            connection.close();  //

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }


    //send message to server
    private void sendMessage(String message,String nameClient) {

        try {
            output.writeObject(nameClient + " - " + message); // mesajul trimis de noi se salveaza ca obiect
            output.flush();
            showMessage("\n" + nameClient + " - " + message);

        } catch (IOException ioException) {
            chatWindow.append("\nSomehing wrong in sending to a messege");
        }

    }


    //change/update chatWindow
    private void showMessage(final String m) {

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(m);
                    }
                }
        );

    }


    // give permission to type
    private void ableToType(final boolean tof) {

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




