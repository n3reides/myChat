/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author olda4871
 */
class NewContact extends JFrame implements ActionListener {
    
    JTextField textFieldName;
    JTextField textFieldIP;
    JTextField textFieldPort;

    NewContact() {
        setTitle("New Contact");
        this.setSize(new Dimension(400, 300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //JComboBox comboBox = new JComboBox();
        JPanel textAreaPanel = new JPanel();
        textFieldName = new JTextField();
        textFieldIP = new JTextField();
        textFieldPort = new JTextField();
        GridLayout textLayout = new GridLayout(0, 1);
        textAreaPanel.setLayout(textLayout);
        textAreaPanel.add(new JLabel("name:"));
        textAreaPanel.add(textFieldName);
        textAreaPanel.add(new JLabel("IP:"));
        textAreaPanel.add(textFieldIP);
        textAreaPanel.add(new JLabel("Port:"));
        textAreaPanel.add(textFieldPort);
        add(new JLabel("New Contact"), BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        GridLayout buttonLayout = new GridLayout(0, 2);
        buttonPanel.setLayout(buttonLayout);
        buttonPanel.add(new JButton("Save Contact"));
        buttonPanel.add(new JButton("Back"));
        add(buttonPanel, BorderLayout.SOUTH);
        .addActionListener(this);
    }

    private void saveContact() throws IOException {
        ArrayList<String> lines = new ArrayList<String>(3);
        String name = textFieldName.getText();
        String IP = textFieldIP.getText();
        String port = textFieldPort.getText();
        lines.set(0, name);
        lines.set(1, IP);
        lines.set(2, port);
        Path file = Paths.get(name + ".contactdetails.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Save Contact")) {
                try {
                    saveContact();
                } catch (IOException ex) {
                    Logger.getLogger(NewContact.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
