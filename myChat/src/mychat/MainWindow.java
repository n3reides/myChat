
package mychat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class MainWindow extends JFrame implements ActionListener {

    MainWindow() {
        setLayout(new GridBagLayout());
        setSize(400, 300);
        setTitle("Chat program");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel centerPanel = new JPanel();
        add(centerPanel,new GridBagConstraints());
        JButton serverStartButton = new JButton("Start server"); 
        JButton clientStartButton = new JButton("Start client");
        JButton exitButton = new JButton("Exit program");
        centerPanel.add(serverStartButton);
        centerPanel.add(clientStartButton);
        centerPanel.add(exitButton);
        serverStartButton.addActionListener(this);
        clientStartButton.addActionListener(this);
        exitButton.addActionListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (((JButton) (ae.getSource())).getText().equals("Start server")) {
            StartServerWindow serverWindow = new StartServerWindow();
        } else if (((JButton) (ae.getSource())).getText().equals("Start client")) {
            try {
                StartClientWindow clientWindow = new StartClientWindow();
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (((JButton) (ae.getSource())).getText().equals("Exit program")) {
            dispose();
        }
    }   
}