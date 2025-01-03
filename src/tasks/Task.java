package tasks;
import statuses.TaskStatus;

public class Task {
    private int id;
    private final String taskName;
    private final String taskDescription;
    private TaskStatus status;


    public Task(int id, String taskName, String taskDescription, TaskStatus status) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public Task(String taskName, String taskDescription, TaskStatus status) {
        this(0, taskName, taskDescription, status);
    }

    public Task(int id, String taskName, String taskDescription) {
        this(id, taskName, taskDescription, TaskStatus.IN_PROGRESS);
    }

    public Task(String taskName, String taskDescription) {
        this(0, taskName, taskDescription, TaskStatus.IN_PROGRESS);
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
        return "tasks.Task{" + "id=" + id +
                ", status=" + status +
                ", name='" + taskName + '\'' +
                ", description='" + taskDescription + '\'' +
                '}';
    }


}
