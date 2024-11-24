import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtaskIDs = new ArrayList<>();

    public Epic(int id, String taskName, String taskDescription, ArrayList<Integer> subtaskIDs) {
        super(id, taskName, taskDescription);
        this.subtaskIDs = subtaskIDs;
    }

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }


    public ArrayList<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

    public void setSubtaskIDs(ArrayList<Integer> subtaskIDs) {
        this.subtaskIDs = subtaskIDs;
    }

    @Override
    public String toString() {
        return "Epic{" + "id=" + getId() +
                ", status=" + getStatus() +
                ", name='" + getTaskName() + '\'' +
                ", description='" + getTaskDescription() + '\'' +
                ", subtaskIDs.size=" + subtaskIDs.size() +
                '}';
    }
}
