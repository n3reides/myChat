package mychat;

import com.dosse.upnp.UPnP;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PingThread extends Thread {
    private ServerSocket myServerSocket;
    private final Server myServer;
    
    PingThread(int port, Server server){
        try {
            myServerSocket = new ServerSocket(port);
            UPnP.openPortTCP(port);
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
    public void run(){
        while(true){
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