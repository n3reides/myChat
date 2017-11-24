package mychat;

import java.io.*;
import java.net.*;

class Client {
    
    Socket mySocket;
    ChatParticipant client;

    Client(String localhost, int port) {
        try {
            String name = "CLIENT";
            mySocket = new Socket(localhost, port);
            client = new ChatParticipant(mySocket, name);
        } catch (IOException e) {
            System.out.println("IOException in Client, could not connect");
        }
    }
    
}
