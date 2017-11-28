
package mychat;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


class Client {
    
    Socket mySocket;
    ChatParticipant client;
    String name;

   /* Client(String aName, String localhost, int port) {
        try {
            name = aName;
            mySocket = new Socket(localhost, port);
            client = new ChatParticipant(mySocket, name);
        } catch (IOException e) {
            System.out.println("IOException in Client, could not connect");
        }
    } */
    
    Client(Contact myContact){
        try {
            mySocket = new Socket(myContact.getIP(), myContact.getPort());
            client = new ChatParticipant(mySocket, myContact);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
