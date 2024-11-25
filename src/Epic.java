import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    private List<Integer> subtaskIDs = new ArrayList<>();

    public Epic(int id, String taskName, String taskDescription, List<Integer> subtaskIDs) {
        super(id, taskName, taskDescription);
        this.subtaskIDs = subtaskIDs;
    }

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public void removeSubtask(int id){
        subtaskIDs.remove(Integer.valueOf(id));
    }

    public void clearSubtasks() {
        subtaskIDs.clear();
    }

    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

    public void setSubtaskIDs(List<Integer> subtaskIDs) {
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
