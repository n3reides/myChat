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
    //ArrayList<String> nameArray = new ArrayList<>();
    ArrayList<Contact> contactArray = new ArrayList<>();

    Server(int port) throws IOException {
        PingThread pingServer = new PingThread(port, this);
        pingServer.start();

    }

    private boolean isClientInContactArray(Contact client) {
        boolean inArray = false;
        for (int i = 0; i < contactArray.size(); i++) {
            if (client.equals(contactArray.get(i))) {
                contactArray.remove(i);
                outputStreamArray.remove(i);
                inArray = !inArray;
                break;
            }

        }
        return inArray;
    }

    private void uppdateConnectedClients() {
        for (ObjectOutputStream OS : outputStreamArray) {
            try {
                //   System.out.println(nameArray.toString());
                OS.writeObject(contactArray.clone());
                OS.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        System.out.println("We are in server OR");
        if (exception == null) {
            if (object instanceof Contact) {
                Contact client = (Contact) object;
                //if (client.active) {
                //String name = connectedClient.getName();
                //nameArray.add(name);
                if (!isClientInContactArray(client)) {
                    contactArray.add(client);
                }
                uppdateConnectedClients();

            }
            /*for (ObjectOutputStream OS : outputStreamArray) {
                try {
                    //   System.out.println(nameArray.toString());
                    OS.writeObject(contactArray.clone());
                    OS.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            } */
        } else if (object instanceof String) {
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
