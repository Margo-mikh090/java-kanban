package tasks;

import org.junit.jupiter.api.Test;
import enums.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void tasksEqualWhenIDsEqual() {
        Subtask subtask1 = new Subtask(1, "Test Task1", "Test Task1 description", TaskStatus.NEW, "08.02.25 11:00", 180);
        Subtask subtask2 = new Subtask(1, "Test Task2", "Test Task2 description", TaskStatus.DONE, "08.02.25 11:00", 180);
        assertEquals(subtask1, subtask2, "Задачи не равны");
    }
}