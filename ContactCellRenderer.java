
package mychat;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

// kommer inte riktigt ihåg vad den här gör
// men är rätt säker på att den behövs för meny-systemet
// alltså ej särskilt viktig för hjälplärare

class ContactCellRenderer extends DefaultListCellRenderer {
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Contact) {
            value = ((Contact) value).getName();
        }
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        return this;
    }
    
}
