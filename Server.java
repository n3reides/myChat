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

// THIS class will open a server and thus is rather complex
// Called upon from the StartServerWindow class which is a frame
// The port entered in text field of the StartServerWindow(default 8191) is the only argument to the constructor

class Server extends JPanel implements ObjectStreamListener, ActionListener, WindowListener {
    
    // Manages many things, including a outputstream array that deals with sending messages
    // It also incorporates an array of object stream managers, listening for incoming connections and messages
    // Srrays of sockets and contacts are managed in the server class
    // Plus some visual interface, which is not so complex
    
    final ArrayList<ObjectOutputStream> OUTPUT_STREAM_ARRAY_LIST = new ArrayList<>();
    final ArrayList<ObjectStreamManager> STREAM_MANAGER_ARRAY_LIST = new ArrayList<>();
    private final ArrayList<Socket> SOCKET_ARRAY_LIST = new ArrayList<>();
    private final ArrayList<Contact> CONTACT_ARRAY_LIST = new ArrayList<>();
    private final JTextField WELCOME_MSG_FIELD;
    private final PingThread PING_SERVER_THREAD;
    private final JTextArea WELCOME_MSG_AREA;
    int totalSeconds, totalMinutes, totalHours, connections;
    JFrame serverStatusWindow;
    String UPTIME, welcomeMessage;
    JLabel uptimeLabel;
    JPanel serverPanel;
    JLabel connectionsLabel;

    Server(int port) throws IOException {
        PING_SERVER_THREAD = new PingThread(port, this); // See PingThread class!
        // This will create a new thread that listens to incoming streams
        // This means you can use the main thread to work with the rest of the program
        PING_SERVER_THREAD.start(); // A thread needs to be started!
        
        // Below is some graphical code, zzzz
        serverStatusWindow = new JFrame();
        connections = 0;
        totalSeconds = 0; totalMinutes = 0; totalHours = 0;
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
        // You can only connect to your own chatroom(on the same device) by using your local IP address
        serverPanel.add(new JLabel(InetAddress.getByName("localhost").toString()));
        serverPanel.add(connectionsLabel);
        serverPanel.add(uptimeLabel);
        serverPanel.add(new JLabel("Enter a welcome message here:"));
        serverPanel.add(WELCOME_MSG_FIELD);
        serverPanel.add(new JLabel("The current welcome message is: "));
        serverPanel.add(messageAreaScroller);
        serverPanel.add(sendButton);
        serverPanel.add(closeServerButton);
        
        // Of course, we are listening to some buttons too
        closeServerButton.addActionListener(this);
        sendButton.addActionListener(this);
        serverStatusWindow.addWindowListener(this);
        
        // Timer will be updated for each second that server is up
        Timer uptimeTimer = new Timer(1000, this);
        uptimeTimer.start();
    }

    
    // This method looks for your ONLINE ip address
    // If you want other people to connect to server, you NEED this IP
    
    // START OF FINDLOCALIPADDRESS
    private String findLocalIPAddress() {
        String systemipaddress = "";
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
            BufferedReader sc =
            new BufferedReader(new InputStreamReader(url_name.openStream())); // Just so we actually get the proper IP and not something else
            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e)
        {
            systemipaddress = "Cannot Execute Properly";
        }
        return systemipaddress;
    // END OF FINDLOCALIPADDRESS 
    }
    
    

    // Defines a few features of the server window
    
    @Override  // Still no idea if this is necessary???
    
    // START OF ACTIONPERFORMED
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Close server")) {
                try {
                    sendMessageToAllStreams("The server is now closing. bai");
                    PING_SERVER_THREAD.closeServer(); // Remember to close thread when you are closing the server
                    CONTACT_ARRAY_LIST.clear(); // Clears the array of contacts and updates this
                    updateConnectedClients();
                    closeAllSockets(); // Really important to close all sockets, so no more traffic can be sent over this port
                    serverStatusWindow.dispose(); // THEN we clear the status window :))
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

                // just edits your welcome message and sends it to the chatroom
            } else if (((JButton) (ae.getSource())).getText().equals("Add welcome message")) {
                welcomeMessage = WELCOME_MSG_FIELD.getText();
                WELCOME_MSG_AREA.setText(welcomeMessage);
                WELCOME_MSG_FIELD.setText("");
            }
            // will be updated every second the server is up, is just a clock
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
        // END OF ACTIONPERFORMED
        }
    }

    // A method for closing all sockets between the server and connected clients!
    
    // START OF CLOSEALLSOCKETS
    private void closeAllSockets() {
        for (Socket socket: SOCKET_ARRAY_LIST) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (ObjectStreamManager OSM : STREAM_MANAGER_ARRAY_LIST) {
            OSM.closeManager();
        }
    // END OF CLOSEALLSOCKETS
    }
    
    // A method for checking whether or not a client is in the array of currently connected contacts
    // Why? Because we use this to refresh the number of currently connected clients
    // See objectRecieved method for the call
    
    // START OF ISCLIENTINCONTACTARRAY
        private boolean isClientInContactArray(Contact client) {
        boolean inArray = false;
        for (int i = 0; i < CONTACT_ARRAY_LIST.size(); i++) {
            if (client.equals(CONTACT_ARRAY_LIST.get(i))) {
                CONTACT_ARRAY_LIST.remove(i); // remove them from the contact array
                OUTPUT_STREAM_ARRAY_LIST.remove(i); // remove them from the output stream array
                connections--;
                inArray = !inArray;
                break;
            }
        }
        return inArray;
    // END OF ISCLIENTINCONTACTARRAY
    }

    // Whenever a client connects to the server, this method will update the output stream
    // This means the new client will also recieve all outputs from the stream
        
    // START OF UPDATECONNECTEDCLIENTS
    private void updateConnectedClients() {
        for (ObjectOutputStream OS : OUTPUT_STREAM_ARRAY_LIST) {
            try {
                OS.writeObject(CONTACT_ARRAY_LIST.clone()); //writes the contact array to the output stream
                OS.flush(); //refreshes the output stream
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    //END OF UPDATECONNECTEDCLIENTS
    }

    // Whenever a message is recieved in the listener(see below)
    // This method will try to send the message to the output stream
    // In other words, to all connected clients
    
    // START OF SENDMESSAGETOALLSTREAMS
    private void sendMessageToAllStreams(String message) {
        for (ObjectOutputStream OS : OUTPUT_STREAM_ARRAY_LIST) {
            try {
                OS.writeObject(message); // Whatever message is recieved will be SENT BACK
                OS.flush(); // refreshes the output stream (AFTER sending the message)
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    // END OF SENDMESSAGETOALLSTREAMS
    }
    
    // Whenever someone connects to the server this method sends a message
    // See listener below
    
    // START OF SENDENTERMESSAGETOSTREAMS
    private void sendEnterMessageToStreams(Contact newContact){
        for(int i = 0; i<CONTACT_ARRAY_LIST.size(); i++){
            if(newContact != CONTACT_ARRAY_LIST.get(i)){
                try {
                OUTPUT_STREAM_ARRAY_LIST.get(i).writeObject(newContact.getName() + " has entered the chatroom.");
                // getName from the new contact and for each connected stream in the output stream array, send a message
                OUTPUT_STREAM_ARRAY_LIST.get(i).flush(); //always flush your output streams
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }
    // END OF SENDENTERMESSAGETOSTREAMS
    }

    // This is our listener method
    // Checks if the object recieved is a contact or a message
    @Override
    
    // START OF OBJECTRECIEVED
    public void objectReceived(int number, Object object, Exception exception) {
        if (exception == null) {   // Exception == null means we have recieved something rather than nothing
            
            if (object instanceof Contact) { // If the object is contact type, we have a NEW contact entering or OLD contact leaving
                Contact client = (Contact) object; // object is cast from Object type to Contact type
                // note this will not cast an exception, because we KNOW the object is not a NULL type
                if (!isClientInContactArray(client)) { // if not a previous contact, send enter message
                    sendEnterMessageToStreams(client);
                    CONTACT_ARRAY_LIST.add(client); // also add to array of clients of course
                    
                } else { // if already in array, this means they left :((
                    String leaveMessage = client.getName() + " has left the chat room.";
                    sendMessageToAllStreams(leaveMessage);
                }
                updateConnectedClients(); // refresh the connected clients

            } else if (object instanceof String) { // houston, we have a message
                String message = (String) object;
                sendMessageToAllStreams(message); // send that message to everyone and their grandmother

            }
        }
    // END OF OBJECTRECIEVED
    }

    // only WindowClosing method is interesting here
    
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