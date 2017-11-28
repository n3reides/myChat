package mychat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

final class StartClientWindow extends JFrame implements ActionListener {

    private final JButton newContactButton;
    private final JPanel northPanel;
    private final JPanel centerPanel;
    private final JPanel southPanel;
    private JComboBox contactBox;
    private Contact contactPicked;
    private JTextField nameTextField;

    StartClientWindow() throws IOException {
        setTitle("Start Window");
        setSize(new Dimension(400, 300));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        northPanel = new JPanel();
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 1));
        southPanel = new JPanel();

        createContactsFolder();
        createContactBox();

        newContactButton = new JButton("Add new contact");
        JButton backButton = new JButton("Back");
        nameTextField = new JTextField();
        JButton chooseFolderButton = new JButton("Choose contact folder");
        northPanel.add(new JLabel("Pick your contact folder"), BorderLayout.NORTH);
        northPanel.add(chooseFolderButton, BorderLayout.SOUTH);
        southPanel.add(newContactButton);
        southPanel.add(backButton, BorderLayout.EAST);
        centerPanel.add(new JLabel("Choose contact"));
        centerPanel.add(contactBox);
        centerPanel.add(new JLabel("Write your username"));
        centerPanel.add(nameTextField);
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(this);
        //centerPanel.add(contactBox);
        centerPanel.add(connectButton, BorderLayout.SOUTH);

        chooseFolderButton.addActionListener(this);
        backButton.addActionListener(this);
        newContactButton.addActionListener(this);
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setVisible(true);
        contactBox.addActionListener(this);
    }

    void createContactsFolder() {
        File dir = new File("Contacts/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File contacts = new File(dir, "myContacts.txt");
        if (!contacts.exists()) {
            try {
                contacts.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(StartClientWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void createContactBox() {
        try {
            contactBox = new JComboBox(createContactList("myContacts.txt"));
            ListCellRenderer renderer = new ContactCellRenderer();
            contactBox.setRenderer(renderer);
            contactPicked = (Contact) contactBox.getSelectedItem();
        } catch (IOException ex) {
            Logger.getLogger(StartClientWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //  String[] createContactFolderList() {

    // }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JComboBox) {
            JComboBox cb = (JComboBox) ae.getSource();
            contactPicked = (Contact) cb.getSelectedItem();

            //this.pack();
        } else if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Add new contact")) {
                NewContactWindow newContact = new NewContactWindow();
                newContact.setVisible(true);
                this.dispose();
            } else if (((JButton) (ae.getSource())).getText().equals("Connect")) {
                if (contactPicked != null) {
                    String name = nameTextField.getText();
                    String IP = contactPicked.getIP();
                    int port = contactPicked.getPort();
                    
                    Client newClient = new Client(new Contact (name,IP, port));
                }

            } else if (((JButton) (ae.getSource())).getText().equals("Choose contact folder")) {
                File dir = new File("Contacts/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                refreshComboBox();

            }
             else if (((JButton) (ae.getSource())).getText().equals("Back")) {
                     MainWindow newMainWindow = new MainWindow();
                     dispose();
                 }
        }
    }

    private void refreshComboBox() {
        JFileChooser contactFolderChooser = new JFileChooser("Contacts/");
        int returnVal = contactFolderChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = contactFolderChooser.getSelectedFile();
            String fileName = selectedFile.getName();
            System.out.println(fileName);
            try {
                Contact[] myContacts = createContactList(fileName);
                contactBox.removeAllItems();
                for (Contact aContact : myContacts) {
                    contactBox.addItem(aContact);
                }
            } catch (IOException ex) {
                Logger.getLogger(StartClientWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

            revalidate();
            repaint();

        }

    }

    Contact[] createContactList(String fileName) throws IOException {
        File file = new File("Contacts/", fileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        ArrayList<String> lines = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
            lines.add(stringBuffer.toString());
            stringBuffer.setLength(0);
        }
        ArrayList<String> NAMES = new ArrayList<>();
        ArrayList<String> IP_ADDRESSES = new ArrayList<>();
        ArrayList<Integer> PORTS = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String string = lines.get(i);
            String[] splitString = string.split("\\s+");
            NAMES.add(splitString[0]);
            IP_ADDRESSES.add(splitString[1]);
            PORTS.add(Integer.parseInt(splitString[2]));
        }
        Contact[] contactList = new Contact[lines.size()];
        int i = 0;
        while (i < contactList.length) {
            contactList[i] = new Contact(NAMES.get(i), IP_ADDRESSES.get(i), PORTS.get(i));
            i++;
        }
        return contactList;
    }

}