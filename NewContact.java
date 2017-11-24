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
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
 * @author Max
 */
public class NewContact extends JFrame implements ActionListener {

    JTextField textFieldName;
    JTextField textFieldIP;
    JTextField textFieldPort;
    JButton backButton, saveButton;
    File contactFile;
    Path file;

    NewContact() {
        setTitle("New Contact");
        this.setSize(new Dimension(400, 300));
        setResizable(false);
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

        //add(new JLabel("New Contact"), BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        GridLayout buttonLayout = new GridLayout(0, 2);
        buttonPanel.setLayout(buttonLayout);
        saveButton = new JButton("Save Contact");
        backButton = new JButton("Back");

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        saveButton.addActionListener(this);
        backButton.addActionListener(this);

        add(buttonPanel, BorderLayout.SOUTH);

        String fileName = "MyContacts.txt";
        File dir = new File("Contacts/");
        dir.mkdirs();
        contactFile = new File(dir, fileName);
        file = contactFile.toPath();

    }

    private void saveContact() throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        String name = textFieldName.getText();
        name = name.replaceAll("\\s+",".");
        String IP = textFieldIP.getText();
        IP = IP.replaceAll("\\s+","");
        String port = textFieldPort.getText();
        port = port.replaceAll("\\s+","");
        textFieldName.setText("");
        textFieldIP.setText("");
        textFieldPort.setText("");
        //lines.add(name);
        //lines.add(IP);
        //lines.add(port);
        String line = name + " " + IP + " " + port;
        lines.add(line);

        /*String fileName = "MyContacts.txt";
        File dir = new File("Contacts/");
        dir.mkdirs();
        File contactFile = new File(dir, fileName);
        Path file = contactFile.toPath(); */
        if( contactFile.exists()){
        Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        }else{
             Files.write(file, lines, Charset.forName("UTF-8"));
        }
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
