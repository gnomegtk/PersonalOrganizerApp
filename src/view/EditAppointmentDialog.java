package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;
import service.AppointmentService;
import model.Task;
import util.Messages;

/**
 * EditAppointmentDialog allows updating and deleting an appointment.
 * It ensures automatic updates to the grid and calendar upon any change.
 */
public class EditAppointmentDialog extends JDialog {
    private AppointmentService service;
    private Task task;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JDateChooser startDateChooser, endDateChooser;
    private JSpinner startTimeSpinner, endTimeSpinner;
    private JButton updateButton, cancelButton, deleteButton;
    private SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public EditAppointmentDialog(Frame owner, Task task) {
        super(owner, Messages.get("edit.title"), true);
        this.task = task;
        this.service = AppointmentService.getInstance();
        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes UI components.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(Messages.get("label.title")), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(25);
        titleField.setPreferredSize(new Dimension(250, 30));
        formPanel.add(titleField, gbc);

        // Description label and field (optional)
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(Messages.get("label.description")), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(250, 80));
        formPanel.add(descScroll, gbc);

        // Start Date and Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel(Messages.get("label.start")), gbc);
        gbc.gridx = 1;
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString(Messages.get("date.format.date")); // ✅ Ensures correct format
        startDateChooser.setPreferredSize(new Dimension(150, 30));
        startPanel.add(startDateChooser);
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        startTimeSpinner.setPreferredSize(new Dimension(60, 30));
        startPanel.add(startTimeSpinner);
        formPanel.add(startPanel, gbc);

        // End Date and Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel(Messages.get("label.end")), gbc);
        gbc.gridx = 1;
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString(Messages.get("date.format.date")); // ✅ Ensures correct format
        endDateChooser.setPreferredSize(new Dimension(150, 30));
        endPanel.add(endDateChooser);
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        endTimeSpinner.setPreferredSize(new Dimension(60, 30));
        endPanel.add(endTimeSpinner);
        formPanel.add(endPanel, gbc);

        // Buttons: Update, Delete, and Cancel
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        updateButton = new JButton(Messages.get("button.update"));
        deleteButton = new JButton(Messages.get("button.delete"));
        cancelButton = new JButton(Messages.get("button.cancel"));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        updateButton.addActionListener((ActionEvent e) -> updateAppointment());
        deleteButton.addActionListener((ActionEvent e) -> deleteAppointment());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
    }

    /**
     * Populates the fields with the current values of the appointment.
     */
    private void populateFields() {
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            try {
                Date startDate = sdfDateTime.parse(task.getStart());
                Date endDate = sdfDateTime.parse(task.getEnd());
                startDateChooser.setDate(startDate);
                startTimeSpinner.setValue(startDate);
                endDateChooser.setDate(endDate);
                endTimeSpinner.setValue(endDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the appointment details and refreshes the UI.
     */
    private void updateAppointment() {
        String newTitle = titleField.getText().trim();
        String newDescription = descriptionArea.getText().trim();
        Date startDate = startDateChooser.getDate();
        Date startTime = (Date) startTimeSpinner.getValue();
        Date endDate = endDateChooser.getDate();
        Date endTime = (Date) endTimeSpinner.getValue();

        if (newTitle.isEmpty() || startDate == null || startTime == null || endDate == null || endTime == null) {
            JOptionPane.showMessageDialog(this, Messages.get("error.fillAllFields"));
            return;
        }

        if (startDate.equals(endDate) && startTime.equals(endTime)) {
            JOptionPane.showMessageDialog(this, Messages.get("error.invalidTime")); // ✅ Prevents same start & end time
            return;
        }

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startDate);
        calStart.set(Calendar.HOUR_OF_DAY, startTime.getHours());
        calStart.set(Calendar.MINUTE, startTime.getMinutes());

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endDate);
        calEnd.set(Calendar.HOUR_OF_DAY, endTime.getHours());
        calEnd.set(Calendar.MINUTE, endTime.getMinutes());

        String newStart = sdfDateTime.format(calStart.getTime());
        String newEnd = sdfDateTime.format(calEnd.getTime());

        service.updateTask(task.getId(), newTitle, newDescription, newStart, newEnd);
        service.loadTasksFromDatabase(); // ✅ Refresh UI after update
        dispose();
    }

    /**
     * Deletes the appointment and updates all views.
     */
    private void deleteAppointment() {
        int confirmation = JOptionPane.showConfirmDialog(this,
            Messages.get("msg.confirmDelete"),
            Messages.get("dialog.confirmDelete"),
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            service.deleteTask(task.getId());
            service.loadTasksFromDatabase(); // ✅ Remove from UI immediately
            dispose();
        }
    }
}
