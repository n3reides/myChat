/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olda4871
 */
public class PingThread extends Thread {
    ServerSocket myServerSocket;
    Server myServer;
    
    PingThread(int port, Server server){
        try {
            myServerSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(PingThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        myServer = server;
        
    }
    
    @Override
    public void run(){
        while(true){
            try {
                Socket mySocket = myServerSocket.accept();
             //   System.out.println("new socket opened: " + mySocket);
                myServer.outputStreamArray.add(new ObjectOutputStream(mySocket.getOutputStream()));
                ObjectInputStream OBJECT_INPUT_STREAM = new ObjectInputStream(mySocket.getInputStream());
                ObjectStreamManager OSM = new ObjectStreamManager((int) (Math.random() * 1000), OBJECT_INPUT_STREAM, myServer);
              //  System.out.println(OSM);
                myServer.streamManagerArray.add(OSM);
            } catch (IOException ex) {
                Logger.getLogger(PingThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
    }
    
}
