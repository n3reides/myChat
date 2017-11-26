package mychat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class NewContact extends JFrame implements ActionListener {

    JFrame newFolderFrame;
    JTextField textFieldName;
    JTextField textFieldIP;
    JTextField textFieldPort;
    JTextField folderNameField;
    JButton backButton;
    JButton saveButton;
    File contactFile;
    Path file;
    String fileName;
    JPanel northPanel;
    JLabel chosenFolder;
    JFileChooser contactFolderChooser;

    NewContact() {
        setTitle("New Contact");
        this.setSize(new Dimension(400, 300));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        northPanel = new JPanel();
        chosenFolder = new JLabel();
        JButton chooseFolderButton = new JButton("Choose contact folder");
        northPanel.add(chooseFolderButton, BorderLayout.WEST);
        JButton newFolderButton = new JButton("Add new folder");
        northPanel.add(newFolderButton, BorderLayout.EAST);
        JPanel textAreaPanel = new JPanel();
        textFieldName = new JTextField();
        textFieldIP = new JTextField();
        textFieldPort = new JTextField();
        GridLayout textLayout = new GridLayout(0, 1);
        textAreaPanel.setLayout(textLayout);
        textAreaPanel.add(chosenFolder);
        textAreaPanel.add(new JLabel("name:"));
        textAreaPanel.add(textFieldName);
        textAreaPanel.add(new JLabel("IP:"));
        textAreaPanel.add(textFieldIP);
        textAreaPanel.add(new JLabel("Port:"));
        textAreaPanel.add(textFieldPort);
        //add(new JLabel("New Contact"), BorderLayout.NORTH);
        add(northPanel, BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        GridLayout buttonLayout = new GridLayout(0, 2);
        buttonPanel.setLayout(buttonLayout);
        saveButton = new JButton("Save Contact");
        backButton = new JButton("Back");
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        chooseFolderButton.addActionListener(this);
        newFolderButton.addActionListener(this);
        saveButton.addActionListener(this);
        backButton.addActionListener(this);
        add(buttonPanel, BorderLayout.SOUTH);
        fileName = "MyContacts.txt";
        File dir = new File("Contacts/");
        dir.mkdirs();
        contactFile = new File(dir, fileName);
        file = contactFile.toPath();
    }

    private void saveContact() throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        String name = textFieldName.getText();
        name = name.replaceAll("\\s+", ".");
        String IP = textFieldIP.getText();
        IP = IP.replaceAll("\\s+", "");
        String port = textFieldPort.getText();
        port = port.replaceAll("\\s+", "");
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
        if (contactFile.exists()) {
            Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } else {
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
            } else if (((JButton) (ae.getSource())).getText().equals("Back")) {
                try {
                    StartWindow backToStartWindow = new StartWindow();
                    this.dispose();

                } catch (IOException ex) {
                    Logger.getLogger(NewContact.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (((JButton) (ae.getSource())).getText().equals("Add new folder")) {
                createNewFolderFrame();
                this.dispose();
            } else if (((JButton) (ae.getSource())).getText().equals("Back to new contact window")) {
                NewContact newContact = new NewContact();
                newContact.setVisible(true);
                newFolderFrame.dispose();
            } else if (((JButton) (ae.getSource())).getText().equals("Create new folder")) {
                try {
                    fileName = folderNameField.getText() + ".txt";
                    File dir = new File("Contacts/");
                    String[] emptyStringArray;
                    dir.mkdirs();
                    contactFile = new File(dir, fileName);
                    //     file = contactFile.;
                    if (!contactFile.exists()) {
                        contactFile.createNewFile();
                    }

                } catch (IOException ex) {
                    Logger.getLogger(NewContact.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (((JButton) (ae.getSource())).getText().equals("Choose contact folder")) {
                contactFolderChooser = new JFileChooser("Contacts/");
                int returnVal = contactFolderChooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = contactFolderChooser.getSelectedFile();
                    fileName = (String) chosenFile.getName();
                    chosenFolder.setText("You are editing folder " + fileName);
                    File dir = new File("Contacts/");
                    dir.mkdirs();
                    contactFile = new File(dir, fileName);
                    file = contactFile.toPath();
                    repaint();
                }
            }

        }
    }


    /* This method will create the frame for entering new folders to the Contacts folder. */
    void createNewFolderFrame() {
        newFolderFrame = new JFrame();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFolderFrame.setTitle("Create new folder");
        newFolderFrame.setSize(new Dimension(400, 300));
        newFolderFrame.setResizable(false);
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        JButton backButton = new JButton("Back to new contact window");
        JButton newFolderButton = new JButton("Create new folder");
        folderNameField = new JTextField(30);
        newFolderFrame.add(northPanel, BorderLayout.NORTH);
        northPanel.add(new JLabel("Enter folder name here:"), BorderLayout.NORTH);
        northPanel.add(folderNameField, BorderLayout.SOUTH);
        newFolderFrame.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(backButton, BorderLayout.WEST);
        southPanel.add(newFolderButton, BorderLayout.EAST);
        newFolderFrame.setVisible(true);
        backButton.addActionListener(this);
        newFolderButton.addActionListener(this);
        newFolderFrame.pack();
    }
}

