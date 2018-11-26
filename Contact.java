
package mychat;

import java.io.Serializable;

// Istället för att ha varje 'client' med dessa variabler separerade
// då skulle vi behöva hantera de viktiga saker som ingår här separat över hela programmet
// då har vi en klass Contact varpå objektet Contact lättare kan hanteras
// lite objektorientering, m.a.o

class Contact implements Serializable {
    
    private final String CONTACT_NAME;
    private final String CONTACT_IP;
    private final int CONTACT_PORT;

    Contact(String name, String IP, int PORT) {
        CONTACT_NAME = name;
        CONTACT_IP = IP;
        CONTACT_PORT = PORT;
    }

    String getName() {
        return CONTACT_NAME;
    }

    String getIP() {
        return CONTACT_IP;
    }

    int getPort() {
        return CONTACT_PORT;
    }
    
}
