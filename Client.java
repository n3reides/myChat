
package mychat;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

// en client skapas och då körs ChatParticipant med vår socket och vår contact som inparametrar
// värt att notera, helt enkelt, att detta utgör själva basen för hur du ansluter till en Server
// om servern finns så kommer serverSocket ta emot din Socket och skapa en anslutning
// också värt att notera att själva anslutningen inte räcket, utan du behöver fortfarande en inputStream och en outputStream
// ChatParticipant öppnar ett nytt chattfönster!

class Client {
    
    private Socket mySocket;
    private ChatParticipant client;

    Client(Contact myContact) {
        try {
            mySocket = new Socket(myContact.getIP(), myContact.getPort());
            client = new ChatParticipant(mySocket, myContact);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
