/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author olda4871
 */
class ChatParticipant extends JFrame implements ActionListener, ObjectStreamListener {
    
    private final String name;
    //private final JFrame chatFrame;
    private JPanel textAreaPanel;
    private JPanel northPanel;
    private JScrollPane scrollPaneTextArea;
    private JTextField textField;
    private JTextArea textArea;
    private JButton sendButton;
    private JButton closeButton;
    private final Socket mySocket;
    private OutputStream myOutput;
    private ObjectOutputStream objectOutput;
    private ObjectInputStream objectInput;
    private ObjectStreamManager myManager;
    
    private JPanel mainPanel;
    private JPanel southPanel;
    private JPanel eastPanel;
    private JTextArea contactsArea;
    private JScrollPane scrollPaneContactsArea;
    
    private final Contact thisContact;

    ChatParticipant(Socket socket, Contact myContact) throws IOException {
        thisContact = myContact;
        name = myContact.getName();
        /*chatFrame = new JFrame();
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setResizable(false); */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,300);
        setResizable(false);
        setVisible(true);
        createMisc();
        createPanels();
        addContentToPanels();
        //addPanelsToFrame();
        /*chatFrame.setVisible(true);*/
        pack();
        textField.addActionListener(this);
        sendButton.addActionListener(this);
        closeButton.addActionListener(this);
        mySocket = socket;
        myOutput = mySocket.getOutputStream();
        objectOutput = new ObjectOutputStream(myOutput);
        InputStream myInput = mySocket.getInputStream();
        objectInput = new ObjectInputStream(myInput);
        myManager = new ObjectStreamManager((int) (Math.random() * 100), objectInput, this);
        
        objectOutput.writeObject(thisContact);
    }

    void createMisc() {
        int field_width = 30;
        int rows = 10;
        int columns = 30;
        textField = new JTextField(field_width);
        sendButton = new JButton("Send");
        closeButton = new JButton("Close");
        textArea = new JTextArea(rows, columns);
        textArea.setLineWrap (true);
        textArea.setEditable(false);
        contactsArea = new JTextArea(rows, 10);
        contactsArea.setEditable(false);
        scrollPaneContactsArea= new JScrollPane(contactsArea);
        scrollPaneContactsArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneTextArea = new JScrollPane(textArea);
        scrollPaneTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    void addContentToPanels(){
        //mainPanel.add(textArea, BorderLayout.WEST);
        mainPanel.add(scrollPaneTextArea, BorderLayout.CENTER);
        //eastPanel.add(contactsArea,BorderLayout.CENTER);
        eastPanel.add(scrollPaneContactsArea, BorderLayout.EAST);
        southPanel.add(closeButton,BorderLayout.WEST);
        southPanel.add(textField, BorderLayout.CENTER);
        southPanel.add(sendButton, BorderLayout.EAST);
    }

    void addPanelsToFrame() {
        //add(mainPanel, BorderLayout.NORTH);
        //add(southPanel, BorderLayout.SOUTH);
        /*chatFrame.add(textField, BorderLayout.CENTER);
        chatFrame.add(sendButton, BorderLayout.EAST);
        chatFrame.add(closeButton, BorderLayout.WEST);
        chatFrame.add(northPanel, BorderLayout.NORTH); */
    }

    void createPanels() {
        mainPanel = new JPanel(new BorderLayout());
        southPanel = new JPanel(new BorderLayout());
        eastPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.WEST);
        add(eastPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
        /*textAreaPanel = new JPanel();
        northPanel = new JPanel();
        northPanel.add(textAreaPanel, BorderLayout.CENTER);
        northPanel.add(scrollPane, BorderLayout.EAST); */
    }

    public void send() throws IOException, ClassNotFoundException {
        try {
            //System.out.println("vi är i CLIENT send");
            String message = name + " : " + textField.getText();
            textField.setText("");
            objectOutput.writeObject(message);
            //System.out.println("vi är i CLIENT send efter objectOutput.write");
            objectOutput.flush();
            //display(message);
        } catch (IOException e) {
            //System.out.println("IOException in send");
        }
    }

    public void display(String message) throws IOException, ClassNotFoundException {
        //System.out.println("vi är i CLIENT display");
        textArea.append(message + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Close")) {
                try {
                    //thisContact.active = false;
                    objectOutput.writeObject(thisContact);
                    objectOutput.flush();
                    mySocket.close();
                    dispose();
                    //chatFrame.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    //System.out.println("vi är i CLIENT actionPerformed");
                    send();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (ae.getSource() instanceof JTextField) {
            try {
                //System.out.println("vi är i CLIENT actionPerformed");
                send();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        if (exception == null) {
            try {
                if (object instanceof String) {
                    //System.out.println("vi är i (CLIENT) objectRecieved");
                    String message = (String) object;
                    display(message);
                } else if (object != null && object instanceof Integer) {
                    CloseDialog closeDialog = new CloseDialog();
                    closeDialog.setVisible(true);
                    mySocket.close();
                    dispose();
                    //chatFrame.dispose();
                }
                else if (object instanceof ArrayList) {
                    contactsArea.setText("");
                    ArrayList<Contact> contactArray = new ArrayList((ArrayList<Contact>) object);
                    for (Contact contact : contactArray) {
                        contactsArea.append(contact.getName() + "\n"); 
                    }
                    //System.out.println("Sys print " + contactsArea.getText());
                }
            } catch (IOException ex) {
                Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
