
package mychat;


class Contact {
    
    final String CONTACT_NAME;
    final String CONTACT_IP;
    final int CONTACT_PORT;

    Contact(String name, String IP, int PORT) {
        CONTACT_NAME = name;
        CONTACT_IP = IP;
        CONTACT_PORT = PORT;
    }
    
    String getName(){
        return CONTACT_NAME;
    }
    
}