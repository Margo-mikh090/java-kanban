package tasks;
import statuses.TaskStatus;

public class Subtask extends Task {
    private int epicID;

    public Subtask(int id, String taskName, String taskDescription, TaskStatus status, int epicID) {
        super(id, taskName, taskDescription, status);
        this.epicID = epicID;
    }

    public Subtask(String taskName, String taskDescription, TaskStatus status, int epicID) {
        super(taskName, taskDescription, status);
        this.epicID = epicID;
    }

    //Конструктор для теста сабтаска
    public Subtask(int id, String taskName, String taskDescription, TaskStatus status) {
        super(id, taskName, taskDescription, status);
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" + "id=" + getId() +
                ", status=" + getStatus() +
                ", name='" + getTaskName() + '\'' +
                ", description='" + getTaskDescription() + '\'' +
                ", epicID=" + epicID +
                '}';
    }
}
