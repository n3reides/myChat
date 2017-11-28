package mychat;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class Server implements ObjectStreamListener {

    ArrayList<ObjectOutputStream> outputStreamArray = new ArrayList<>();
    ArrayList<ObjectStreamManager> streamManagerArray = new ArrayList<>();
    ArrayList<Socket> socketArray = new ArrayList<>();
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

    private void sendMessageToAllStreams(String message) {
        for (ObjectOutputStream OS : outputStreamArray) {
            try {
                OS.writeObject(message);
                OS.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        //System.out.println("We are in server OR");
        if (exception == null) {
            if (object instanceof Contact) {
                Contact client = (Contact) object;
                if (!isClientInContactArray(client)) {
                    contactArray.add(client);
                } else {
                    String leaveMessage = client.getName() + " has left the chat room.";
                    sendMessageToAllStreams(leaveMessage);
                }
                uppdateConnectedClients();

            } else if (object instanceof String) {
                String message = (String) object;
                sendMessageToAllStreams(message);

            }
        }

    }
}
