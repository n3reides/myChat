
package mychat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

// Den här klassen nås när vi väljer Start Client
// Syftet med klassen är att du ska kunna välja ett 'chatroom' och sedan ansluta till det, om det ligger öppet
// Att lägga till ett nytt chatroom görs inte här, utan det görs i NewContactWindow
// En stor del av konstruktorn kan ignoreras, det är bara grafisk kod

// Det mest väsentliga är att det är från den här klassen som vi skapar en Client
// Se ex.vis startNewClient()-metoden för att se hur det går till

// Alla chatrooms ligger sparade i en .txt-fil som heter MyContacts.txt
// De flesta metoder i den här klassen hanterar meny-systemet som har med det att göra
// exempelvis, createContactList() har ingen 'nödvändig' funktion för själva labben

final class StartClientWindow extends JFrame implements ActionListener {

    private JComboBox contactBox;
    private Contact contactPicked;
    private final JTextField NAME_FIELD;

    StartClientWindow() throws IOException {
        setTitle("Start Window");
        setSize(new Dimension(400, 300));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel northPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 1));
        JPanel southPanel = new JPanel();
        createContactsFolder();
        createContactBox();
        JButton newContactButton = new JButton("Add new chatroom");
        JButton backButton = new JButton("Back");
        NAME_FIELD = new JTextField();
        NAME_FIELD.addActionListener(this);
        JButton chooseFolderButton = new JButton("Choose chatroom folder");
        northPanel.add(new JLabel("Pick your chatroom folder"), BorderLayout.NORTH);
        northPanel.add(chooseFolderButton, BorderLayout.SOUTH);
        southPanel.add(newContactButton);
        southPanel.add(backButton, BorderLayout.EAST);
        centerPanel.add(new JLabel("Choose chatroom"));
        centerPanel.add(contactBox);
        centerPanel.add(new JLabel("Write your username"));
        centerPanel.add(NAME_FIELD);
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(this);
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

    
    private void startNewClient() {
        if (contactPicked != null) {
            if (NAME_FIELD.getText().length() > 0) {
                String name = NAME_FIELD.getText();
                String IP = contactPicked.getIP();
                int port = contactPicked.getPort();
                Client newClient = new Client(new Contact(name, IP, port));
                NAME_FIELD.setText("");
            } else { // om användaren ej väljer ett eget namn
                NAME_FIELD.setText("IAmToStupidToEnterAUsername"); 
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JComboBox) {
            JComboBox cb = (JComboBox) ae.getSource();
            contactPicked = (Contact) cb.getSelectedItem();
        } else if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Add new chatroom")) {
                NewContactWindow newContact = new NewContactWindow();
                newContact.setVisible(true);
                this.dispose();
                
                
            // Trycker du på connect så startas en ny client! se metoden ovan
            } else if (((JButton) (ae.getSource())).getText().equals("Connect")) {
                startNewClient();
                
               
            } else if (((JButton) (ae.getSource())).getText().equals("Choose chatroom folder")) {
                File dir = new File("Contacts/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                refreshComboBox();
            } else if (((JButton) (ae.getSource())).getText().equals("Back")) {
                MainWindow newMainWindow = new MainWindow();
                dispose();
            }
        } else if (ae.getSource() instanceof JTextField) {
            startNewClient();
        }
    }

    
    // metoderna här nedan hanterar själva väljandet av chatroom
    // inte lika viktigt för hjälplärare att kunna
    
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
        }
    }

//
    
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
