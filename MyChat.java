package mychat;
import java.io.*;
public class MyChat {

    //Olle Dahlstedt & Max Sonebäck
    // Uppgift 4; 2017-11-24
    private static final int PORT = 8191; // 2^13 - 1, PRIME

    public static void main(String[] args) throws IOException {
        boolean SERVER_STARTED = true;
        if (!SERVER_STARTED) {
            Server myServer = new Server(PORT);
            SERVER_STARTED = !SERVER_STARTED;
        } else {
            StartWindow startWindow = new StartWindow();
        }

    }

    /* String IP = lines.get(1);
            int PORT = Integer.parseInt(lines.get(2));
            Client myClient = new Client(IP, PORT); 
         NewContact newcontact = new NewContact();
         newcontact.setVisible(true);*/
}




