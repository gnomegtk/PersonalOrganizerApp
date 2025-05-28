package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import service.AppointmentService;
import model.Task;
import com.toedter.calendar.JDateChooser;
import util.Messages;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskView extends JPanel {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JDateChooser startDateChooser, endDateChooser;
    private JSpinner startTimeSpinner, endTimeSpinner;
    private JButton addButton, deletePastButton;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private AppointmentService service;
    private SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public TaskView() {
        service = AppointmentService.getInstance();
        service.addListener(() -> SwingUtilities.invokeLater(() -> loadAppointments()));

        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(Messages.get("label.title")), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        titleField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(titleField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(Messages.get("label.description")), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        JScrollPane scrollDesc = new JScrollPane(descriptionArea);
        scrollDesc.setPreferredSize(new Dimension(300, 80));
        formPanel.add(scrollDesc, gbc);

        // Start Date and Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel(Messages.get("label.start")), gbc);
        gbc.gridx = 1;
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString(Messages.get("date.format.date"));
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
        endDateChooser.setDateFormatString(Messages.get("date.format.date"));
        endDateChooser.setPreferredSize(new Dimension(150, 30));
        endPanel.add(endDateChooser);
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        endTimeSpinner.setPreferredSize(new Dimension(60, 30));
        endPanel.add(endTimeSpinner);
        formPanel.add(endPanel, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        addButton = new JButton(Messages.get("button.add"));
        deletePastButton = new JButton(Messages.get("button.deletePast"));
        buttonPanel.add(addButton);
        buttonPanel.add(deletePastButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Table for appointments
        tableModel = new DefaultTableModel(new String[]{"ID", Messages.get("label.title"), Messages.get("label.start")}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // Double-click row to edit appointment
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = taskTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        int id = (int) tableModel.getValueAt(row, 0);
                        Task t = service.getTaskById(id);
                        if (t != null) {
                            new EditAppointmentDialog(null, t).setVisible(true);
                        }
                    }
                }
            }
        });

        addButton.addActionListener(e -> addAppointment());
        deletePastButton.addActionListener(e -> service.deletePastAppointments());

        clearForm();
        loadAppointments();
    }

    private void addAppointment() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        Date startDate = startDateChooser.getDate();
        Date startTime = (Date) startTimeSpinner.getValue();
        Date endDate = endDateChooser.getDate();
        Date endTime = (Date) endTimeSpinner.getValue();

        if (title.isEmpty() || startDate == null || startTime == null || endDate == null || endTime == null) {
            JOptionPane.showMessageDialog(this, Messages.get("error.fillAllFields"));
            return;
        }

        Calendar now = Calendar.getInstance();
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startDate);
        calStart.set(Calendar.HOUR_OF_DAY, startTime.getHours());
        calStart.set(Calendar.MINUTE, startTime.getMinutes());

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endDate);
        calEnd.set(Calendar.HOUR_OF_DAY, endTime.getHours());
        calEnd.set(Calendar.MINUTE, endTime.getMinutes());

        if (calStart.before(now)) {
            JOptionPane.showMessageDialog(this, Messages.get("msg.error.startPast"));
            return;
        }

        if (startDate.equals(endDate) && startTime.equals(endTime)) {
            JOptionPane.showMessageDialog(this, Messages.get("msg.error.invalidTime"));
            return;
        }

        String startStr = sdfDateTime.format(calStart.getTime());
        String endStr = sdfDateTime.format(calEnd.getTime());

        service.addTask(title, description, startStr, endStr);
        clearForm();
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        Date now = new Date();
        startDateChooser.setDate(now);
        endDateChooser.setDate(now);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        Date roundedTime = cal.getTime();

        startTimeSpinner.setValue(roundedTime);
        endTimeSpinner.setValue(roundedTime);
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        List<Task> tasks = service.getTasks();
        // Use locale-specific date format, read from messages (e.g., date.format.datetime)
        String localizedFormatPattern = Messages.get("date.format.datetime");
        SimpleDateFormat localizedFormat = new SimpleDateFormat(localizedFormatPattern);
        // The stored format is "yyyy-MM-dd HH:mm"
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Task t : tasks) {
            String formattedDate = t.getStart();
            try {
                Date date = originalFormat.parse(t.getStart());
                formattedDate = localizedFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tableModel.addRow(new Object[]{t.getId(), t.getTitle(), formattedDate});
        }
    }
}
