package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewedTasks = new ArrayList<>();
    private static final int MAX_SIZE_LIST_OF_VIEWS = 10;

    @Override
    public List<Task> getHistory() {
        return viewedTasks;
    }

    @Override
    public void add(Task task) {
        if (viewedTasks.size() == MAX_SIZE_LIST_OF_VIEWS) {
            viewedTasks.removeFirst();
        }
        viewedTasks.add(task);
    }
}
