/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author olda4871
 */
class MainWindow extends JFrame implements ActionListener {
    
    private final JPanel centerPanel;
    private final JButton SERVER_START_BUTTON;
    private final JButton CLIENT_START_BUTTON;
    private final JButton EXIT_BUTTON;

    MainWindow() {
        setLayout(new GridBagLayout());
        setSize(400, 300);
        setTitle("Chat program");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerPanel = new JPanel();
        add(centerPanel,new GridBagConstraints());
        SERVER_START_BUTTON = new JButton("Start server");
        CLIENT_START_BUTTON = new JButton("Start client");
        EXIT_BUTTON = new JButton("Exit program");
        centerPanel.add(SERVER_START_BUTTON);
        centerPanel.add(CLIENT_START_BUTTON);
        centerPanel.add(EXIT_BUTTON);
        SERVER_START_BUTTON.addActionListener(this);
        CLIENT_START_BUTTON.addActionListener(this);
        EXIT_BUTTON.addActionListener(this);
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
