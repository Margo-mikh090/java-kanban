package managers;

import enums.TaskStatus;
import exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static final File file = new File("./fileBacked.csv");;
    private static TaskManager fileBackedTaskManager;
    Task addedTask1;
    Epic addedEpic1;
    Subtask addedSubtask1;
    Task addedTask2;
    Epic addedEpic2;
    Subtask addedSubtask2;

    public FileBackedTaskManagerTest() throws IOException {
        super(FileBackedTaskManager.loadFromFile(File.createTempFile("testFile", ".tmp")));
    }

    @BeforeEach
    public void BeforeEach() {
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        addedTask1 = fileBackedTaskManager.addTask(new Task("Task", "Task", TaskStatus.NEW, "09.02.25 11:00", 180));
        addedEpic1 = fileBackedTaskManager.addEpic(new Epic("Epic", "Epic"));
        addedSubtask1 = fileBackedTaskManager.addSubtask(new Subtask("Subtask",
                "Subtask", TaskStatus.IN_PROGRESS, addedEpic1.getId(), "07.02.25 11:00", 180));
        addedTask2 = fileBackedTaskManager.addTask(new Task("Task", "Task", TaskStatus.NEW, "06.02.25 11:00", 180));
        addedEpic2 = fileBackedTaskManager.addEpic(new Epic("Epic", "Epic"));
        addedSubtask2 = fileBackedTaskManager.addSubtask(new Subtask("Subtask",
                "Subtask", TaskStatus.IN_PROGRESS, addedEpic1.getId(), "10.02.25 11:00", 180));
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
        fileBackedTaskManager.addTask(new Task("Task", "Task", TaskStatus.IN_PROGRESS, "08.02.25 11:00", 180));
        assertEquals(1, fileBackedTaskManager.getListOfTasks().size());
        fileBackedTaskManager.removeAllTasks();
    }

    @Test
    void testLoadNullOrUnknownFile() {
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(new File("")));
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(new File("hamburger")));
    }
}
