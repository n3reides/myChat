package mychat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

class NewContactWindow extends JFrame implements ActionListener {

    private final JTextField NAME_FIELD;
    private final JTextField IP_FIELD;
    private final JTextField PORT_FIELD;
    private final JLabel CHATROOM_FOLDER_LABEL;
    private final JLabel NEW_CHATROOM_FOLDER_LABEL = new JLabel();
    private JTextField folderNameField;
    private File contactFile;
    private Path file;
    private String fileName;
    private JFrame newFolderFrame;
    private JFileChooser contactFolderChooser;

    NewContactWindow() {
        setTitle("New Chatroom");
        this.setSize(new Dimension(400, 300));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createContactsFolder();

        CHATROOM_FOLDER_LABEL = new JLabel("You are now editing " + fileName);
        

        NAME_FIELD = new JTextField();
        IP_FIELD = new JTextField();
        PORT_FIELD = new JTextField();

        addComponents();

    }

    private void createContactsFolder() {
        fileName = "MyContacts.txt";
        File dir = new File("Contacts/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        contactFile = new File(dir, fileName);
        file = contactFile.toPath();

    }

    private void addComponents() {

        JPanel textAreaPanel = new JPanel();

        GridLayout textLayout = new GridLayout(0, 1);
        textAreaPanel.setLayout(textLayout);
        textAreaPanel.add(CHATROOM_FOLDER_LABEL);
        textAreaPanel.add(new JLabel("name:"));
        textAreaPanel.add(NAME_FIELD);
        textAreaPanel.add(new JLabel("IP:"));
        textAreaPanel.add(IP_FIELD);
        textAreaPanel.add(new JLabel("Port:"));
        textAreaPanel.add(PORT_FIELD);

        JPanel northPanel = new JPanel();

        JButton chooseFolderButton = new JButton("Choose chatroom folder");
        JButton newFolderButton = new JButton("Add new folder");
        northPanel.add(chooseFolderButton, BorderLayout.WEST);
        northPanel.add(newFolderButton, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save chatroom");
        JButton backButton = new JButton("Back");
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
        GridLayout buttonLayout = new GridLayout(0, 2);
        buttonPanel.setLayout(buttonLayout);

        chooseFolderButton.addActionListener(this);
        newFolderButton.addActionListener(this);
        saveButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    private void saveContact() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String name = NAME_FIELD.getText();
        name = name.replaceAll("\\s+", ".");
        if (name.length() == 0) {
            CHATROOM_FOLDER_LABEL.setText("Please enter a chatroom name");
            return;
        }
        String IP = IP_FIELD.getText();
        IP = IP.replaceAll("\\s+", "");
        if (IP.length() == 0 || hasLetters(IP)) {
            CHATROOM_FOLDER_LABEL.setText("Please enter a valid IP Address");
            return;
        }
        String port = PORT_FIELD.getText();
        port = port.replaceAll("\\s+", "");
        int intPort;
        try {
            intPort = Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            CHATROOM_FOLDER_LABEL.setText("Please enter a valid port number");
            return;
        }

        NAME_FIELD.setText("");
        IP_FIELD.setText("");
        PORT_FIELD.setText("");
        String line = name + " " + IP + " " + Integer.toString(intPort);
        lines.add(line);
        if (contactFile.exists()) {
            Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } else {
            Files.write(file, lines, Charset.forName("UTF-8"));
        }
         CHATROOM_FOLDER_LABEL.setText("You are now editing " + fileName);
    }

    private boolean hasLetters(String IP) {
        boolean containLetters = false;
        for (char ch : IP.toCharArray()) {
            if (Character.isLetter(ch)) {
                containLetters = true;
                break;
            }
        }
        return containLetters;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() instanceof JButton) {
            switch (((JButton) (ae.getSource())).getText()) {
                case "Save chatroom":
                    try {
                        saveContact();
                    } catch (IOException ex) {
                        Logger.getLogger(NewContactWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "Back":
                    try {
                        StartClientWindow backToStartWindow = new StartClientWindow();
                        this.dispose();

                    } catch (IOException ex) {
                        Logger.getLogger(NewContactWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "Add new folder":
                    createNewFolderFrame();
                    this.dispose();
                    break;
                case "Back to new chatroom window":
                    NewContactWindow newContact = new NewContactWindow();
                    newContact.setVisible(true);
                    newFolderFrame.dispose();
                    break;
                case "Create new folder":
                    try {
                        fileName = folderNameField.getText() + ".txt";
                        File dir = new File("Contacts/");
                        dir.mkdirs();
                        contactFile = new File(dir, fileName);
                        if (!contactFile.exists()) {
                            contactFile.createNewFile();
                        }
                        folderNameField.setText("");
                        NEW_CHATROOM_FOLDER_LABEL.setText("Folder created!");

                    } catch (IOException ex) {
                        Logger.getLogger(NewContactWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "Choose chatroom folder":
                    contactFolderChooser = new JFileChooser("Contacts/");
                    int returnVal = contactFolderChooser.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File chosenFile = contactFolderChooser.getSelectedFile();
                        fileName = (String) chosenFile.getName();
                        CHATROOM_FOLDER_LABEL.setText("You are editing folder " + fileName);
                        File dir = new File("Contacts/");
                        dir.mkdirs();
                        contactFile = new File(dir, fileName);
                        file = contactFile.toPath();
                        repaint();
                    }
                    break;
                default:
                    break;
            }

        }
    }


    /* This method will create the frame for entering new folders to the Contacts folder. */
    void createNewFolderFrame() {
        newFolderFrame = new JFrame();
        newFolderFrame.setLayout(new GridBagLayout());
        newFolderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFolderFrame.setTitle("Create new folder");
        newFolderFrame.setSize(new Dimension(400, 300));
        newFolderFrame.setResizable(false);
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(0,1));    
        JButton backButton = new JButton("Back to new chatroom window");
        JButton newFolderButton = new JButton("Create new folder");
        folderNameField = new JTextField();
        folderNameField.setPreferredSize( new Dimension( 200, 30 ) );
        northPanel.add(NEW_CHATROOM_FOLDER_LABEL);
        northPanel.add(new JLabel("Enter folder name here:"));
        northPanel.add(folderNameField);
        newFolderFrame.add(northPanel, new GridBagConstraints());
        northPanel.add(newFolderButton);
        northPanel.add(backButton);
        newFolderFrame.setVisible(true);
        backButton.addActionListener(this);
        newFolderButton.addActionListener(this);
    }
}