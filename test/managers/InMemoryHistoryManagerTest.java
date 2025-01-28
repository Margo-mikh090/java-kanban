package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import enums.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void BeforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldReturnListWithOneTaskWhenTasksEqual() {
        final Task task =  taskManager.addTask(new Task(0, "Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW));
        for (int i = 1; i < 6; i++) {
            taskManager.getTask(task.getId());
        }
        assertEquals(1, taskManager.getHistory().size());
        assertNotEquals(5, taskManager.getHistory().size());
        assertEquals(task.getTaskName(), taskManager.getHistory().getFirst().getTaskName());
    }

    @Test
    void shouldRemoveTasksInHistoryWhenRemoveTasks() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId()));
        final Task task = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW));
        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask2.getId());
        assertEquals(4, taskManager.getHistory().size());
        taskManager.removeTask(task.getId());
        assertEquals(3, taskManager.getHistory().size());
        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldReturnOldTaskVersionInViewedTasks() {
        final Task initialTask = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW));
        taskManager.getTask(initialTask.getId());
        final Task updatedTask = taskManager.updateTask(new Task(initialTask.getId(), "Test updateTask",
                "Test updateTask description", TaskStatus.IN_PROGRESS));
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(initialTask.getTaskName(), taskManager.getHistory().getFirst().getTaskName(),
                "В истории не сохранилась старая версия");
        assertNotEquals(updatedTask.getTaskName(), taskManager.getHistory().getFirst().getTaskName());
    }

    @Test
    void shouldReturnListOfDifferentTypesOfTasksInViewedTasks() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, epic.getId()));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId()));
        final Task task = taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW));
        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask2.getId());
        assertEquals(epic, taskManager.getHistory().get(2));
        assertEquals(task, taskManager.getHistory().get(0));
        assertEquals(subtask2, taskManager.getHistory().get(3));
    }
}