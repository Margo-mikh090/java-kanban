package managers;

import enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static File file;
    private static FileBackedTaskManager fileBackedTaskManager;
    Task addedTask1;
    Task addedTask2;
    Epic addedEpic1;
    Epic addedEpic2;
    Subtask addedSubtask1;
    Subtask addedSubtask2;

    @BeforeEach
    public void BeforeEach() {
        file = new File("./fileBacked.csv");
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        addedTask1 = fileBackedTaskManager.addTask(new Task("Task", "Task", TaskStatus.NEW));
        addedEpic1 = fileBackedTaskManager.addEpic(new Epic("Epic", "Epic"));
        addedSubtask1 = fileBackedTaskManager.addSubtask(new Subtask("Subtask",
                "Subtask", TaskStatus.IN_PROGRESS, addedEpic1.getId()));
        addedTask2 = fileBackedTaskManager.addTask(new Task("Task", "Task", TaskStatus.NEW));
        addedEpic2 = fileBackedTaskManager.addEpic(new Epic("Epic", "Epic"));
        addedSubtask2 = fileBackedTaskManager.addSubtask(new Subtask("Subtask",
                "Subtask", TaskStatus.IN_PROGRESS, addedEpic1.getId()));

    }

    @Test
    public void shouldSaveTasksIntoFile() {
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(fileBackedTaskManager.getTask(addedTask1.getId()).getTaskName(),
                newFileBackedTaskManager.getTask(addedTask1.getId()).getTaskName());
        assertEquals(fileBackedTaskManager.getSubtask(addedSubtask1.getId()).getTaskName(),
                newFileBackedTaskManager.getSubtask(addedSubtask1.getId()).getTaskName());
        assertEquals(fileBackedTaskManager.getEpic(addedEpic1.getId()).getTaskName(),
                newFileBackedTaskManager.getEpic(addedEpic1.getId()).getTaskName());
        assertEquals(2, fileBackedTaskManager.getSubtasksByEpicID(addedEpic1.getId()).size());
        assertEquals(2, fileBackedTaskManager.getListOfTasks().size());
        assertEquals(2, fileBackedTaskManager.getListOfEpics().size());
    }

    @Test
    public void shouldDeleteTasksFromFileWhenDelete() {
        fileBackedTaskManager.removeAllTasks();
        fileBackedTaskManager.removeAllEpics();
        fileBackedTaskManager.addTask(new Task("Task", "Task", TaskStatus.IN_PROGRESS));
        assertEquals(1, fileBackedTaskManager.getListOfTasks().size());
        fileBackedTaskManager.removeAllTasks();
    }
}
