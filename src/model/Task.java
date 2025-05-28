package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents an appointment/task.
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private String start; // Expected in "yyyy-MM-dd HH:mm" format
    private String end;   // Expected in "yyyy-MM-dd HH:mm" format

    public Task(int id, String title, String description, String start, String end) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getStart() {
        return start;
    }
    public String getEnd() {
        return end;
    }

    // Setters (if needed)
    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStart(String start) {
        this.start = start;
    }
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * Converts the start time string into milliseconds.
     * Returns 0 if parsing fails.
     */
    public long getStartMillis() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(start);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
