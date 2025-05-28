package service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import model.Task;
import util.Messages;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AppointmentAlertManager periodically checks for upcoming appointments and alerts the user.
 */
public class AppointmentAlertManager {
    private AppointmentService service;
    private Timer timer;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public AppointmentAlertManager() {
        service = AppointmentService.getInstance();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                checkForUpcomingAppointments();
            }
        }, 0, 60000); // Check every minute.
    }

    private void checkForUpcomingAppointments() {
        List<Task> tasks = service.getTasks();
        long now = System.currentTimeMillis();
        for (Task task : tasks) {
            long startMillis = task.getStartMillis(); // Method defined in Task.java
            if (startMillis - now <= 60000 && startMillis - now > 0) {
                JOptionPane.showMessageDialog(null, 
                    String.format(Messages.get("msg.alert.upcoming"), task.getTitle(), "1"));
            } else if (startMillis <= now && startMillis + 60000 > now) {
                JOptionPane.showMessageDialog(null, 
                    String.format(Messages.get("msg.alert.starting"), task.getTitle()));
            }
        }
    }

    public void stop() {
        timer.cancel();
    }
}
