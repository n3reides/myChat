
package mychat;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


class Client {
    
    private Socket mySocket;
    private ChatParticipant client;
    
    Client(Contact myContact){
        try {
            mySocket = new Socket(myContact.getIP(), myContact.getPort());
            client = new ChatParticipant(mySocket, myContact);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}