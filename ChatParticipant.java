
package mychat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.*;



class ChatParticipant extends JPanel implements ActionListener, ObjectStreamListener {
    
    private final String name;
    private final JFrame chatFrame;
    private JPanel textAreaPanel;
    private JPanel northPanel;
    private JScrollPane scrollPane;
    private JTextField textField;
    private JTextArea textArea;
    private JButton sendButton;
    private JButton closeButton;
    private final Socket mySocket;
    private OutputStream myOutput;
    private ObjectOutputStream objectOutput;
    private ObjectInputStream objectInput;
    private ObjectStreamManager myManager;

    ChatParticipant(Socket socket, String NAME) throws IOException {
        name = NAME;
        chatFrame = new JFrame();
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setResizable(false);
        createMisc();
        createPanels();
        addPanelsToFrame();
        chatFrame.setVisible(true);
        chatFrame.pack();
        textField.addActionListener(this);
        sendButton.addActionListener(this);
        closeButton.addActionListener(this);
        
        mySocket = socket;
        myOutput = mySocket.getOutputStream();
        objectOutput = new ObjectOutputStream(myOutput);
        InputStream myInput = mySocket.getInputStream();
        objectInput = new ObjectInputStream(myInput);
        myManager = new ObjectStreamManager((int) (Math.random() * 100), objectInput, this);
    }

    void createMisc() {
        int field_width = 30;
        int rows = 10;
        int columns = 30;
        textField = new JTextField(field_width);
        sendButton = new JButton("Send");
        closeButton = new JButton("Close");
        textArea = new JTextArea(rows, columns);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
    }

    void addPanelsToFrame() {
        chatFrame.add(textField, BorderLayout.CENTER);
        chatFrame.add(sendButton, BorderLayout.EAST);
        chatFrame.add(closeButton, BorderLayout.WEST);
        chatFrame.add(northPanel, BorderLayout.NORTH);
    }

    void createPanels() {
        textAreaPanel = new JPanel();
        northPanel = new JPanel();
        northPanel.add(textAreaPanel, BorderLayout.CENTER);
        northPanel.add(scrollPane, BorderLayout.EAST);
    }

    public void send() throws IOException, ClassNotFoundException {
        try {
            System.out.println("vi är i CLIENT send");
            String message = name + " säger: " + textField.getText();
            textField.setText("");
            objectOutput.writeObject(message);
            System.out.println("vi är i CLIENT send efter objectOutput.write");
            objectOutput.flush();
            display(message);
        } catch (IOException e) {
            System.out.println("IOException in send");
        }
    }

    public void display(String message) throws IOException, ClassNotFoundException {
        System.out.println("vi är i CLIENT display");
        textArea.append(message + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Close")) {
                try {
                    objectOutput.writeObject(2);
                    objectOutput.flush();
                    mySocket.close();
                    chatFrame.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    System.out.println("vi är i CLIENT actionPerformed");
                    send();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (ae.getSource() instanceof JTextField) {
            try {
                System.out.println("vi är i CLIENT actionPerformed");
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
                if (object != null && object instanceof String) {
                    System.out.println("vi är i (CLIENT) objectRecieved");
                    String message = (String) object;
                    display(message);
                } else if (object != null && object instanceof Integer) {
                    CloseDialog closeDialog = new CloseDialog();
                    closeDialog.setVisible(true);
                    mySocket.close();
                    chatFrame.dispose();
                }
            } catch (IOException ex) {
                Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
