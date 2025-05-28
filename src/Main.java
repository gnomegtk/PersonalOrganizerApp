package main;

import javax.swing.*;
import view.TaskView;
import view.CalendarView;
import util.Messages;
import java.awt.BorderLayout;
import service.AppointmentAlertManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(Messages.get("app.title"));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab(Messages.get("tab.tasks"), new TaskView());
            tabbedPane.addTab(Messages.get("tab.calendar"), new CalendarView());

            frame.add(tabbedPane, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Start the alert manager to check for upcoming appointments.
            new AppointmentAlertManager();
        });
    }
}
