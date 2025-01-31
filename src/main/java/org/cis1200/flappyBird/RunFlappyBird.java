package org.cis1200.flappyBird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RunFlappyBird implements Runnable {

    public static void applyFontToComponents(Font font, JComponent... components) {
        for (JComponent component : components) {
            component.setFont(font);
        }
    }

    public void run() {
        AudioPlayer.playMusic("files/lol.wav");

        Color bgColor = new Color(226, 231, 224);
        Color sideColor = new Color(240, 164, 144);
        Font customFont = new Font("Arial", Font.PLAIN, 20);

        // font
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("files/retrograde.otf"));
            customFont = customFont.deriveFont(15f); // Set font size
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        } catch (FontFormatException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }

        // Top-level frame in which game components live.
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("Flappy Bird");
        frame.setLocation(300, 200);
        frame.setBackground(bgColor);

        // bottom panel with score
        final JPanel score_panel = new JPanel();
        score_panel.setPreferredSize(new Dimension(1000, 50));
        frame.add(score_panel, BorderLayout.SOUTH);
        score_panel.setBackground(sideColor);
        final JLabel scoreBoard = new JLabel();
        score_panel.add(scoreBoard);

        // Main playing area
        final GameDisplay court = new GameDisplay(scoreBoard, true);
        court.setPreferredSize(new Dimension(1000, 400));
        frame.add(court, BorderLayout.CENTER);
        court.setBackground(bgColor);

        // Top panel with pause and reset button
        final JPanel control_panel = new JPanel();
        control_panel.setPreferredSize(new Dimension(1000, 50));
        frame.add(control_panel, BorderLayout.NORTH);
        control_panel.setBackground(sideColor);

        // Pause button
        final JButton pause = new JButton("Pause");
        pause.addActionListener(e -> {
            court.pauseToggler(pause);
            AudioPlayer.playEffect("files/press.wav");
        });
        control_panel.add(pause);

        // Reset button
        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> {
            court.reset();
            court.pauseLabelController(pause);
            AudioPlayer.playEffect("files/press.wav");
        });
        control_panel.add(reset);

        final JButton quit = new JButton("Save & Quit");
        quit.addActionListener(e -> {
            AudioPlayer.playEffect("files/press.wav");
            court.saveGame();
            System.exit(0);
        });
        control_panel.add(quit);

        final JButton help = new JButton("Help");
        help.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                String message = null;
                try {
                    message = new String(Files.readAllBytes(Paths.get("files/instructions.html")));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JEditorPane editorPane = new JEditorPane("text/html", message);
                editorPane.setEditable(false);

                // Add JEditorPane to a scrollable popup
                JScrollPane scrollPane = new JScrollPane(editorPane);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                JDialog dialog = new JDialog();
                dialog.setTitle("Instructions");
                dialog.setModal(true);
                dialog.add(scrollPane);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            });
            AudioPlayer.playEffect("files/press.wav");
        });
        control_panel.add(help);

        applyFontToComponents(customFont, scoreBoard, reset, pause, quit, help);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                try {
                    court.loadGame();
                    court.pauseLabelController(pause);
                } catch (RuntimeException e1) {
                    court.reset();
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    court.saveGame();
                } catch (RuntimeException e1) {
                    court.reset();
                }
            }
        });

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
