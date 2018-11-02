
package mychat;

import java.io.*;
import java.net.*;

public class MyChat {

    
    // Max Sonebäck & Olle Dahlstedt
    // Olle Dahlstedt & Max Sonebäck
    // STS2A
    // Uppgift 5: 2017-11-29
    // Vape Nation Chatroom
    // Immortal Technique

    // Comments by Olle <2018-11-01>
    
    // run program, start here
    public static void main(String[] args) throws IOException {
    //    System.out.println(InetAddress.getByName("localhost"));
    
    // this probably doesnt do anything but I'm too afraid to comment it out
        String systemipaddress = "";
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
 
            BufferedReader sc =
            new BufferedReader(new InputStreamReader(url_name.openStream()));
 
            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e)
        {
            systemipaddress = "Cannot Execute Properly";
        }


    // main function just starts the main thread which runs the main window 
        MainWindow mainFrame = new MainWindow();
        
    }
}