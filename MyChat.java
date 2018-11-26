package mychat;



import java.awt.*;

import java.awt.event.*;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.ObjectInputStream;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;

import java.net.Socket;
import java.net.URL;

import java.util.ArrayList;

import java.util.logging.Level;

import java.util.logging.Logger;

import javax.swing.*;

// Uppgift 5 (och lite mer typ)
// Max Sonebäck och Olle Dahlstedt
// STS2A ht2017
// kommenterad av Olle ca 2018-11-25

// värt att läsa är instruktioner för chatten som hjälplärare
// kommentarer till den här koden syftar till förståelse, inte lusläsning
// programmet ska fungera i dess nuvarande form precis som det gjorde förra året

// programmet är byggt med avsikt att du ska kunna hantera det helt och hållet från gränssnittet
// därmed är gränssnittet väldigt utbyggt, mycket mer så än vad som förväntas av eleverna
// om du försöker sätta dig in i programmet, använd gärna det grafiska gränssnittet som underlag och jämför i koden vad som händer när du interagerar med det

// de viktigaste klasserna att hålla koll på för hjälplärare är som vanligt Server, Client och ChatParticipant
// viktigt är att förstå hur Server fungerar med PingThread

// en förenklad modell går som följande:

// väljer du att starta en server så kommer du att göra det via StartServerWindow -> Server 
// Server: i konstruktorn för Server körs PingThread som ligger och lyssnar efter anslutningar
// PingThread hanterar alla INKOMMANDE anslutningar till servern och vidarebefordrar detta till Server
// vad som är inkommande i detta fallet är ju då en Socket! så då har vi en anslutning
// Se PingThreads run()-metod och kommentarer där, för förtydligande över vad som faktiskt görs i den separata tråden
// vad som vidarebefordras är bland annat den socket som öppnas och den arraylist över streammanagers som Server hanterar
// Sedan hanteras alla inkommande object över denna i Server och skickas TILLBAKA via outputstreams
// ett objekt är i det här fallet något som skickas via en socket över en stream
// vi har begränsat oss till att endast skicka Strings och Contacts över våra streams, se Server för mer detaljer

// väljer du att starta en client så kommer du att göra det via StartClientWindow -> Client -> ChatParticipant
// notera att du kan göra det genom gränssnittet även om du redan har startat en server samtidigt
// detta för att din server hanterar sin serverSocket via en annan tråd
// se Client och ChatParticipant för det som huvudsakligen är viktigt

// det finns även instruktioner i själva gränssnittet hur du kan använda det




public class MyChat {

    public static void main(String[] args) throws IOException {
        // En gång i tiden tog vi reda på vad vår IP-adress var genom en print-sats här
        // Numera kan vi se det direkt från servern när vi startar en ny server
        // Koden här nedan gör därmed inget, men ligger kvar för sakens skull
        String systemipaddress = "";
        try {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));
            systemipaddress = sc.readLine().trim();
            //    System.out.println(InetAddress.getByName("localhost"));
        } catch (Exception e) {
            systemipaddress = "Cannot Execute Properly";
        }
        
        // Main-metoden skapar helt enkelt vårt MainWindow, vilket är varifrån hela gränssnittet körs vidare
        MainWindow mainFrame = new MainWindow();
    }
}

// för enkelhetens skull så har jag lagt MainWindow här nedanför också
class MainWindow extends JFrame implements ActionListener {

    MainWindow() {
        setLayout(new GridBagLayout());
        setSize(500, 300);
        setTitle("Chat program");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel centerPanel = new JPanel();
        add(centerPanel, new GridBagConstraints());
        JButton serverStartButton = new JButton("Start server");
        JButton clientStartButton = new JButton("Start client");
        JButton exitButton = new JButton("Exit program");
        JButton instructionsButton = new JButton("Instructions");
        centerPanel.add(serverStartButton);
        centerPanel.add(clientStartButton);
        centerPanel.add(exitButton);
        centerPanel.add(instructionsButton);
        serverStartButton.addActionListener(this);
        clientStartButton.addActionListener(this);
        exitButton.addActionListener(this);
        instructionsButton.addActionListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (((JButton) (ae.getSource())).getText().equals("Start server")) {
            StartServerWindow serverWindow = new StartServerWindow();
            dispose();
        } else if (((JButton) (ae.getSource())).getText().equals("Start client")) {
            try {
                StartClientWindow clientWindow = new StartClientWindow();
                dispose();
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (((JButton) (ae.getSource())).getText().equals("Exit program")) {
            dispose();
        } else if (((JButton) (ae.getSource())).getText().equals("Instructions")) {
            JFrame instructionsFrame = new JFrame();
            instructionsFrame.setSize(400, 300);
            instructionsFrame.setVisible(true);
            instructionsFrame.setLayout(new GridBagLayout());
            instructionsFrame.setResizable(false);
            JPanel instructionsPanel = new JPanel();
            JTextArea instructionsArea = new JTextArea();
            instructionsArea.setEditable(false);
            JScrollPane instructionsScrollPane = new JScrollPane(instructionsArea);
            instructionsPanel.add(instructionsScrollPane, new GridBagConstraints());
            instructionsFrame.add(instructionsPanel, new GridBagConstraints());
            instructionsArea.append("Welcome to our simple chat program" + "\n" + "Below is how to use it:" + "\n" + "1) Start a new server, with a port of your choice." + "\n" + "2) There you can see your IP and set some basic settings for your room." + "\n" + "3) To connect, add a new chatroom through the client windows." + "\n" + "4) A chatroom connection requires the IP of the server host as well as the port." + "\n" + "5) Once you save the chatroom, you can then access it with a screen name from the client window." + "\n" + "6) There are additional options for sorting different chatrooms." + "\n" + "7) To chat with somebody, just have them enter and save your IP and port as a chatroom and go wild." + "\n" + "8) The chatroom name option is simply an identifier for organizational purposes." + "\n" + "\n" + "This program was created in november 2017 by Max Sonebäck and Olle Dahlstedt as a school project.");
            instructionsFrame.setTitle("How to use and credits");
            instructionsFrame.pack();
        }
    }
}











