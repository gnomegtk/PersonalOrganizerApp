package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * AppointmentCellRenderer is a custom cell renderer for displaying appointments.
 * It sets the background to light green if the cell contains appointment text.
 */
public class AppointmentCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String text = (String) value;
        if (text != null && !text.isEmpty()) {
            cell.setBackground(new Color(200, 255, 200)); // light green
        } else {
            cell.setBackground(Color.white);
        }
        return cell;
    }
}
