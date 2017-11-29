package mychat;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class StartServerWindow extends JFrame implements ActionListener {

    private final JTextField PORT_FIELD;

    StartServerWindow() {
        setLayout(new GridBagLayout());
        setTitle("Start new server");
        setSize(new Dimension(400, 300));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel centerPanel = new JPanel();
        add(centerPanel, new GridBagConstraints());
        centerPanel.setLayout(new GridLayout(0, 1));
        PORT_FIELD = new JTextField();
        PORT_FIELD.setPreferredSize(new Dimension(200, 30));
        centerPanel.add(new JLabel("Choose Port"));
        centerPanel.add(PORT_FIELD);
        PORT_FIELD.setText("8191");
        JButton StartServerButton = new JButton("Start server");
        JButton backButton = new JButton("Back");
        centerPanel.add(StartServerButton);
        centerPanel.add(backButton);
        StartServerButton.addActionListener(this);
        backButton.addActionListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (((JButton) (ae.getSource())).getText().equals("Start server")) {
            try {
                if (PORT_FIELD.getText() != null) {
                    Server newServer = new Server(Integer.parseInt(PORT_FIELD.getText()));
                }
            } catch (IOException ex) {
                Logger.getLogger(StartServerWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (((JButton) (ae.getSource())).getText().equals("Back")) {
            MainWindow newMainWindow = new MainWindow();
            dispose();
        }
    }

}
