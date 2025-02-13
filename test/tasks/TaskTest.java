package tasks;

import org.junit.jupiter.api.Test;
import enums.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksEqualWhenIDsEqual() {
        Task task1 = new Task(1, "Test Task1", "Test Task1 description", TaskStatus.NEW, "08.02.25 11:00", 180);
        Task task2 = new Task(1, "Test Task2", "Test Task2 description", TaskStatus.DONE, "08.02.25 11:00", 180);
        assertEquals(task1, task2, "Задачи не равны");
    }
}