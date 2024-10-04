package org;

import org.foxesworld.foxesSound.decoder.JavaLayerException;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Start extends JFrame {

    private static final int FRAME_WIDTH = 300;
    private static final int FRAME_HEIGHT = 250;
    private JPanel container;
    private JButton btn;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Start frame = new Start();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public Start() {
        setTitle("foxesSound 1.2.0");
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        container = new JPanel();
        container.setLayout(new BorderLayout());
        add(container, BorderLayout.CENTER);
        createScene();
    }

    private void createScene() {
        btn = new JButton("Test snd");
        btn.addActionListener(event -> {
            btn.setEnabled(false);
            Thread playSnd = new Thread(() -> {
                try {
                    new Test(this).test();
                } catch (JavaLayerException | FileNotFoundException | InterruptedException ex) {
                    Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            playSnd.start();
        });
        container.add(btn, BorderLayout.CENTER);
    }

    public JButton getBtn() {
        return btn;
    }
}