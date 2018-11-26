
package mychat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

// PingThread är en viktig klass som hanterar en tråd som agerar som 'lyssnare'

// syftet är att - då du skapar en server - så kommer den servern att lyssna efter anslutningar
// detta avlyssnandet görs i den här klassen, vilket görs på en separat tråd, så att programmet aldrig fryser
// se ex.vis run-metoden som hanterar alla inkommande anslutningar
// - se klassen Server för hur själva servern fungerar med alla arrays
class PingThread extends Thread {

    private ServerSocket myServerSocket;
    private final Server myServer;

    PingThread(int port, Server server) {
        try {
            myServerSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(PingThread.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Try another port");
        }
        myServer = server;
    }

    void closeServer() throws IOException {
        stop();
        myServerSocket.close();
    }

    // då denna metod ska lyssna efter anslutningar så ligger den här run()-metoden och provar att acceptera nya anslutningar
    // om en anslutning sker så accepterar serverSocket detta och skapar en Socket för den anslutningen
    // en ny outputStream skapas för denna Socket
    // Denna outputStream läggs till i arraylisten som hanterar outputStreams i Server
    // det skapas också en ny StreamManager för denna Socket som hanterar alla inkomna objekt från denna anslutning
    // denna StreamManager läggs till i arraylisten som hanterar StreamManagers i Server
    // allt som läggs till här hanteras alltså bara EN gång i en här klassen, och alltså endast en gång på den här tråden
    // själva uppkopplingen hanteras alltså sedan i Servern! och den här tråden fortsätter lyssna efter inkommande anslutningar
    
    @Override
    public void run() {
        while (true) {
            try {
                Socket mySocket = myServerSocket.accept();
                myServer.connections++;
                ObjectOutputStream newOS = new ObjectOutputStream(mySocket.getOutputStream());
                newOS.writeObject(myServer.welcomeMessage);
                myServer.OUTPUT_STREAM_ARRAY_LIST.add(newOS);
                ObjectInputStream OBJECT_INPUT_STREAM = new ObjectInputStream(mySocket.getInputStream());
                ObjectStreamManager OSM = new ObjectStreamManager((int) (Math.random() * 1000), OBJECT_INPUT_STREAM, myServer);
                myServer.STREAM_MANAGER_ARRAY_LIST.add(OSM);
            } catch (IOException ex) {
                Logger.getLogger(PingThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
