import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int nextID = 1;

    public Task addTask(Task task) {
        task.setId(nextID++);
        tasks.put(task.getId(), task);

        return tasks.get(task.getId());
    }

    public Epic addEpic(Epic epic) {
        epic.setId(nextID++);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());

        return epics.get(epic.getId());
    }

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

    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);

        return tasks.get(task.getId());
    }

    public Subtask updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        updateEpicStatus(epic.getId());

        return subtasks.get(subtask.getId());
    }

    public Epic updateEpic(Epic epic) {
        epics.put(epic.getId(), epic); //меняется описание и название
        updateEpicStatus(epic.getId()); //так как заходит новый объект, который заменит старый, и только менеджер
                                        //присваивает статус эпику, то мы присваиваем статус подмененному эпику
        return epics.get(epic.getId());
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask removedSubtask = subtasks.remove(id);
        Epic epic = epics.get(removedSubtask.getEpicID());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    public void removeEpic(int id) {
        for (Integer subtaskID : epics.get(id).getSubtaskIDs()) {
            subtasks.remove(subtaskID);
        }
        epics.remove(id);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public List<Subtask> getSubtasksByEpicID(int id) {
        List<Subtask> subtasksInEpic = new ArrayList<>();
        for (Integer subtaskID : epics.get(id).getSubtaskIDs()) {
            subtasksInEpic.add(subtasks.get(subtaskID));
        }
        return  subtasksInEpic;
    }

    public List<Task> getListOfTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        for (Integer task : tasks.keySet()) {
            listOfTasks.add(tasks.get(task));
        }
        return listOfTasks;
    }

    public List<Subtask> getListOfSubtasks() {
        List<Subtask> listOfSubtasks = new ArrayList<>();
        for (Integer subtask : subtasks.keySet()) {
            listOfSubtasks.add(subtasks.get(subtask));
        }
        return listOfSubtasks;
    }

    public List<Epic> getListOfEpics() {
        List<Epic> listOfEpics = new ArrayList<>();
        for (Integer epic : epics.keySet()) {
            listOfEpics.add(epics.get(epic));
        }
        return listOfEpics;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public  void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic.getId());
        }
    }

    public  void removeAllEpics() {
        subtasks.clear();
        epics.clear();
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

        if (countSubtasksNEW == countAllSubtasks || countAllSubtasks== 0) {
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
