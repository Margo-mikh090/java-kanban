package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;
import enums.TaskStatus;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private static TaskManager taskManager;

    protected InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldReturnNEWEpicStatusWhenAllSubtasksNEW() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.NEW, epic.getId(), "08.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, epic.getId(), "08.02.25 11:00", 180));

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика вычисляется неверно");
    }

    @Test
    void shouldReturnDONEEpicStatusWhenAllSubtasksDONE() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.DONE, epic.getId(), "08.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.DONE, epic.getId(), "08.02.25 11:00", 180));

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика вычисляется неверно");
    }

    @Test
    void shouldReturnINPROGRESSEpicStatusWhenAllSubtasksInVariableStatuses() {
        final Epic epic = taskManager.addEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        final Subtask subtask1 = taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.NEW, epic.getId(), "08.02.25 11:00", 180));
        final Subtask subtask2 = taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.DONE, epic.getId(), "08.02.25 11:00", 180));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика вычисляется неверно");
    }
}