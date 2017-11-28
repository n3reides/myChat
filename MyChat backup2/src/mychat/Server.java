package mychat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

    ArrayList<ObjectOutputStream> outputStreamArray = new ArrayList<>();
    ArrayList<ObjectStreamManager> streamManagerArray = new ArrayList<>();
    ArrayList<Socket> socketArray = new ArrayList<>();
    ArrayList<Contact> contactArray = new ArrayList<>();
    JButton closeServerButton;
    JButton sendButton;
    JFrame serverStatusWindow;
    JTextField welcomeMessageTextField;
    int totalSeconds, totalMinutes, totalHours, connections;
    String UPTIME, welcomeMessage;
    JLabel uptimeLabel;
    JPanel serverPanel;
    JLabel connectionsLabel;
    PingThread pingServer;
    JTextArea welcomeMessageArea;

    Server(int port) throws IOException {
        pingServer = new PingThread(port, this);
        pingServer.start();
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
        welcomeMessageTextField = new JTextField(30);
        closeServerButton = new JButton("Close server");
        sendButton = new JButton("Add welcome message");
        uptimeLabel = new JLabel("Server uptime: " + UPTIME);
        connectionsLabel = new JLabel("Current number of connections: " + connections);
        welcomeMessageArea = new JTextArea(3, 20);
        welcomeMessageArea.setEditable(false);
        welcomeMessageArea.setLineWrap(true);
        JScrollPane messageAreaScroller = new JScrollPane(welcomeMessageArea);
        messageAreaScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        serverPanel.add(connectionsLabel);
        serverPanel.add(uptimeLabel);
        serverPanel.add(new JLabel("Enter a welcome message here:"));
        serverPanel.add(welcomeMessageTextField);
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
                    pingServer.closeServer();
                    closeAllSockets();
                    serverStatusWindow.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (((JButton) (ae.getSource())).getText().equals("Add welcome message")) {
                welcomeMessage = welcomeMessageTextField.getText();
                
                welcomeMessageArea.setText(welcomeMessage);
                welcomeMessageTextField.setText("");
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
            //serverPanel.repaint();
        }
    }

    private void closeAllSockets() {
        for (Socket socket: socketArray) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
          
        }
        for (ObjectStreamManager OSM : streamManagerArray) {
            OSM.closeManager();
        }
    }
    
    
        private boolean isClientInContactArray(Contact client) {
        boolean inArray = false;
        for (int i = 0; i < contactArray.size(); i++) {
            if (client.equals(contactArray.get(i))) {
                contactArray.remove(i);
                outputStreamArray.remove(i);
                connections--;
                inArray = !inArray;
                break;
            }

        }
        return inArray;
    }

    private void uppdateConnectedClients() {
        for (ObjectOutputStream OS : outputStreamArray) {
            try {
                //   System.out.println(nameArray.toString());
                OS.writeObject(contactArray.clone());
                OS.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sendMessageToAllStreams(String message) {
        for (ObjectOutputStream OS : outputStreamArray) {
            try {
                OS.writeObject(message);
                OS.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        //System.out.println("We are in server OR");
        if (exception == null) {
            if (object instanceof Contact) {
                Contact client = (Contact) object;
                if (!isClientInContactArray(client)) {
                    contactArray.add(client);
                    
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