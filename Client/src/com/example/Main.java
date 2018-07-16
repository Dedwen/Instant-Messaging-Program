package com.example;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	Client client1 = new Client("127.0.0.1");
	client1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // iese din program cand apesi 'x'
	client1.startRunning(); // running client
    }
}
