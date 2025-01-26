package tasks;

import org.junit.jupiter.api.Test;
import enums.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicsEqualWhenIDsEqual() {
        Epic epic1 = new Epic(1, "Test Epic1", "Test Epic1 description", TaskStatus.NEW);
        Epic epic2 = new Epic(1, "Test Epic2", "Test Epic2 description", TaskStatus.DONE);
        assertEquals(epic1, epic2, "Задачи не равны");
    }
}