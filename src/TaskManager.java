import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int nextID = 1;

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
        ArrayList<Integer> subtasksIDs = refreshSubtasksInEpic(epics.get(subtask.getEpicID()));
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
        refreshSubtasksInEpic(epic);
        updateEpicStatus(epic.getId());

        return subtasks.get(subtask.getId());
    }

    public Epic updateEpic(Epic epic) {
        refreshSubtasksInEpic(epic);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());

        return epics.get(epic.getId());
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        ArrayList<Integer> subtaskIDs = refreshSubtasksInEpic(epics.get(subtasks.get(id).getEpicID()));
        Epic epic = epics.get(subtasks.get(id).getEpicID());
        subtaskIDs.remove(Integer.valueOf(id));
        epic.setSubtaskIDs(subtaskIDs);
        epics.put(subtasks.get(id).getEpicID(), epic);
        updateEpicStatus(subtasks.get(id).getEpicID());
        subtasks.remove(id);
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

    public ArrayList<Subtask> getSubtasksByEpicID(int id) {
        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();
        for (Integer subtaskID : epics.get(id).getSubtaskIDs()) {
            subtasksInEpic.add(subtasks.get(subtaskID));
        }
        return  subtasksInEpic;
    }

    public ArrayList<Task> getListOfTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for (Integer task : tasks.keySet()) {
            listOfTasks.add(tasks.get(task));
        }
        return listOfTasks;
    }

    public ArrayList<Subtask> getListOfSubtasks() {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (Integer subtask : subtasks.keySet()) {
            listOfSubtasks.add(subtasks.get(subtask));
        }
        return listOfSubtasks;
    }

    public ArrayList<Epic> getListOfEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>();
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
    }

    public  void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void removeAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public ArrayList<Integer> refreshSubtasksInEpic(Epic epic) {
        ArrayList<Integer> subtaskIDs = new ArrayList<>();
        for (Integer subtaskID : epic.getSubtaskIDs()) {
            Subtask subtask = subtasks.get(subtaskID);
            subtaskIDs.add(subtask.getId());
        }
        epic.setSubtaskIDs(subtaskIDs);

        return epic.getSubtaskIDs();
    }

    private void updateEpicStatus(int epicID) {
        int subtasksNEW = 0;
        int subtasksDONE = 0;
        int allSubtasks = epics.get(epicID).getSubtaskIDs().size();

        for (Integer subtaskID : epics.get(epicID).getSubtaskIDs()) {
            if (subtasks.get(subtaskID).getStatus() == TaskStatus.NEW) {
                subtasksNEW++;
            } else if (subtasks.get(subtaskID).getStatus() == TaskStatus.DONE) {
                subtasksDONE++;
            }
        }

        if (subtasksNEW == allSubtasks || allSubtasks == 0) {
            Epic epic = epics.get(epicID);
            epic.setStatus(TaskStatus.NEW);
            epics.put(epicID, epic);
        } else if (subtasksDONE == allSubtasks) {
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
