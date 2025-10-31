package main;

import javax.swing.*;
import javax.swing.JFrame.*;
public class  Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Harvest Realm");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);
        gamePanel.startGameThread();
    }
}