package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, Subtask> subtasksMap = new HashMap<>();

    public Map<Integer, Task> getTasksMap() {
        return tasksMap;
    }

    public Map<Integer, Epic> getEpicsMap() {
        return epicsMap;
    }

    public Map<Integer, Subtask> getSubtasksMap() {
        return subtasksMap;
    }
}
