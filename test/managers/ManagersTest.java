package managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldReturnInMemoryTaskManagerWhenGetDefault() {
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    void shouldReturnInMemoryHistoryManagerWhenGetDefaultHistory() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }

}