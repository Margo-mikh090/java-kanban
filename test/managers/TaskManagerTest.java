package managers;

import enums.TaskStatus;
import exceptions.TimeIntersectionException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    void addTask() {
        final Task task = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 11:00", 180));
        final Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpicAndSubtasks() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId(), "09.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(), "10.02.25 11:00", 180));
        final Epic savedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        final Subtask savedSubtask1 = taskManager.getSubtask(subtask1.getId());
        final Subtask savedSubtask2 = taskManager.getSubtask(subtask2.getId());
        assertNotNull(subtask1, "Подзадача не найдена");
        assertNotNull(subtask2, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask1, "Подзадачи не совпадают");
        assertEquals(subtask2, savedSubtask2, "Подзадачи не совпадают");

        final List<Epic> epics = taskManager.getListOfEpics();
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");

        final List<Subtask> subtasksInEpic = taskManager.getSubtasksByEpicID(epic.getId());
        final List<Subtask> subtasks = taskManager.getListOfSubtasks();
        assertNotNull(subtasksInEpic, "Подзадачи эпика не возвращаются");
        assertEquals(2, subtasksInEpic.size(), "Неверное количество подзадач в эпике");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач");
        assertEquals(subtask2, subtasksInEpic.get(1), "Подзадачи не совпадают");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void shouldReturnTaskWithSameIDWhenUpdated() {
        final Task initialTask = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.IN_PROGRESS, "08.02.25 11:00", 180));
        final Task updatedTask = taskManager.updateTask(new Task(initialTask.getId(), "Test updateTask",
                "Test updateTask description", TaskStatus.IN_PROGRESS, "08.02.25 11:00", 180));
        assertEquals(initialTask, updatedTask, "Задача получила другой id");
    }

    @Test
    void shouldReturnSubtaskAndEpicWithSameIDWhenUpdated() {
        final Epic intialEpic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask initialSubtask = taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.IN_PROGRESS, intialEpic.getId(), "05.02.25 17:00", 60));
        final Subtask updatedSubtask = taskManager.updateSubtask(new Subtask(initialSubtask.getId(),
                "Test updateSubtask", "Test updateSubtask description",
                TaskStatus.DONE, initialSubtask.getEpicID(), "05.02.25 17:00", 60));
        final Epic updatedEpic = taskManager.updateEpic(new Epic(intialEpic.getId(), "Test updateEpic",
                "Test updateEpic description", intialEpic.getSubtaskIDs()));
        assertEquals(initialSubtask, updatedSubtask, "Подзадача получила другой id");
        assertEquals(intialEpic, updatedEpic, "Эпик получил другой id");
    }

    @Test
    void shouldReturnNullByDeletedTaskID() {
        final Task task = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 11:00", 180));
        taskManager.removeTask(task.getId());
        assertNull(taskManager.getTask(task.getId()), "Задача не удалена");
    }

    @Test
    void shouldReturnNullByDeletedSubtaskID() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId(), "09.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(), "08.02.25 11:00", 180));
        assertEquals(2, epic.getSubtaskIDs().size());
        taskManager.removeSubtask(subtask1.getId());
        assertNull(taskManager.getSubtask(subtask1.getId()), "Подзадача не удалена");
        assertEquals(1, epic.getSubtaskIDs().size(), "Подзадача не удалена из эпика");
        assertEquals(subtask2.getId(), epic.getSubtaskIDs().get(0), "Подзадача не удалена из эпика");
    }

    @Test
    void shouldReturnNullByDeletedEpicID() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId(), "09.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(), "08.02.25 11:00", 180));
        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getEpic(epic.getId()), "Эпик не удален");
        assertNull(taskManager.getSubtask(subtask1.getId()));
        assertNull(taskManager.getSubtask(subtask2.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenRemoveAllTasks() {
        final Task task = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 11:00", 180));
        taskManager.removeAllTasks();
        assertTrue(taskManager.getListOfTasks().isEmpty(), "Задачи некорректно удаляются");
    }

    @Test
    void shouldReturnEmptyListWhenDeleteEpics() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId(), "09.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(), "08.02.25 11:00", 180));
        taskManager.removeAllEpics();
        assertTrue(taskManager.getListOfEpics().isEmpty(), "Эпики некорректно удаляются");
        assertTrue(taskManager.getListOfSubtasks().isEmpty(), "Подзадачи должны быть удалены");
    }

    @Test
    void shouldReturnEmptyListWhenDeleteSubtasks() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId(), "09.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(), "08.02.25 11:00", 180));
        taskManager.removeAllSubtasks();

        assertTrue(taskManager.getListOfSubtasks().isEmpty(), "Подзадачи некорректно удаляются");
        assertTrue(epic.getSubtaskIDs().isEmpty(), "Подзадачи некорректно удаляются");
    }

    @Test
    void shouldReturnDifferentTasksWhenGivenAndGeneratedID() {
        final Task task1 = taskManager.addTask(new Task(1, "Test addNewTask",
                "Test addNewTask description", TaskStatus.DONE, "09.02.25 11:00", 180));
        final Task task2 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.DONE, "08.02.25 11:00", 180));
        assertNotEquals(task1, task2, "Задачи конфликтуют");
    }

    @Test
    void shouldSaveTasksWithDifferentTypesWhenGetHistory() {
        final Task task1 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 11:00", 180));
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId(),
                "09.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(),
                "09.02.25 08:00", 60));

    }

    @Test
    void shouldBeCorrectValidationWithTimeIntersection() {
        Task task1 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 11:00", 180));
        assertThrows(TimeIntersectionException.class, () -> taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 08:00", 300)));
        Task task3 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 14:00", 60));
        Task task4 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 15:00", 60));
        taskManager.updateTask(new Task(task4.getId(), "Test updateTask",
                "Test addNewTask description", TaskStatus.IN_PROGRESS,
                "08.02.25 15:00", 60));
        Task task5 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 08:00", 180));
        assertThrows(TimeIntersectionException.class, () -> taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 08:00", 180)));
        assertEquals(4, taskManager.getPrioritizedTasks().size());
        Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId(),
                "09.02.25 11:00", 180));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(),
                "09.02.25 08:00", 60));
        assertEquals(6, taskManager.getPrioritizedTasks().size());
        taskManager.removeEpic(epic.getId());
        assertEquals(4, taskManager.getPrioritizedTasks().size());
        taskManager.removeTask(task1.getId());
        assertEquals(3, taskManager.getPrioritizedTasks().size());

    }

    @Test
    void shouldReturnCorrectInformationAboutHistory() {
        final Task task1 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 11:00", 180));
        final Task task2 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 14:05", 60));
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId(),
                "09.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(),
                "09.02.25 08:00", 60));
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask1.getId());
        assertEquals(3, taskManager.getHistory().size());
        taskManager.removeAllEpics();
        assertEquals(1, taskManager.getHistory().size());
    }
}
