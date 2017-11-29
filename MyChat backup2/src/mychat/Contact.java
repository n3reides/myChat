
package mychat;

import java.io.Serializable;

class Contact implements Serializable {
    
    private final String CONTACT_NAME;
    private final String CONTACT_IP;
    private final int CONTACT_PORT;

    Contact(String name, String IP, int PORT) {
        CONTACT_NAME = name;
        CONTACT_IP = IP;
        CONTACT_PORT = PORT;
    }
        String getName(){
         return CONTACT_NAME;
    }
        String getIP(){
         return CONTACT_IP;
    }
        int getPort() {
            return CONTACT_PORT;
    }
     
 } 
    