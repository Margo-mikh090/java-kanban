package tasks;

import enums.TaskStatus;
import enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskIDs = new ArrayList<>();

    public Epic(int id, String taskName, String taskDescription, List<Integer> subtaskIDs) {
        super(id, taskName, taskDescription, TaskStatus.IN_PROGRESS, null, null);
        this.subtaskIDs = subtaskIDs;
    }

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskStatus.IN_PROGRESS, null, null);
    }

    public Epic(int id, String taskName, String taskDescription) {
        super(id, taskName, taskDescription, TaskStatus.IN_PROGRESS, null, null);
    }

    //Конструктор для теста эпика
    public Epic(int id, String taskName, String taskDescription, TaskStatus status) {
        super(id, taskName, taskDescription, status, null, null);
    }

    public void removeSubtask(int id) {
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
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
