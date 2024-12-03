package tasks;

import org.junit.jupiter.api.Test;
import statuses.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksEqualWhenIDsEqual() {
        Task task1 = new Task(1, "Test Task1", "Test Task1 description", TaskStatus.NEW);
        Task task2 = new Task(1, "Test Task2", "Test Task2 description", TaskStatus.DONE);
        assertEquals(task1, task2, "Задачи не равны");
    }
}