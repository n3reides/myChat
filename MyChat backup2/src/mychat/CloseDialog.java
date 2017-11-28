/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


class CloseDialog extends JDialog implements Serializable, ActionListener {
    
    CloseDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 100);
        setResizable(false);
        setTitle("Chat ended :(");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("The other person has closed the connection."), BorderLayout.CENTER);
        this.add(panel);
        JButton okButton = new JButton("OK");
        okButton.setSize(20, 30);
        okButton.addActionListener(this);
        this.add(okButton, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        this.dispose();
    }
    
}
