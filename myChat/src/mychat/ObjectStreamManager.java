
package mychat;

import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.SwingUtilities;


class ObjectStreamManager {
    
    private final ObjectInputStream theStream;
    private final ObjectStreamListener theListener;
    private final int theNumber;
    private volatile boolean stopped = false;

    public ObjectStreamManager(int number, ObjectInputStream stream, ObjectStreamListener listener) {
        theNumber = number;
        theStream = stream;
        theListener = listener;
        new InnerListener().start();
    }

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

    private class InnerListener extends Thread {

        @Override
        public void run() {
            while (!stopped) {
                try {
                    callback(theStream.readObject(), null);
                } catch (Exception e) {
                    callback(null, e);
                }
            }
            try {
                theStream.close();
            } catch (IOException e) {
            }
        }
    }

    public void closeManager() {
        stopped = true;
    }
    
}