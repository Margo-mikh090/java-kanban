package managers;

import enums.TaskStatus;
import exceptions.NotFoundException;
import exceptions.TimeIntersectionException;
import exceptions.UpdateEpicTimeException;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int nextID = 1;
    protected Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparing(Task::getId));
    }

    @Override
    public Task addTask(Task task) {
        if (!isAvailableTaskDuration(task)) {
            throw new TimeIntersectionException("Данная задача пересекается по времени с уже существующей");
        }
        prioritizedTasks.add(task);
        task.setId(nextID++);
        tasks.put(task.getId(), task);

        return tasks.get(task.getId());
    }

    @Override
    public Epic addEpic(Epic epic) throws UpdateEpicTimeException {
        epic.setId(nextID++);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());

        return epics.get(epic.getId());
    }

    @Override
    public Subtask addSubtask(Subtask subtask) throws UpdateEpicTimeException {
        if (!isAvailableTaskDuration(subtask)) {
            throw new TimeIntersectionException("Данная задача пересекается по времени с уже существующей");
        }
        subtask.setId(nextID++);
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        List<Integer> subtasksIDs = epics.get(subtask.getEpicID()).getSubtaskIDs();
        Epic epic = epics.get(subtask.getEpicID());
        subtasksIDs.add(subtask.getId());
        epic.setSubtaskIDs(subtasksIDs);
        updateEpicStatus(subtask.getEpicID());
        updateEpicTime(epic.getId());
        epics.put(subtask.getEpicID(), epic);

        return subtasks.get(subtask.getId());
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            throw new NotFoundException("Задача с id=" + task.getId() + " не найдена");
        }
        if (!isAvailableTaskDuration(task)) {
            throw new TimeIntersectionException("Данная задача пересекается по времени с уже существующей");
        }
        prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(task));
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);

        return tasks.get(task.getId());
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws UpdateEpicTimeException {
        if (subtasks.get(subtask.getId()) == null) {
            throw new NotFoundException("Подзадача с id=" + subtask.getId() + " не найдена");
        }
        if (!isAvailableTaskDuration(subtask)) {
            throw new TimeIntersectionException("Данная задача пересекается по времени с уже существующей");
        }
        prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(subtask));
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        Epic epic = epics.get(subtask.getEpicID());
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());

        return subtasks.get(subtask.getId());
    }

    @Override
    public Epic updateEpic(Epic epic) throws UpdateEpicTimeException {
        if (epics.get(epic.getId()) == null) {
            throw new NotFoundException("Эпик с id=" + epic.getId() + " не найден");
        }
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());

        return epics.get(epic.getId());
    }

    @Override
    public void removeTask(int id) {
        if (tasks.get(id) == null) {
            throw new NotFoundException("Задача с id=" + id + " не найдена");
        }
        historyManager.remove(id);
        prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(tasks.get(id)));
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(int id) throws UpdateEpicTimeException {
        if (subtasks.get(id) == null) {
            throw new NotFoundException("Подзадача с id=" + id + " не найдена");
        }
        historyManager.remove(id);
        Subtask removedSubtask = subtasks.remove(id);
        Epic epic = epics.get(removedSubtask.getEpicID());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());
        prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(removedSubtask));
    }

    @Override
    public void removeEpic(int id) {
        if (epics.get(id) == null) {
            throw new NotFoundException("Эпик с id=" + id + " не найден");
        }
        for (Integer subtaskID : epics.get(id).getSubtaskIDs()) {
            historyManager.remove(subtaskID);
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(subtasks.get(subtaskID)));
            subtasks.remove(subtaskID);
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) == null) {
            throw new NotFoundException("Задача с id=" + id + " не найдена");
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.get(id) == null) {
            throw new NotFoundException("Подзадача с id=" + id + " не найдена");
        }
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) == null) {
            throw new NotFoundException("Эпик с id=" + id + " не найден");
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpicID(int id) {
        if (epics.get(id) == null) {
            throw new NotFoundException("Эпик с id=" + id + " не найден");
        }
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicID() == id)
                .toList();
    }

    @Override
    public List<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(task));
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() throws UpdateEpicTimeException {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(subtask));
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(subtask));
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean isAvailableTaskDuration(Task task) {
        if (task.getStartTime() != null && !prioritizedTasks.isEmpty()) {
            LocalDateTime startTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();
            Set<Task> timeCheck = prioritizedTasks.stream()
                    .filter(taskFromSet -> (taskFromSet.getStartTime().isAfter(endTime)
                            || taskFromSet.getEndTime().isBefore(startTime)
                            || taskFromSet.getEndTime().equals(startTime)
                            || taskFromSet.getStartTime().equals(endTime)
                            || taskFromSet.equals(task)))
                    .collect(Collectors.toSet());
            return timeCheck.size() == prioritizedTasks.size();
        } else return prioritizedTasks.isEmpty() && task.getStartTime() != null;
    }

    private void updateEpicTime(int epicID) throws UpdateEpicTimeException {
        Epic epic = epics.get(epicID);
        if (!epic.getSubtaskIDs().isEmpty()) {
            LocalDateTime epicStartTime = epic.getSubtaskIDs().stream()
                    .map(subtasks::get)
                    .min(Comparator.comparing(Subtask::getStartTime))
                    .orElseThrow(() -> new UpdateEpicTimeException("Не удалось обновить время эпика")).getStartTime();
            LocalDateTime epicEndTime = epic.getSubtaskIDs().stream()
                    .map(subtasks::get)
                    .max(Comparator.comparing(Subtask::getEndTime))
                    .orElseThrow(() -> new UpdateEpicTimeException("Не удалось обновить время эпика")).getEndTime();
            epic.setStartTime(epicStartTime);
            epic.setDuration(Duration.between(epicStartTime, epicEndTime));
        }
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
