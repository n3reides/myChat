package mychat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class MainWindow extends JFrame implements ActionListener {

    MainWindow() {
        setLayout(new GridBagLayout());
        setSize(500, 300);
        setTitle("Chat program");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel centerPanel = new JPanel();
        add(centerPanel, new GridBagConstraints());
        JButton serverStartButton = new JButton("Start server");
        JButton clientStartButton = new JButton("Start client");
        JButton exitButton = new JButton("Exit program");
        JButton instructionsButton = new JButton("Instructions");
        centerPanel.add(serverStartButton);
        centerPanel.add(clientStartButton);
        centerPanel.add(exitButton);
        centerPanel.add(instructionsButton);
        serverStartButton.addActionListener(this);
        clientStartButton.addActionListener(this);
        exitButton.addActionListener(this);
        instructionsButton.addActionListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (((JButton) (ae.getSource())).getText().equals("Start server")) {
            StartServerWindow serverWindow = new StartServerWindow();
            dispose();
        } else if (((JButton) (ae.getSource())).getText().equals("Start client")) {
            try {
                StartClientWindow clientWindow = new StartClientWindow();
                dispose();
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (((JButton) (ae.getSource())).getText().equals("Exit program")) {
            dispose();
        } else if (((JButton) (ae.getSource())).getText().equals("Instructions")) {
            JFrame instructionsFrame = new JFrame();
            instructionsFrame.setSize(400,300);
            instructionsFrame.setVisible(true);
            instructionsFrame.setLayout(new GridBagLayout());
            instructionsFrame.setResizable(false);
            JPanel instructionsPanel = new JPanel();
            JTextArea instructionsArea = new JTextArea();
            instructionsArea.setEditable(false);
            JScrollPane instructionsScrollPane = new JScrollPane(instructionsArea);
            instructionsPanel.add(instructionsScrollPane, new GridBagConstraints());
            instructionsFrame.add(instructionsPanel, new GridBagConstraints());
            instructionsArea.append("Welcome to our simple chat program" + "\n" + "Below is how to use it:" + "\n" + "1) Start a new server, with a port of your choice." + "\n" + "2) There you can see your IP and set some basic settings for your room." + "\n" + "3) To connect, add a new chatroom through the client windows." + "\n" + "4) A chatroom connection requires the IP of the server host as well as the port." + "\n" + "5) Once you save the chatroom, you can then access it with a screen name from the client window." + "\n" + "6) There are additional options for sorting different chatrooms." + "\n" + "7) To chat with somebody, just have them enter and save your IP and port as a chatroom and go wild." + "\n" + "8) The chatroom name option is simply an identifier for organizational purposes." + "\n" + "\n" + "This program was created in november 2017 by Max Sonebäck and Olle Dahlstedt as a school project.");
            instructionsFrame.setTitle("How to use and credits");
            instructionsFrame.pack();
        }
    }
}