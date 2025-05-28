package service;

import model.Task;
import database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages appointments by performing CRUD operations on the database
 * and ensures UI listeners are updated whenever data changes.
 */
public class AppointmentService {
    private static AppointmentService instance;
    private List<Task> tasks;
    private List<AppointmentListener> listeners;

    private AppointmentService() {
        DatabaseManager.initializeDatabase();
        tasks = new ArrayList<>();
        listeners = new ArrayList<>();
        loadTasksFromDatabase();
    }

    public static AppointmentService getInstance() {
        if (instance == null) {
            instance = new AppointmentService();
        }
        return instance;
    }

    public void loadTasksFromDatabase() {
        tasks.clear();
        String query = "SELECT * FROM tasks ORDER BY start_time ASC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("start_time"),
                    rs.getString("end_time")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notifyListeners(); // Notifica as telas para atualização automática
    }

    public void addTask(String title, String description, String start, String end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("Start time and end time cannot be the same.");
        }
        String sql = "INSERT INTO tasks (title, description, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, start);
            stmt.setString(4, end);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadTasksFromDatabase(); // Atualiza os dados e notifica os listeners
    }

    public void updateTask(int id, String title, String description, String start, String end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("Start time and end time cannot be the same.");
        }
        String sql = "UPDATE tasks SET title = ?, description = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, start);
            stmt.setString(4, end);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadTasksFromDatabase(); // Atualiza os dados e notifica os listeners
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadTasksFromDatabase(); // Atualiza os dados e notifica os listeners
    }

    public void deletePastAppointments() {
        String sql = "DELETE FROM tasks WHERE start_time < datetime('now')";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadTasksFromDatabase(); // Atualiza os dados e notifica os listeners
    }

    public Task getTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id)
                return task;
        }
        return null;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksByDate(String date) {
        List<Task> filtered = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStart().startsWith(date))
                filtered.add(task);
        }
        return filtered;
    }

    public void addListener(AppointmentListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners() {
        for (AppointmentListener listener : listeners) {
            listener.onAppointmentsChanged();
        }
    }
}
