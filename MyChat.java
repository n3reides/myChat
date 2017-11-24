package mychat;

import java.io.*;
import java.net.*;


public class MyChat {
    
    //Olle Dahlstedt & Max Sonebäck
    // Uppgift 3; 2017-11-24

    private static final int PORT = 8191; // 2^13 - 1, PRIME

    public static void main(String[] args) throws IOException {
        boolean SERVER_STARTED = false;       
        if (!SERVER_STARTED) {
            Server myServer = new Server(PORT);
            SERVER_STARTED = !SERVER_STARTED;
        } else {
            String localhost = InetAddress.getLocalHost().getHostAddress();
            Client myClient = new Client(localhost, PORT); 
       // NewContact newContact = new NewContact();
       // newContact.setVisible(true);
        }
    }
}







