package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import service.AppointmentService;
import model.Task;
import util.Messages;
import com.toedter.calendar.JCalendar;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarView extends JPanel {
    private AppointmentService service;
    private JCalendar calendar;
    private JTable calendarTable;
    private DefaultTableModel tableModel;
    private List<Task> currentTasks;

    public CalendarView() {
        service = AppointmentService.getInstance();
        setLayout(new BorderLayout());

        // Initialize the calendar component
        calendar = new JCalendar();
        // When the day changes, reload the appointments for that date.
        calendar.getDayChooser().addPropertyChangeListener("day", evt -> loadAppointmentsForSelectedDate());
        add(calendar, BorderLayout.NORTH);

        // Use localized column headers instead of literal "Hour" and "Appointments"
        tableModel = new DefaultTableModel(new String[]{Messages.get("label.hour"), Messages.get("label.appointments")}, 24) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Populate the table with rows for each hour (0-23)
        for (int hour = 0; hour < 24; hour++) {
            String hourLabel = String.format("%02d:00 - %02d:59", hour, hour);
            tableModel.setValueAt(hourLabel, hour, 0);
            tableModel.setValueAt("", hour, 1);
        }

        // Create the table and override prepareRenderer to highlight rows with appointments.
        calendarTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                // Check the "appointments" column (index 1) for content.
                String appointText = (String) getValueAt(row, 1);
                if (appointText != null && !appointText.trim().isEmpty()) {
                    comp.setBackground(new Color(200, 255, 200)); // light green when there is at least one appointment
                } else {
                    comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        };
        calendarTable.setRowHeight(30);
        add(new JScrollPane(calendarTable), BorderLayout.CENTER);

        // Double-click event to edit the appointment for that hour.
        calendarTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int row = calendarTable.rowAtPoint(e.getPoint());
                    int hour = row;
                    List<Task> tasksForHour = new ArrayList<>();
                    for (Task t : currentTasks) {
                        try {
                            String startStr = t.getStart(); // Stored format "yyyy-MM-dd HH:mm"
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date startTime = sdf.parse(startStr);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(startTime);
                            if(cal.get(Calendar.HOUR_OF_DAY) == hour) {
                                tasksForHour.add(t);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(tasksForHour.size() == 1) {
                        new EditAppointmentDialog(null, tasksForHour.get(0)).setVisible(true);
                    } else if(tasksForHour.size() > 1) {
                        String[] options = new String[tasksForHour.size()];
                        for (int i = 0; i < tasksForHour.size(); i++) {
                            Task t = tasksForHour.get(i);
                            options[i] = t.getId() + " - " + t.getTitle() + " (" + t.getStart().substring(11) + ")";
                        }
                        String selection = (String) JOptionPane.showInputDialog(CalendarView.this,
                                Messages.get("msg.selectAppointment"),
                                Messages.get("dialog.selectAppointment"),
                                JOptionPane.PLAIN_MESSAGE,
                                null, options, options[0]);
                        if(selection != null) {
                            String idStr = selection.split(" - ")[0];
                            int selectedId = Integer.parseInt(idStr);
                            Task selectedTask = service.getTaskById(selectedId);
                            if(selectedTask != null) {
                                new EditAppointmentDialog(null, selectedTask).setVisible(true);
                            }
                        }
                    }
                }
            }
        });

        // Register this view as a listener to get updated when tasks change.
        service.addListener(() -> SwingUtilities.invokeLater(() -> loadAppointmentsForSelectedDate()));

        loadAppointmentsForSelectedDate();
    }

    public void loadAppointmentsForSelectedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date selectedDate = calendar.getDate();
        String dateStr = sdf.format(selectedDate);
        currentTasks = service.getTasksByDate(dateStr);
        // Clear all appointment cells first.
        for (int row = 0; row < 24; row++) {
            tableModel.setValueAt("", row, 1);
        }
        // Format the appointment times to follow a localized date/time pattern.
        String localizedFormatPattern = Messages.get("date.format.datetime"); // e.g., "dd/MM/yyyy HH:mm" for Portuguese
        SimpleDateFormat localizedFormat = new SimpleDateFormat(localizedFormatPattern);
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Task t : currentTasks) {
            try {
                Date date = originalFormat.parse(t.getStart());
                String formattedDate = localizedFormat.format(date);
                // Get the hour the appointment starts at.
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                String existing = (String) tableModel.getValueAt(hour, 1);
                String apptStr = t.getTitle() + " (" + formattedDate.substring(formattedDate.indexOf(" ") + 1) + ")";
                if (existing == null || existing.isEmpty()) {
                    tableModel.setValueAt(apptStr, hour, 1);
                } else {
                    tableModel.setValueAt(existing + "; " + apptStr, hour, 1);
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
