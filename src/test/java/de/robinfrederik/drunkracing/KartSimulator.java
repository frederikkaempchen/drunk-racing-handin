package de.robinfrederik.drunkracing;

import javax.swing.*;

public class KartSimulator extends JFrame {

    public KartSimulator() {
        setTitle("Go-Kart Simulator");
        setSize(1000, 750); // Set initial window size (width, height)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setLocationRelativeTo(null); // Center window on screen

        KartPanel panel = new KartPanel();
        add(panel); // Add the custom JPanel to the frame

        setVisible(true); // Make the frame visible
    }

    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(KartSimulator::new);
    }
}
