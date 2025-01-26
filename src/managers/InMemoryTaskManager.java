package managers;

import enums.TaskStatus;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final Storage storage = new Storage();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int nextID = 1;

    @Override
    public Task addTask(Task task) {
        task.setId(nextID++);
        storage.getTasksMap().put(task.getId(), task);

        return storage.getTasksMap().get(task.getId());
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(nextID++);
        storage.getEpicsMap().put(epic.getId(), epic);
        updateEpicStatus(epic.getId());

        return storage.getEpicsMap().get(epic.getId());
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(nextID++);
        storage.getSubtasksMap().put(subtask.getId(), subtask);
        List<Integer> subtasksIDs = storage.getEpicsMap().get(subtask.getEpicID()).getSubtaskIDs();
        Epic epic = storage.getEpicsMap().get(subtask.getEpicID());
        subtasksIDs.add(subtask.getId());
        epic.setSubtaskIDs(subtasksIDs);
        storage.getEpicsMap().put(subtask.getEpicID(), epic);
        updateEpicStatus(subtask.getEpicID());

        return storage.getSubtasksMap().get(subtask.getId());
    }

    @Override
    public Task updateTask(Task task) {
        storage.getTasksMap().put(task.getId(), task);

        return storage.getTasksMap().get(task.getId());
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        storage.getSubtasksMap().put(subtask.getId(), subtask);
        Epic epic = storage.getEpicsMap().get(subtask.getEpicID());
        updateEpicStatus(epic.getId());

        return storage.getSubtasksMap().get(subtask.getId());
    }

    @Override
    public Epic updateEpic(Epic epic) {
        storage.getEpicsMap().put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return storage.getEpicsMap().get(epic.getId());
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        storage.getTasksMap().remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        historyManager.remove(id);
        Subtask removedSubtask = storage.getSubtasksMap().remove(id);
        Epic epic = storage.getEpicsMap().get(removedSubtask.getEpicID());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void removeEpic(int id) {
        for (Integer subtaskID : storage.getEpicsMap().get(id).getSubtaskIDs()) {
            historyManager.remove(subtaskID);
            storage.getSubtasksMap().remove(subtaskID);
        }
        historyManager.remove(id);
        storage.getEpicsMap().remove(id);
    }

    @Override
    public Task getTask(int id) {

        historyManager.add(storage.getTasksMap().get(id));
        return storage.getTasksMap().get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(storage.getSubtasksMap().get(id));
        return storage.getSubtasksMap().get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(storage.getEpicsMap().get(id));
        return storage.getEpicsMap().get(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpicID(int id) {
        List<Subtask> subtasksInEpic = new ArrayList<>();
        for (Integer subtaskID : storage.getEpicsMap().get(id).getSubtaskIDs()) {
            subtasksInEpic.add(storage.getSubtasksMap().get(subtaskID));
        }
        return  subtasksInEpic;
    }

    @Override
    public List<Task> getListOfTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        for (Integer task : storage.getTasksMap().keySet()) {
            listOfTasks.add(storage.getTasksMap().get(task));
        }
        return listOfTasks;
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        List<Subtask> listOfSubtasks = new ArrayList<>();
        for (Integer subtask : storage.getSubtasksMap().keySet()) {
            listOfSubtasks.add(storage.getSubtasksMap().get(subtask));
        }
        return listOfSubtasks;
    }

    @Override
    public List<Epic> getListOfEpics() {
        List<Epic> listOfEpics = new ArrayList<>();
        for (Integer epic : storage.getEpicsMap().keySet()) {
            listOfEpics.add(storage.getEpicsMap().get(epic));
        }
        return listOfEpics;
    }

    @Override
    public void removeAllTasks() {
        for (Task task : storage.getTasksMap().values()) {
                historyManager.remove(task.getId());
        }
        storage.getTasksMap().clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : storage.getSubtasksMap().values()) {
            historyManager.remove(subtask.getId());
        }
        storage.getSubtasksMap().clear();
        for (Epic epic : storage.getEpicsMap().values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        for (Subtask subtask : storage.getSubtasksMap().values()) {
            historyManager.remove(subtask.getId());
        }
        storage.getSubtasksMap().clear();
        for (Epic epic : storage.getEpicsMap().values()) {
            historyManager.remove(epic.getId());
        }
        storage.getEpicsMap().clear();
    }

    @Override
    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicID) {
        int countSubtasksNEW = 0;
        int countSubtasksDONE = 0;
        int countAllSubtasks = storage.getEpicsMap().get(epicID).getSubtaskIDs().size();

        for (Integer subtaskID : storage.getEpicsMap().get(epicID).getSubtaskIDs()) {
            if (storage.getSubtasksMap().get(subtaskID).getStatus() == TaskStatus.NEW) {
                countSubtasksNEW++;
            } else if (storage.getSubtasksMap().get(subtaskID).getStatus() == TaskStatus.DONE) {
                countSubtasksDONE++;
            }
        }

        if (countSubtasksNEW == countAllSubtasks || countAllSubtasks == 0) {
            Epic epic = storage.getEpicsMap().get(epicID);
            epic.setStatus(TaskStatus.NEW);
            storage.getEpicsMap().put(epicID, epic);
        } else if (countSubtasksDONE == countAllSubtasks) {
            Epic epic = storage.getEpicsMap().get(epicID);
            epic.setStatus(TaskStatus.DONE);
            storage.getEpicsMap().put(epicID, epic);
        } else {
            Epic epic = storage.getEpicsMap().get(epicID);
            epic.setStatus(TaskStatus.IN_PROGRESS);
            storage.getEpicsMap().put(epicID, epic);
        }
    }
}
