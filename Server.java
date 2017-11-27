
package mychat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

class Server implements ObjectStreamListener {

    ArrayList<ObjectOutputStream> outputStreamArray = new ArrayList<>();
    ArrayList<ObjectStreamManager> streamManagerArray = new ArrayList<>();
    ArrayList<Socket> socketArray = new ArrayList<>();

    Server(int port) throws IOException {
        PingThread pingServer = new PingThread(port, this);
        pingServer.start();
        
    }

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        System.out.println("We are in server OR");
        if (exception == null) {
            System.out.println("we have an object");
            String message = (String) object;
            for (ObjectOutputStream OS : outputStreamArray) {
                try {
                    OS.writeObject(message);
                    OS.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }

}
