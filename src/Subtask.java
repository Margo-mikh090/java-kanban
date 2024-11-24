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

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" + "id=" + getId() +
                ", status=" + getStatus() +
                ", name='" + getTaskName() + '\'' +
                ", description='" + getTaskDescription() + '\'' +
                ", epicID=" + epicID +
                '}';
    }
}
