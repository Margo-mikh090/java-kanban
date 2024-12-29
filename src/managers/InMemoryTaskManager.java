package managers;

import statuses.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextID = 1;

    @Override
    public Task addTask(Task task) {
        task.setId(nextID++);
        tasks.put(task.getId(), task);

        return tasks.get(task.getId());
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(nextID++);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());

        return epics.get(epic.getId());
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(nextID++);
        subtasks.put(subtask.getId(), subtask);
        List<Integer> subtasksIDs = epics.get(subtask.getEpicID()).getSubtaskIDs();
        Epic epic = epics.get(subtask.getEpicID());
        subtasksIDs.add(subtask.getId());
        epic.setSubtaskIDs(subtasksIDs);
        epics.put(subtask.getEpicID(), epic);
        updateEpicStatus(subtask.getEpicID());

        return subtasks.get(subtask.getId());
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);

        return tasks.get(task.getId());
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        updateEpicStatus(epic.getId());

        return subtasks.get(subtask.getId());
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return epics.get(epic.getId());
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        historyManager.remove(id);
        Subtask removedSubtask = subtasks.remove(id);
        Epic epic = epics.get(removedSubtask.getEpicID());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void removeEpic(int id) {
        for (Integer subtaskID : epics.get(id).getSubtaskIDs()) {
            historyManager.remove(subtaskID);
            subtasks.remove(subtaskID);
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public Task getTask(int id) {

        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpicID(int id) {
        List<Subtask> subtasksInEpic = new ArrayList<>();
        for (Integer subtaskID : epics.get(id).getSubtaskIDs()) {
            subtasksInEpic.add(subtasks.get(subtaskID));
        }
        return  subtasksInEpic;
    }

    @Override
    public List<Task> getListOfTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        for (Integer task : tasks.keySet()) {
            listOfTasks.add(tasks.get(task));
        }
        return listOfTasks;
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        List<Subtask> listOfSubtasks = new ArrayList<>();
        for (Integer subtask : subtasks.keySet()) {
            listOfSubtasks.add(subtasks.get(subtask));
        }
        return listOfSubtasks;
    }

    @Override
    public List<Epic> getListOfEpics() {
        List<Epic> listOfEpics = new ArrayList<>();
        for (Integer epic : epics.keySet()) {
            listOfEpics.add(epics.get(epic));
        }
        return listOfEpics;
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
                historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicID) {
        int countSubtasksNEW = 0;
        int countSubtasksDONE = 0;
        int countAllSubtasks = epics.get(epicID).getSubtaskIDs().size();

        for (Integer subtaskID : epics.get(epicID).getSubtaskIDs()) {
            if (subtasks.get(subtaskID).getStatus() == TaskStatus.NEW) {
                countSubtasksNEW++;
            } else if (subtasks.get(subtaskID).getStatus() == TaskStatus.DONE) {
                countSubtasksDONE++;
            }
        }

        if (countSubtasksNEW == countAllSubtasks || countAllSubtasks == 0) {
            Epic epic = epics.get(epicID);
            epic.setStatus(TaskStatus.NEW);
            epics.put(epicID, epic);
        } else if (countSubtasksDONE == countAllSubtasks) {
            Epic epic = epics.get(epicID);
            epic.setStatus(TaskStatus.DONE);
            epics.put(epicID, epic);
        } else {
            Epic epic = epics.get(epicID);
            epic.setStatus(TaskStatus.IN_PROGRESS);
            epics.put(epicID, epic);
        }
    }
}
