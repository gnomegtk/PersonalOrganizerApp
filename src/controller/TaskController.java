package controller;

import service.AppointmentService;
import model.Task;
import util.Messages;
import java.util.List;

/**
 * TaskController manages the logic for creating, updating, and deleting tasks.
 */
public class TaskController {
    private AppointmentService service;

    public TaskController() {
        service = AppointmentService.getInstance();
    }

    public void addTask(String title, String description, String start, String end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException(Messages.get("msg.error.invalidTime"));
        }
        int newId = generateNewTaskId();
        Task task = new Task(newId, title, description, start, end);
        service.addTask(task.getTitle(), task.getDescription(), task.getStart(), task.getEnd());
    }

    public void updateTask(int id, String title, String description, String start, String end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException(Messages.get("msg.error.invalidTime"));
        }
        service.updateTask(id, title, description, start, end);
    }

    public void deleteTask(int id) {
        service.deleteTask(id);
    }

    public void deletePastAppointments() {
        service.deletePastAppointments();
    }

    public List<Task> getTasks() {
        return service.getTasks();
    }

    public List<Task> getTasksByDate(String date) {
        return service.getTasksByDate(date);
    }

    private int generateNewTaskId() {
        List<Task> tasks = service.getTasks();
        return tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1;
    }
}
