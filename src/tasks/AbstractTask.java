package tasks;

import enums.TaskStatus;
import enums.TaskType;

public abstract class AbstractTask {
    protected int id;
    protected final String taskName;
    protected final String taskDescription;
    protected TaskStatus status;

    public AbstractTask(int id, String taskName, String taskDescription, TaskStatus status) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public AbstractTask(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public AbstractTask(String taskName, String taskDescription, TaskStatus status) {
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

    public abstract TaskType getType();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AbstractTask task = (AbstractTask) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
