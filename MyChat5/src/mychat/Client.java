
package mychat;

import java.io.IOException;
import java.net.Socket;


class Client {
    
    Socket mySocket;
    ChatParticipant client;
    String name;

    Client(String aName, String localhost, int port) {
        try {
            name = aName;
            mySocket = new Socket(localhost, port);
            client = new ChatParticipant(mySocket, name);
        } catch (IOException e) {
            System.out.println("IOException in Client, could not connect");
        }
    }
    
}
