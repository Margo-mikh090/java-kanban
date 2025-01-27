package tasks;
import enums.TaskStatus;
import enums.TaskType;

public class Task {
    protected int id;
    protected final String taskName;
    protected final String taskDescription;
    protected TaskStatus status;

    public Task(int id, String taskName, String taskDescription, TaskStatus status) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task(String taskName, String taskDescription, TaskStatus status) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
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
                + getTaskDescription();
    }
}

