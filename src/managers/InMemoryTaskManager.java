package managers;

import enums.TaskStatus;
import exceptions.TimeIntersectionException;
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
        try {
            if (!isAvailableTaskDuration(task, prioritizedTasks)) {
                throw new TimeIntersectionException("Данная задача пересекается по времени с уже существующей");
            }
            prioritizedTasks.add(task);
            task.setId(nextID++);
            tasks.put(task.getId(), task);
            return tasks.get(task.getId());
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Epic addEpic(Epic epic) {
            epic.setId(nextID++);
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
            try {
                updateEpicTime(epic.getId());
            } catch (NoSuchElementException e) {
                System.out.println("Ошибка расчета времени эпика");
            }
            prioritizedTasks.add(epic);

            return epics.get(epic.getId());
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        try {
            if (!isAvailableTaskDuration(subtask, prioritizedTasks)) {
                throw new TimeIntersectionException("Данная задача пересекается по времени с уже существующей");
            }
            subtask.setId(nextID++);
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            List<Integer> subtasksIDs = epics.get(subtask.getEpicID()).getSubtaskIDs();
            Epic epic = epics.get(subtask.getEpicID());
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(epic));
            subtasksIDs.add(subtask.getId());
            epic.setSubtaskIDs(subtasksIDs);
            updateEpicStatus(subtask.getEpicID());
            try {
                updateEpicTime(epic.getId());
            } catch (NoSuchElementException e) {
                System.out.println("Ошибка расчета времени эпика");
            }
            prioritizedTasks.add(epic);
            epics.put(subtask.getEpicID(), epic);
            return subtasks.get(subtask.getId());
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Task updateTask(Task task) {
        try {
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(task));
            if (!isAvailableTaskDuration(task, prioritizedTasks)) {
                throw new TimeIntersectionException("Данная задача пересекается по времени с уже существующей");
            }
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            return tasks.get(task.getId());
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        try {
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(subtask));
            if (!isAvailableTaskDuration(subtask, prioritizedTasks)) {
                throw new TimeIntersectionException("Данная задача пересекается по времени с уже существующей");
            }
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            Epic epic = epics.get(subtask.getEpicID());
            updateEpicStatus(epic.getId());
            try {
                updateEpicTime(epic.getId());
            } catch (NoSuchElementException e) {
                System.out.println("Ошибка расчета времени эпика");
            }
            return subtasks.get(subtask.getId());
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(epic));
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        try {
            updateEpicTime(epic.getId());
        } catch (NoSuchElementException e) {
            System.out.println("Ошибка расчета времени эпика");
        }
        prioritizedTasks.add(epic);

        return epics.get(epic.getId());
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(tasks.get(id)));
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        historyManager.remove(id);
        Subtask removedSubtask = subtasks.remove(id);
        Epic epic = epics.get(removedSubtask.getEpicID());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
        try {
            updateEpicTime(epic.getId());
        } catch (NoSuchElementException e) {
            System.out.println("Ошибка расчета времени эпика");
        }
        prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(removedSubtask));
    }

    @Override
    public void removeEpic(int id) {
        for (Integer subtaskID : epics.get(id).getSubtaskIDs()) {
            historyManager.remove(subtaskID);
            subtasks.remove(subtaskID);
        }
        historyManager.remove(id);
        prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(epics.get(id)));
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
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicID() == id)
                .toList();
    }

    @Override
    public List<Task> getListOfTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        return subtasks.values().stream().toList();
    }

    @Override
    public List<Epic> getListOfEpics() {
        return epics.values().stream().toList();
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
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(subtask));
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic.getId());
            try {
                updateEpicTime(epic.getId());
            } catch (NoSuchElementException e) {
                System.out.println("Ошибка расчета времени эпика");
            }
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
            prioritizedTasks.removeIf(taskFromSet -> taskFromSet.equals(epic));
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

    private boolean isAvailableTaskDuration(Task task, Set<Task> prioritizedTasks) {
        if (task.getStartTime() != null) {
            LocalDateTime startTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();
            if (!prioritizedTasks.isEmpty()) {
                Set<Task> timeCheck = prioritizedTasks.stream()
                        .filter(taskFromSet -> (taskFromSet.getStartTime().isAfter(startTime)
                                && taskFromSet.getStartTime().isBefore(endTime)
                                || taskFromSet.getEndTime().isBefore(endTime)
                                && taskFromSet.getEndTime().isAfter(startTime)))
                        .filter(taskFromSet -> !taskFromSet.equals(task))
                        .collect(Collectors.toSet());
                return timeCheck.isEmpty();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void updateEpicTime(int epicID) {
        Epic epic = epics.get(epicID);
        if (!epic.getSubtaskIDs().isEmpty()) {
            LocalDateTime epicStartTime = epic.getSubtaskIDs().stream()
                    .map(subtasks::get)
                    .min(Comparator.comparing(Subtask::getStartTime))
                    .orElseThrow().getStartTime();
            LocalDateTime epicEndTime = epic.getSubtaskIDs().stream()
                    .map(subtasks::get)
                    .max(Comparator.comparing(Subtask::getEndTime))
                    .orElseThrow().getEndTime();
            epic.setStartTime(epicStartTime);
            epic.setDuration(Duration.between(epicStartTime, epicEndTime));
        } else {
            epic.setStartTime(LocalDateTime.now());
            epic.setDuration(Duration.ofMinutes(60));
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
