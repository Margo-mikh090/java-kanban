package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;
import statuses.TaskStatus;

import java.util.List;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addTask() {
        final Task task = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW));
        final Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    void addEpicAndSubtasks() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId()));

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
                "Test addNewTask description", TaskStatus.IN_PROGRESS));
        final Task updatedTask = taskManager.updateTask(new Task(initialTask.getId(), "Test updateTask",
                "Test updateTask description", TaskStatus.IN_PROGRESS));
        assertEquals(initialTask, updatedTask, "Задача получила другой id");
    }

    @Test
    void shouldReturnSubtaskAndEpicWithSameIDWhenUpdated() {
        final Epic intialEpic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask initialSubtask = taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.IN_PROGRESS, intialEpic.getId()));
        final Subtask updatedSubtask = taskManager.updateSubtask(new Subtask(initialSubtask.getId(),
                "Test updateSubtask", "Test updateSubtask description",
                TaskStatus.DONE, initialSubtask.getEpicID()));
        final Epic updatedEpic = taskManager.updateEpic(new Epic(intialEpic.getId(), "Test updateEpic",
                "Test updateEpic description", intialEpic.getSubtaskIDs()));
        assertEquals(initialSubtask, updatedSubtask, "Подзадача получила другой id");
        assertEquals(intialEpic, updatedEpic, "Эпик получил другой id");
    }

    @Test
    void shouldReturnNEWEpicStatusWhenAllSubtasksNEW() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.NEW, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId()));

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика вычисляется неверно");
    }

    @Test
    void shouldReturnDONEEpicStatusWhenAllSubtasksDONE() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.DONE, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.DONE, epic.getId()));

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика вычисляется неверно");
    }

    @Test
    void shouldReturnINPROGRESSEpicStatusWhenAllSubtasksInVariableStatuses() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.NEW, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.DONE, epic.getId()));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика вычисляется неверно");
    }

    @Test
    void shouldReturnNullByDeletedTaskID() {
        final Task task = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW));
        taskManager.removeTask(task.getId());
        assertNull(taskManager.getTask(task.getId()), "Задача не удалена");
    }

    @Test
    void shouldReturnNullByDeletedSubtaskID() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId()));
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
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId()));
        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getEpic(epic.getId()), "Эпик не удален");
        assertNull(taskManager.getSubtask(subtask1.getId()));
        assertNull(taskManager.getSubtask(subtask2.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenRemoveAllTasks() {
        final Task task = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW));
        taskManager.removeAllTasks();
        assertTrue(taskManager.getListOfTasks().isEmpty(), "Задачи некорректно удаляются");
    }

    @Test
    void shouldReturnEmptyListWhenDeleteEpics() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId()));
        taskManager.removeAllEpics();
        assertTrue(taskManager.getListOfEpics().isEmpty(), "Эпики некорректно удаляются");
        assertTrue(taskManager.getListOfSubtasks().isEmpty(), "Подзадачи должны быть удалены");
    }

    @Test
    void shouldReturnEmptyListWhenDeleteSubtasks() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId()));
        taskManager.removeAllSubtasks();

        assertTrue(taskManager.getListOfSubtasks().isEmpty(), "Подзадачи некорректно удаляются");
        assertTrue(epic.getSubtaskIDs().isEmpty(), "Подзадачи некорректно удаляются");
    }

    @Test
    void shouldReturnDifferentTasksWhenGivenAndGeneratedID() {
        final Task task1 = taskManager.addTask(new Task(1, "Test addNewTask",
                "Test addNewTask description", TaskStatus.DONE));
        final Task task2 = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.DONE));
        assertNotEquals(task1, task2, "Задачи конфликтуют");
    }
}