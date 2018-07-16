package com.example;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Server server1 = new Server();
        server1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // iese din aplicatie cand apesi 'x'
        server1.startRunning(); // ruleaza clientul
    }
}
