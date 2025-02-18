package tasks;
import enums.TaskStatus;
import enums.TaskType;

public class Subtask extends Task {
    private int epicID;

    public Subtask(int id, String taskName, String taskDescription, TaskStatus status, int epicID, String startTime, long duration) {
        super(id, taskName, taskDescription, status, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(String taskName, String taskDescription, TaskStatus status, int epicID, String startTime, long duration) {
        super(taskName, taskDescription, status, startTime, duration);
        this.epicID = epicID;
    }

    //Конструктор для теста сабтаска
    public Subtask(int id, String taskName, String taskDescription, TaskStatus status, String startTime, long duration) {
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
                + getStartTime().format(formatter) + ","
                + getEndTime().format(formatter) + ","
                + getEpicID();
    }
}
