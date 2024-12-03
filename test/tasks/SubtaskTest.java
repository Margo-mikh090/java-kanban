package tasks;

import org.junit.jupiter.api.Test;
import statuses.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void tasksEqualWhenIDsEqual() {
        Subtask subtask1 = new Subtask(1, "Test Task1", "Test Task1 description", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(1, "Test Task2", "Test Task2 description", TaskStatus.DONE);
        assertEquals(subtask1, subtask2, "Задачи не равны");
    }
}