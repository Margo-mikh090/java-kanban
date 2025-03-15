package tasks;

import com.google.gson.annotations.Expose;
import enums.TaskStatus;
import enums.TaskType;

public class Subtask extends Task {

    @Expose
    private int epicID;

    public Subtask(int id, String taskName, String taskDescription, TaskStatus status, int epicID, String startTime, Integer duration) {
        super(id, taskName, taskDescription, status, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(String taskName, String taskDescription, TaskStatus status, int epicID, String startTime, Integer duration) {
        super(taskName, taskDescription, status, startTime, duration);
        this.epicID = epicID;
    }

    //Конструктор для теста сабтаска
    public Subtask(int id, String taskName, String taskDescription, TaskStatus status, String startTime, Integer duration) {
        super(id, taskName, taskDescription, status, startTime, duration);
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
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
                + getEndTime().format(DATE_TIME_FORMATTER) + ","
                + getEpicID();
    }
}
