package mychat;

import java.io.*;
import java.net.*;

public class MyChat {

    // Olle Dahlstedt & Max Sonebäck
    // Uppgift 5: 2017-11-29

    public static void main(String[] args) throws IOException {
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

        MainWindow mainFrame = new MainWindow();
        
    }
}