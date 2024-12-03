package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    List<Subtask> getSubtasksByEpicID(int id);

    List<Task> getListOfTasks();

    List<Subtask> getListOfSubtasks();

    List<Epic> getListOfEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    List<Task> getHistory();
}
