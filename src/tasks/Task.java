package tasks;
import enums.TaskStatus;
import enums.TaskType;

public class Task extends AbstractTask {
    public Task(int id, String taskName, String taskDescription, TaskStatus status) {
        super(id, taskName, taskDescription, status);
    }

    public Task(String taskName, String taskDescription, TaskStatus status) {
        super(taskName, taskDescription, status);
    }

    @Override
    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public String toString() {
        return getId() + ","
                + getType() + ","
                + getTaskName() + ","
                + getStatus() + ","
                + getTaskDescription();
    }
}
