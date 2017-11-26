
package mychat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


class Server {
    
    ServerSocket serverSocket;
    Socket mySocket;
    ChatParticipant server;

    Server(int port) throws IOException {
        try {
            String name = "SERVER";
            serverSocket = new ServerSocket(port);
            mySocket = serverSocket.accept();
            server = new ChatParticipant(mySocket, name);
        } catch (IOException e) {
            System.out.println("IOException in Server, could not connect");
        } finally {
            serverSocket.close();
        }
    }
    
}
