package mychat;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


public class ContactCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if (value instanceof Contact) {
            value = ((Contact) value).getName();
        }
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        return this;
    }
}