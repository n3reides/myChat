
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
