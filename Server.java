a/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;

// Comments by Olle <2018-11-01>

// Jämte ChatParticipant är detta nog den absolut viktigaste klassen
// Syftet med servern är att hantera alla inkommande meddelanden, anslutningar och så vidare
// Servern ska också skicka vidare alla dessa, i princip omedelbart, när det tas emot
// Detta görs genom olika ArrayLists som hanterar inputs(StreamManagers från Parrow), outputs(OutputStreams), Sockets och Contacts
// Notera att serverSocket körs på en separat tråd från resten av programmet via PingThread
// Detta betyder att en tråd ligger och lyssnar på alla inkommande anslutningar
// detta utan att påverka resten av programmet (i princip)

// när någon ansluter till serverSocketen kommer alltså de arraylists som ligger i Server att uppdateras automagiskt


class Server extends JPanel implements ObjectStreamListener, ActionListener, WindowListener {

    // När du skapar en server så definieras alla dessa variabler

    final ArrayList<ObjectOutputStream> OUTPUT_STREAM_ARRAY_LIST = new ArrayList<>();
    final ArrayList<ObjectStreamManager> STREAM_MANAGER_ARRAY_LIST = new ArrayList<>();
    private final ArrayList<Socket> SOCKET_ARRAY_LIST = new ArrayList<>();
    private final ArrayList<Contact> CONTACT_ARRAY_LIST = new ArrayList<>();

    
    int totalSeconds;
    int totalMinutes;
    int totalHours;
    int connections;
    
    private final JTextField WELCOME_MSG_FIELD;
    private final PingThread PING_SERVER_THREAD;
    private final JTextArea WELCOME_MSG_AREA;
    JFrame serverStatusWindow;
    String UPTIME;
    String welcomeMessage;
    JLabel uptimeLabel;
    JPanel serverPanel;
    JLabel connectionsLabel;

    Server(int port) throws IOException {
        PING_SERVER_THREAD = new PingThread(port, this); // See PingThread class!
        // Det här startar en ny tråd! Den tråden lyssnar på inkommande anslutningar via serverSocket
        // Det betyder i princip att main()-tråden fortfarande kan göra andra saker, som att ansluta till servern
        
        PING_SERVER_THREAD.start(); // Tråden behöver såklart startas också
    
        // Här nedan följer den grafiska koden för server-fönstret
        serverStatusWindow = new JFrame();
        connections = 0;
        totalSeconds = 0;
        totalMinutes = 0;
        totalHours = 0;
        UPTIME = "";
        welcomeMessage = "";
        serverStatusWindow.setLayout(new GridBagLayout());
        serverPanel = new JPanel();
        serverStatusWindow.add(serverPanel, new GridBagConstraints());
        serverPanel.setLayout(new GridLayout(0, 1));
        serverStatusWindow.setTitle("New server");
        serverStatusWindow.setVisible(true);
        serverStatusWindow.setSize(400, 300);
        serverStatusWindow.setResizable(false);
        WELCOME_MSG_FIELD = new JTextField(30);
        JButton closeServerButton = new JButton("Close server");
        JButton sendButton = new JButton("Add welcome message");
        uptimeLabel = new JLabel("Server uptime: " + UPTIME);
        connectionsLabel = new JLabel("Current number of connections: " + connections);
        WELCOME_MSG_AREA = new JTextArea(3, 20);
        WELCOME_MSG_AREA.setEditable(false);
        WELCOME_MSG_AREA.setLineWrap(true);
        JScrollPane messageAreaScroller = new JScrollPane(WELCOME_MSG_AREA);
        messageAreaScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        serverPanel.add(new JLabel("Your IP: " + findLocalIPAddress()));
        serverPanel.add(new JLabel("IF you want to enter your own chatroom on this device: "));
     
        // Du kan bara ansluta till ditt egna chattrum via din egna lokala IP-adress
        serverPanel.add(new JLabel(InetAddress.getByName("localhost").toString()));
        serverPanel.add(connectionsLabel);
        serverPanel.add(uptimeLabel);
        serverPanel.add(new JLabel("Enter a welcome message here:"));
        serverPanel.add(WELCOME_MSG_FIELD);
        serverPanel.add(new JLabel("The current welcome message is: "));
        serverPanel.add(messageAreaScroller);
        serverPanel.add(sendButton);
        serverPanel.add(closeServerButton);
        
        // ActionListeners
        closeServerButton.addActionListener(this);
        sendButton.addActionListener(this);
        serverStatusWindow.addWindowListener(this);
        
        // En timer för att se hur länge servern har varit aktiv
        Timer uptimeTimer = new Timer(1000, this);
        uptimeTimer.start();
    }

    // Den här metoden letar upp din IP-adress som den är från nätet (alltså inte den lokala!!)
    // Om du vill att andra personer ska kunna ansluta till din server så behövs denna adress
    // Notera att du fortfarande kommer behöva öppna den valda porten i din brandvägg för att detta ska fungera
    private String findLocalIPAddress() {
        String systemipaddress = "";
        try {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream())); // Läser av IP-adressen från denna sida
            systemipaddress = sc.readLine().trim();
        } catch (Exception e) {
            systemipaddress = "Cannot Execute Properly";
        }
        return systemipaddress;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Close server")) {
                try {
                    sendMessageToAllStreams("The server is now closing. bai");
                    PING_SERVER_THREAD.closeServer(); // Notera ordningen vi stänger ner allt när vi väljer att stänga servern
                    CONTACT_ARRAY_LIST.clear(); // Först tråden, cleara sedan contact-array, stäng sedan ner alla sockets och slutligen stäng fönstret
                    updateConnectedClients();
                    closeAllSockets(); 
                    serverStatusWindow.dispose(); 
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (((JButton) (ae.getSource())).getText().equals("Add welcome message")) {
                welcomeMessage = WELCOME_MSG_FIELD.getText();
                WELCOME_MSG_AREA.setText(welcomeMessage);
                WELCOME_MSG_FIELD.setText("");
            }

        } else if (ae.getSource() instanceof Timer) {
            totalSeconds++;
            if (totalSeconds >= 60) {
                totalMinutes++;
                totalSeconds = 0;
                if (totalMinutes >= 60) {
                    totalHours++;
                }
            }
            UPTIME = Integer.toString(totalHours) + " hours, " + Integer.toString(totalMinutes) + " minutes, " + Integer.toString(totalSeconds) + " seconds.";
            uptimeLabel.setText("Server uptime: " + UPTIME);
            connectionsLabel.setText("Current number of connections: " + connections);
        }
    }

    // En metod för att stänga ner alla sockets i Socket arraylist
    private void closeAllSockets() {
        for (Socket socket : SOCKET_ARRAY_LIST) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (ObjectStreamManager OSM : STREAM_MANAGER_ARRAY_LIST) {
            OSM.closeManager();
        }
    }

    // Den här metoden går ut på att undersöka huruvida en client är eller inte är i listan av clients
    // Kan verka onödigt, men handlar om att hålla koll på vilken tid av överföring det handlar om
    // Ex.vis, om du väljer att avbryta uppkopplingen till servern så kommer din contact att skickas till servern
    // Detta för att servern kan hålla koll på vilka kontakter som är anslutna
    // Likaså skickas din contact till servern när du väljer att ansluta till den

    private boolean isClientInContactArray(Contact client) {
        boolean inArray = false;
        for (int i = 0; i < CONTACT_ARRAY_LIST.size(); i++) {
            if (client.equals(CONTACT_ARRAY_LIST.get(i))) {
                // Om clienten redan finns i contact array list,
                // så betyder det att den nu har valt att avbryta anslutningen
                // det är den enda anledningen till varför samma contact skulle skickas till servern igen
                // alltså tar vi bort dem från contact array listen och från output stream array listen
                CONTACT_ARRAY_LIST.remove(i); 
                OUTPUT_STREAM_ARRAY_LIST.remove(i); 
                connections--;
                inArray = !inArray;
                break;
            }
        }
        return inArray;
    }

    // Likaså, om en client inte finns i contact array list, så betyder ovanstående boolean att det anslutit en ny client
    // Då körs den här metoden för att lägga till clienten i arraylisten av contacts
   
    private void updateConnectedClients() {
        for (ObjectOutputStream OS : OUTPUT_STREAM_ARRAY_LIST) { // för varje outputstream i outputstream-arraylisten
            try {
                OS.writeObject(CONTACT_ARRAY_LIST.clone()); //skriver contact-arraylisten till denna outputstream
                OS.flush(); // uppdaterar denna outputstream (så att den nya contacten tar emot allt från outputstreamen
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // När det händer att ett meddelande kommer via lyssnaren (se objectRecieved())
    // Den här metoden kommer försöka skicka det meddelandet till varje outputstream
    // med andra ord, skicka det inkomna meddelandet till alla anslutna clients
    
    private void sendMessageToAllStreams(String message) {
        for (ObjectOutputStream OS : OUTPUT_STREAM_ARRAY_LIST) {
            try {
                OS.writeObject(message); // det meddelande som tas emot skickas alltså tillbaka (till alla! inklusive den som skickade det)
                OS.flush(); // uppdaterar outputstreamen efter att meddelandet skickas
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    // När någon ansluter till servern skickar denna metod ett meddelande till alla!
    // se objectRecieved() för detaljer om hur vi vet att en anslutning sker
    
    private void sendEnterMessageToStreams(Contact newContact) {
        for (int i = 0; i < CONTACT_ARRAY_LIST.size(); i++) {
            if (newContact != CONTACT_ARRAY_LIST.get(i)) {
                try {
                    OUTPUT_STREAM_ARRAY_LIST.get(i).writeObject(newContact.getName() + " has entered the chatroom.");
                    // getName från den nya kontacten och för varje ansluten outputstream, skicka detta meddelande
                    OUTPUT_STREAM_ARRAY_LIST.get(i).flush(); // uppdatera även outputstreamen
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // Det här här vi lyssnar efter inkommande meddelande
    // syftet med lyssnarmetoden är att avgöra huruvida det är en ny connection eller ett inkommande meddelande
    // eller mer specifikt, vilken typ av Object som har mottagits, om det är en String eller en Contact

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        if (exception == null) {
            // Exception == null betyder att vi har mottagit någonting snarare än ingenting
            if (object instanceof Contact) {
                // OM objektet är av Contact-typen, så har vi ANTINGEN en ny contact som anslutit eller en gammal contact som lämnat servern
                Contact client = (Contact) object; // vi castar objectet från object till Contact eftersom vi vet att det är en contact
                // notera att det därmed inte heller kommer att casta en exception eftersom objectet INTE är en NULL
                if (!isClientInContactArray(client)) {
                    // om det INTE är en gammal client, skicka ett anslutningsmeddelande
                    sendEnterMessageToStreams(client);
                    CONTACT_ARRAY_LIST.add(client); // lägg också till den nya clienten till contact_array_list
                } else {
                    // om den redan är i contact_array_list så betyder det att de har lämnat rummet :(
                    String leaveMessage = client.getName() + " has left the chat room.";
                    sendMessageToAllStreams(leaveMessage);
                }
                updateConnectedClients(); // uppdatera nuvarande anslutna clienter
            } else if (object instanceof String) {
                // om det inkomna objectet är en string
                String message = (String) object; // casta till string
                sendMessageToAllStreams(message); // skicka det vidare
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    // Note the order of each command here
    @Override
    public void windowClosing(WindowEvent we) {
        try {
            sendMessageToAllStreams("The server is now closing. bai");
            CONTACT_ARRAY_LIST.clear(); // Clear the contact array list
            updateConnectedClients(); // Update the connection list
            closeAllSockets(); // Close the sockets!
            PING_SERVER_THREAD.closeServer(); // Close the server thread!
            serverStatusWindow.dispose(); // Then dispose the window
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void windowClosed(WindowEvent we) {
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
    
}
