package mychat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
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

class Server extends JPanel implements ObjectStreamListener, ActionListener {
    
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
        PING_SERVER_THREAD = new PingThread(port, this);
        PING_SERVER_THREAD.start();
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
        
        serverPanel.add(new JLabel("Your IP: " + InetAddress.getLocalHost().getHostAddress()));
        serverPanel.add(connectionsLabel);
        serverPanel.add(uptimeLabel);
        serverPanel.add(new JLabel("Enter a welcome message here:"));
        serverPanel.add(WELCOME_MSG_FIELD);
        serverPanel.add(new JLabel("The current welcome message is: "));
        serverPanel.add(messageAreaScroller);
        serverPanel.add(sendButton);
        serverPanel.add(closeServerButton);
        closeServerButton.addActionListener(this);
        sendButton.addActionListener(this);

        Timer uptimeTimer = new Timer(1000, this);
        uptimeTimer.start();
    }

    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JButton) {
            if (((JButton) (ae.getSource())).getText().equals("Close server")) {
                try {
                    sendMessageToAllStreams("The server is now closing. bai");
                    PING_SERVER_THREAD.closeServer();
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
    }
    
    
        private boolean isClientInContactArray(Contact client) {
        boolean inArray = false;
        for (int i = 0; i < CONTACT_ARRAY_LIST.size(); i++) {
            if (client.equals(CONTACT_ARRAY_LIST.get(i))) {
                CONTACT_ARRAY_LIST.remove(i);
                OUTPUT_STREAM_ARRAY_LIST.remove(i);
                connections--;
                inArray = !inArray;
                break;
            }

        }
        return inArray;
    }

    private void uppdateConnectedClients() {
        for (ObjectOutputStream OS : OUTPUT_STREAM_ARRAY_LIST) {
            try {
                OS.writeObject(CONTACT_ARRAY_LIST.clone());
                OS.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sendMessageToAllStreams(String message) {
        for (ObjectOutputStream OS : OUTPUT_STREAM_ARRAY_LIST) {
            try {
                OS.writeObject(message);
                OS.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void sendEnterMessageToStreams(Contact newContact){
        for(int i = 0; i<CONTACT_ARRAY_LIST.size(); i++){
            if(newContact != CONTACT_ARRAY_LIST.get(i)){
                try {
                OUTPUT_STREAM_ARRAY_LIST.get(i).writeObject(newContact.getName() + " has entered the chatroom.");
                OUTPUT_STREAM_ARRAY_LIST.get(i).flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            }
        }
        
    }

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        if (exception == null) {
            if (object instanceof Contact) {
                Contact client = (Contact) object;
                if (!isClientInContactArray(client)) {
                    sendEnterMessageToStreams(client);
                    CONTACT_ARRAY_LIST.add(client);
                    
                } else {
                    String leaveMessage = client.getName() + " has left the chat room.";
                    sendMessageToAllStreams(leaveMessage);
                }
                uppdateConnectedClients();

            } else if (object instanceof String) {
                String message = (String) object;
                sendMessageToAllStreams(message);

            }
        }

    }
}