/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychat;

import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.SwingUtilities;


class ObjectStreamManager implements ObjectStreamListener {
    
    private final ObjectInputStream theStream;
    private final ObjectStreamListener theListener;
    private final int theNumber;
    private volatile boolean stopped = false;

    public ObjectStreamManager(int number, ObjectInputStream stream, ObjectStreamListener listener) {
        theNumber = number;
        theStream = stream;
        theListener = listener;
        new InnerListener().start(); // start to listen on a new thread.
    }

    // This private method accepts an object/exception pair and forwards them
    // to the callback, including also the manager number. The forwarding is scheduled
    // on the Swing thread through an anonymous inner class.
    private void callback(final Object object, final Exception exception) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (!stopped) {
                    theListener.objectReceived(theNumber, object, exception);
                    if (exception != null) {
                        closeManager();
                    }
                }
            }
        });
    }

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // This is where the actual reading takes place.
    private class InnerListener extends Thread {

        @Override
        public void run() {
            while (!stopped) {
                // as long as no one stopped me
                try {
                    callback(theStream.readObject(), null); // read an object and forward it
                } catch (Exception e) {
                    // if Exception then forward it
                    callback(null, e);
                }
            }
            try {
                // I have been stopped: close stream
                theStream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Stop the manager and close the stream.
     **/
    public void closeManager() {
        stopped = true;
    }
    
} // end of ObjectStreamManager
