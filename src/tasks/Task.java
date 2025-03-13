package tasks;

import com.google.gson.annotations.Expose;
import enums.TaskStatus;
import enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    @Expose
    private int id;

    @Expose
    private final String taskName;

    @Expose
    private final String taskDescription;

    @Expose
    private TaskStatus status;

    @Expose
    private Duration duration;

    @Expose
    private LocalDateTime startTime;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(int id, String taskName, String taskDescription, TaskStatus status, String startTime, long duration) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.startTime = LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(String taskName, String taskDescription, TaskStatus status, String startTime, long duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return getId() + ","
                + getType() + ","
                + getTaskName() + ","
                + getStatus() + ","
                + getTaskDescription() + ","
                + getDuration().toMinutes() + ","
                + getStartTime().format(DATE_TIME_FORMATTER) + ","
                + getEndTime().format(DATE_TIME_FORMATTER);
    }
}

