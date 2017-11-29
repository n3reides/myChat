
package mychat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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


class ChatParticipant extends JFrame implements ActionListener, ObjectStreamListener, WindowListener {

    private final Socket MY_SOCKET;
    private final Contact THIS_CONTACT;
    private final ObjectStreamManager MY_MANAGER;
    private final ObjectOutputStream OBJECT_OUTPUT;
    
    private JScrollPane scrollPaneTextArea, scrollPaneContactsArea;
    private JTextField textField;
    private JTextArea textArea, contactsArea;;
    private JButton sendButton, closeButton;
    private JPanel mainPanel, southPanel, eastPanel;

    ChatParticipant(Socket socket, Contact myContact) throws IOException {
        setTitle(myContact.getName());
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        createMisc();
        createPanels();
        addContentToPanels();
        pack();
        
        textField.addActionListener(this);
        sendButton.addActionListener(this);
        closeButton.addActionListener(this);
        addWindowListener(this);
        
        MY_SOCKET = socket;
        OBJECT_OUTPUT = new ObjectOutputStream(MY_SOCKET.getOutputStream());
        MY_MANAGER = new ObjectStreamManager((int) (Math.random() * 100), new ObjectInputStream(MY_SOCKET.getInputStream()), this);
        THIS_CONTACT = myContact;
        OBJECT_OUTPUT.writeObject(THIS_CONTACT);
    }

    final void createMisc() {
        int fieldWidth = 30;
        int rows = 20;
        int textAreaColumns = 40;
        int contactsAreaColumns = 15;
        textField = new JTextField(fieldWidth);
        sendButton = new JButton("Send");
        closeButton = new JButton("Close");
        textArea = new JTextArea(rows, textAreaColumns);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        contactsArea = new JTextArea(rows, contactsAreaColumns);
        contactsArea.setEditable(false);
    }

    final void addContentToPanels() {
        mainPanel.add(scrollPaneTextArea, BorderLayout.CENTER);
        eastPanel.add(scrollPaneContactsArea, BorderLayout.EAST);
        southPanel.add(closeButton, BorderLayout.WEST);
        southPanel.add(textField, BorderLayout.CENTER);
        southPanel.add(sendButton, BorderLayout.EAST);
    }

    final void createPanels() {
        mainPanel = new JPanel(new BorderLayout());
        southPanel = new JPanel(new BorderLayout());
        eastPanel = new JPanel(new BorderLayout());
        scrollPaneContactsArea = new JScrollPane(contactsArea);
        scrollPaneContactsArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneTextArea = new JScrollPane(textArea);
        scrollPaneTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(mainPanel, BorderLayout.WEST);
        add(eastPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void send() throws IOException, ClassNotFoundException {
        try {
            String message = THIS_CONTACT.getName() + " : " + textField.getText();
            textField.setText("");
            OBJECT_OUTPUT.writeObject(message);
            OBJECT_OUTPUT.flush();
        } catch (IOException e) {
        }
    }

    private void display(String message) throws IOException, ClassNotFoundException {
        textArea.append(message + "\n");
    }
    
    void closeChatWindow() throws IOException {
        MY_MANAGER.closeManager();
        OBJECT_OUTPUT.writeObject(THIS_CONTACT);
        OBJECT_OUTPUT.close();
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Close")) {
                try {
                    closeChatWindow();
                } catch (IOException ex) {
                    dispose();
                }
            } else {
                try {
                    send();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (ae.getSource() instanceof JTextField) {
            try {
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
                    String message = (String) object;
                    display(message);
                } else if (object != null && object instanceof Integer) {
                    MY_SOCKET.close();
                    dispose();
                } else if (object instanceof ArrayList) {
                    contactsArea.setText("");
                    ArrayList<Contact> contactArray = new ArrayList((ArrayList<Contact>) object);
                    for (Contact contact : contactArray) {
                        contactsArea.append(contact.getName() + "\n");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent we) {}

    @Override
    public void windowClosing(WindowEvent we) {
        try {
            closeChatWindow();
        } catch (IOException ex) {
            dispose();
        }
    }

    @Override
    public void windowIconified(WindowEvent we) {}

    @Override
    public void windowDeiconified(WindowEvent we) {}

    @Override
    public void windowActivated(WindowEvent we) {}

    @Override
    public void windowDeactivated(WindowEvent we) {}

    @Override
    public void windowClosed(WindowEvent we) {}

}