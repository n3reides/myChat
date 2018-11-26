
package mychat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

// ChatParticipant är vår chattruta
// en av de 3 viktigaste klasserna
// det är den här som körs när du (som client) ansluter till en server
// jag har försökt separera de 'oväsentliga' grafiska delarna
// de flesta kommentarer kommer att förtydliga vad som sker 'under ytan'

class ChatParticipant extends JFrame implements ActionListener, ObjectStreamListener, WindowListener {
    
    // en ChatParticipant behöver en Socket, en contact
    private final Socket MY_SOCKET;
    private final Contact THIS_CONTACT; // som namnet antyder, den Contact som ligger sparad här är den Contact du just nu chattar via
    // Alla andra contacts ligger sparade i Server
    private final ObjectStreamManager MY_MANAGER; // ObjectStreamManager hanterar INPUT streams, den ligger alltså och lyssnar på inkommande meddlanden
    // dessa inkommande meddelanden skickas från Servern
    // händelseförloppet går alltså till ungefär så här:
    // skriv ett meddelande och klick 'skicka meddelande' -> meddelandet skickas till Server via vår output stream -> meddelandet hanteras i Server och skickas tillbaka till alla ChatParticipants
    private final ObjectOutputStream OBJECT_OUTPUT; // skicka ett objekt över outputstreamen
    
    // vanliga grafiska delar
    private JScrollPane scrollPaneTextArea;
    private JScrollPane scrollPaneContactsArea;
    private JTextField textField;
    private JTextArea textArea;
    private JTextArea contactsArea;
    private JButton sendButton;
    private JButton closeButton;
    private JPanel mainPanel;
    private JPanel southPanel;
    private JPanel eastPanel;

    ChatParticipant(Socket socket, Contact myContact) throws IOException { // Vi tar alltså emot en socket och en Contact från client
        setTitle(myContact.getName());
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        createMisc();
        createPanels();
        addContentToPanels();
        pack();
        
        // interaktiva lyssnare
        textField.addActionListener(this);
        sendButton.addActionListener(this);
        closeButton.addActionListener(this);
        addWindowListener(this); 
        // windowListener gör att vi kan hantera vad som händer om du 'kryssar' fönstret istället för att klicka på Close
        
        // vår socket fås från Client
        MY_SOCKET = socket;
        // vi skapar en outputStream
        OBJECT_OUTPUT = new ObjectOutputStream(MY_SOCKET.getOutputStream());
        // vår streamManager är alltså vår lyssnare! det är genom den vi tar emot meddelanden
        MY_MANAGER = new ObjectStreamManager((int) (Math.random() * 100), new ObjectInputStream(MY_SOCKET.getInputStream()), this);
        
        // det här är du
        THIS_CONTACT = myContact;
        
        OBJECT_OUTPUT.writeObject(THIS_CONTACT); // det första du gör är att skicka din Contact över outputStream till Servern
    }
    
    // jag har lagt actionPerformed och objectRecieved ovanför metoderna de anropar - så håll koll på metodanropen och referera till dem
    @Override
    public void actionPerformed(ActionEvent ae) {
        // det finns tv olika knappar, antingen Close eller Send message
        if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Close")) {
                // har du tryckt på close? stäng fönstret och avsluta uppkopplingen
                try {
                    closeChatWindow();
                } catch (IOException ex) {
                    dispose();
                }
            } else {
                // annars har du tryckt på Send message, så anropa send()
                try {
                    send();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ChatParticipant.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (ae.getSource() instanceof JTextField) {
            //det här betyder att du tryckt på enter, så du anropar send()
            // att trycka på enter ingår alltså i JTextField
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
                if (object instanceof String) { //om du har tagit emot en string
                    String message = (String) object; // casta det mottagna objektet till en string
                    display(message); // anropa display så att meddelandet dyker upp i din chatfield
                    
                } else if (object != null && object instanceof Integer) {
                    // alltså, när vi stänger ner servern så skickar vi en int
                    // det är bara ett arbiträrt val, skulle lika gärna kunna varit en specifik string typ 'Closing'
                    MY_SOCKET.close();
                    dispose();
                    
                } else if (object instanceof ArrayList) {
                    // om nya anslutningar sker till servern så skickas en ArrayList från Servern
                    // Då uppdateras contactsArea där alla anslutna kontakter visas
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

    
    // Skicka ett meddelande, över vår outputStream, till Server
    private void send() throws IOException, ClassNotFoundException {
        try {
            String message = THIS_CONTACT.getName() + " : " + textField.getText(); 
            //ta texten från textField och skicka det med ditt namn
            textField.setText("");
            OBJECT_OUTPUT.writeObject(message);
            OBJECT_OUTPUT.flush();
        } catch (IOException e) {
        }
    }
    // om ett meddelande tas emot, lägg till det till textArea
    private void display(String message) throws IOException, ClassNotFoundException {
        textArea.append(message + "\n");
    }

    // hanterar vad som behövs göras när du stänger fönstret
    // det är viktigt vilken ordning detta görs i
    // du behöver stänga uppkopplingen mot Server innan du stänger tråden som kör fönstret
    void closeChatWindow() throws IOException {
        MY_MANAGER.closeManager();
        OBJECT_OUTPUT.writeObject(THIS_CONTACT);
        OBJECT_OUTPUT.close();
        dispose();
    }

    // grafisk kod
    // oviktig
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
    
    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    public void windowClosing(WindowEvent we) {
        try {
            closeChatWindow();
        } catch (IOException ex) {
            dispose();
        }
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }
    
}
